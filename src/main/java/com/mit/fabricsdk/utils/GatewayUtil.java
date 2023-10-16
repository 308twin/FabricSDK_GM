package com.mit.fabricsdk.utils;

import com.alibaba.fastjson.JSONObject;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.X509Identity;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author Haodong Li
 * @date 2023年05月24日 12:18
 */
public class GatewayUtil {
    public static Gateway buildGateway(String ccp_url, String pk_url, String cert_url) throws Exception{
        //false：关闭本地服务
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "false");
        Path networkConfigPath = Paths.get(ccp_url);
        Path pk_path = Paths.get(pk_url);
        Path cert_path = Paths.get(cert_url);
        X509Certificate certificate = Identities.readX509Certificate(Files.newBufferedReader(cert_path, StandardCharsets.UTF_8));
        PrivateKey privateKey = Identities.readPrivateKey(Files.newBufferedReader(pk_path, StandardCharsets.UTF_8));
        File ccp_file = new File(ccp_url);
        byte[] bytes = new byte[(int) ccp_file.length()];
        FileInputStream fileInputStream = new FileInputStream(ccp_file);
        fileInputStream.read(bytes);
        String ccp_json = new String(bytes);
        JSONObject jsonObject = JSONObject.parseObject(ccp_json);
        String org_name = jsonObject.getJSONObject("client").getString("organization");
        String MSP = jsonObject.getJSONObject("organizations").getJSONObject(org_name).getString("mspid");
        X509Identity x509Identity = Identities.newX509Identity(MSP, certificate, privateKey);
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(x509Identity).networkConfig(networkConfigPath).discovery(true);
        return builder.connect();
    }
//    public static Gateway buildGateway(File ccp_file, String pk, String cert) throws Exception{
//        //false：关闭本地服务
//        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "false");
//        X509Certificate certificate = Identities.readX509Certificate(cert);
//        PrivateKey privateKey = Identities.readPrivateKey(pk);
//        byte[] bytes = new byte[(int) ccp_file.length()];
//        System.out.println(ccp_file.exists());
//        FileInputStream fileInputStream = new FileInputStream(ccp_file);
//        fileInputStream.read(bytes);
//        String ccp_json = new String(bytes);
//        JSONObject jsonObject = JSONObject.parseObject(ccp_json);
//        String org_name = jsonObject.getJSONObject("client").getString("organization");
//        String MSP = jsonObject.getJSONObject("organizations").getJSONObject(org_name).getString("mspid");
//        X509Identity x509Identity = Identities.newX509Identity(MSP, certificate, privateKey);
//        Gateway.Builder builder = Gateway.createBuilder();
//        builder.identity(x509Identity).networkConfig(new FileInputStream(ccp_file)).discovery(true);
//        fileInputStream.close();
//        return builder.connect();
//    };
}
