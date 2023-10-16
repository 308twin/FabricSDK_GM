package com.mit.fabricsdk.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Haodong Li
 * @date 2023年06月02日 12:59
 */
@Entity
@Getter
@Setter
@Table(name = "blockChain_channel")
public class BlockChainChannel {

    @Id
    @Column(name="channel_name")
    private String channelName;

    @Column(name="channel_id")
    private String channelId;
//
//    @Column(name="peer_name")
//    private String peerName;

    @Column(name="creator")
    private String creator;

    @Column(name="target_org")
    private String targetOrg;
}
