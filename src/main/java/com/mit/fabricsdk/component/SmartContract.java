package com.mit.fabricsdk.component;

import com.mit.fabricsdk.utils.GatewayUtil;

import lombok.Data;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Haodong Li
 * @date 2023年05月24日 12:55
 */

@Data
public class SmartContract {
    private Contract contract;
    private Network network;
    private Gateway gateway;

    @Value("${path.base.ccp}")
    private String ccpUrl;

    @Value("${path.base.pk}")
    private String pkUrl;

    @Value("${path.base.certification}")
    private String certUrl;

    private String networkName = "";
    private String contractName = "";
//    public SmartContract() throws Exception{
//        setGateway(ccpUrl,pkUrl,certUrl);
//        setNetwork("mychannel");
//        setContract("basic");
//    }

    public SmartContract(String ccp_url,String pk_url, String cert_url, String network_name, String contract_name) throws Exception{
        setGateway(ccp_url, pk_url, cert_url);
        setNetwork(network_name);
        setContract(contract_name);
    }
//
//    public SmartContract(File ccp_file , String pk_url, String cert_url, String network_name, String contract_name) throws Exception{
//        setGateway(ccp_file, pk_url, cert_url);
//        setNetwork(network_name);
//        setContract(contract_name);
//    }

    public void setGateway(String ccp_url,String pk_url, String cert_url) throws Exception{
        this.gateway = GatewayUtil.buildGateway(ccp_url, pk_url, cert_url);
    }

//    public void setGateway(File ccp_file,String pk, String cert) throws Exception{
//        this.gateway = GatewayUtils.buildGateway(ccp_file, pk, cert);
//    }

    public void setNetwork(String network_name) {
        this.network = gateway.getNetwork(network_name);
    }

    public void setContract(String contract_name) {
        this.contract = network.getContract(contract_name);
    }

}
