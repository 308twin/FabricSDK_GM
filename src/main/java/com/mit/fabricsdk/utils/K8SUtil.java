/*
 * @Author: LHD
 * @Date: 2024-01-02 14:19:10
 * @LastEditors: 308twin 790816436@qq.com
 * @LastEditTime: 2024-01-10 16:48:28
 * @Description: 
 * 
 * Copyright (c) 2024 by 308twin@790816436@qq.com, All Rights Reserved. 
 */
package com.mit.fabricsdk.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

public class K8SUtil {
    public static V1Pod podFilter(String namespace,V1PodList list){      
        List<V1Pod> filteredPods = list.getItems().stream()
                .filter(pod -> pod.getMetadata().getName().contains(namespace))
                .collect(Collectors.toList());
        if(filteredPods.size()==0)
            return null;
        return filteredPods.get(0);
    }

    public static String excuteK8SCommand(String namespace, String podName,String command[]) throws Exception {
        // 初始化Kubernetes客户端
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        String res = "";

        // 查询Pods
        V1PodList list = listAllPods();
        if (list.getItems().isEmpty()) {            
            return "没有找到Pods";
        }

        V1Pod pod = podFilter(podName, list);

        // 选择第一个Pod
        String podFullName = pod.getMetadata().getName();      
        boolean tty = true;
        Exec exec = new Exec(client);
        Process proc = exec.exec(namespace, podFullName, command, null, true, true);

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            //System.out.println(output.toString()); 
            res = output.toString();
        } catch (Exception e) {
            throw e;
        }finally {
            proc.destroy();
        } 

        int exitCode = proc.waitFor();
        if(exitCode!=0)
            throw new Exception("执行命令失败"+res);
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
}
