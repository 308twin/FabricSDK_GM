package com.mit.fabricsdk.utils;

import lombok.AllArgsConstructor;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Haodong Li
 * @date 2023年06月07日 21:46
 */

public class RunableUtil implements Runnable{
    private Thread t;
    private Contract contract;
    private String eventName;
    private String json;

    public RunableUtil(Contract contract, String eventName, String json) {
        this.contract = contract;
        this.eventName = eventName;
        this.json = json;
        System.out.println("Creating Thread" +  eventName );
    }

    public void run() {
        System.out.println("Running Thread" +  eventName );
        try {
            contract.submitTransaction(eventName,json);
        }catch (Exception e) {
            System.out.println("Thread " +  eventName + " interrupted.");
        }
        System.out.println("Thread " +  eventName + " exiting.");
    }

    public String start () {
        System.out.println("Starting " +  eventName );
        if (t == null) {
            t = new Thread (this, eventName);
            t.start ();
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        return currentTime;
    }
}
