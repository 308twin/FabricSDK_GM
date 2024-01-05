package com.mit.fabricsdk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.milagro.amcl.RSA2048.public_key;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
public class BashUtil {
    /**
     * 执行bash命令并返回结果。
     *
     * @param command 要执行的命令。
     * @return 命令的输出结果。
     * @throws IOException 当执行命令出现问题时抛出。
     * @throws InterruptedException 当等待命令执行结果被中断时抛出。
     */
    public static String executeCommand(String podName,String command[]) throws Exception {
        // 初始化Kubernetes客户端
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        String res = "";

        // 查询Pods
        String namespace = "mx";
        V1PodList list = listAllPods();
        if (list.getItems().isEmpty()) {            
            return "没有找到Pods";
        }

        V1Pod pod = podFilter(podName, list);

        // 选择第一个Pod
        String podFullName = pod.getMetadata().getName();

        // 执行命令
        command = new String[]{"/bin/bash", "-c", "export CORE_PEER_ADDRESS=org1-peer1:7051 && peer chaincode query -C pingliangroadipfschannel -n ipfs  -c  '{\"function\":\"QueryFileInfos\",\"Args\":[\"2000-03-30T14:07:00Z\", \"2090-03-30T14:09:05Z\", \"\", \"\", \"\", \"\", \"\",\"\",\"\"]}'"};
        boolean tty = true;
        Exec exec = new Exec(client);
        Process proc = exec.exec(namespace, podFullName, command, null, true, true);

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            System.out.println(output.toString()); 
            res = output.toString();
        } finally {
            proc.destroy();
        }

        int exitCode = proc.waitFor();
        System.out.println("Exit code: " + exitCode);
        return res;
    }

    public static V1PodList listAllPods() throws Exception{
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null,null,null);
        return list;
        // for (V1Pod item : list.getItems()) {
        //     System.out.println(item.getMetadata().getName());
        // }
    }

    public static V1Pod podFilter(String namespace,V1PodList list){      
        List<V1Pod> filteredPods = list.getItems().stream()
                .filter(pod -> pod.getMetadata().getName().contains("org1-admin-cli"))
                .collect(Collectors.toList());
        if(filteredPods.size()==0)
            return null;
        return filteredPods.get(0);
    }

    public static boolean hasSingleContainer(String namespace, String podName) throws Exception {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api(client);

        // 获取Pod的详情
        V1Pod pod = api.readNamespacedPod(podName, namespace, null); // pretty设置为null

        // 检查容器的数量
        return pod.getSpec().getContainers().size() == 1;
    }

    public static String executeCommandInPod(String namespace, String podName,  String[] command) throws ApiException, IOException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        Exec exec = new Exec(client);
        boolean tty = true;

        Process proc = exec.exec(namespace, podName, command, null, true, tty);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } finally {
            proc.destroy();
        }
    }
    
}
