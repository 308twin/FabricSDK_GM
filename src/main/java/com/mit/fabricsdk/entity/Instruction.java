package com.mit.fabricsdk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.annotations.ApiModelProperty;
/**
 * @author Haodong Li
 * @date 2023年05月24日 12:13
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Instruction {
    //    private String generationTime;
//    private String eqDes;
//    private String pid;
//    private String pidDes;
//    private String eLevel;
//    private String valueDisplay;
//    private String des;
//    private String eTime;
//    private String originTime;
//    private String restoreTime;
//    private String ack;
//    private String ackTime;
//    private String user;
    @JsonProperty("generation_time")
    @ApiModelProperty(value = "Generation Time")
    private String generationTime;

    @JsonProperty("EQDes")
    @ApiModelProperty(value = "EQ Description")
    private String eqDes;

    @JsonProperty("PID")
    @ApiModelProperty(value = "PID")
    private String pid;

    @JsonProperty("PIDDes")
    @ApiModelProperty(value = "PID Description")
    private String pidDes;

    @JsonProperty("ELevel")
    @ApiModelProperty(value = "Event Level")
    private String eLevel;

    @JsonProperty("ValueDisplay")
    @ApiModelProperty(value = "Value Display")
    private String valueDisplay;

    @JsonProperty("Des")
    @ApiModelProperty(value = "Description")
    private String des;

    @JsonProperty("ETime")
    @ApiModelProperty(value = "Event Time")
    private String eTime;

    @JsonProperty("OriginTime")
    @ApiModelProperty(value = "Origin Time")
    private String originTime;

    @JsonProperty("RestoreTime")
    @ApiModelProperty(value = "Restore Time")
    private String restoreTime;

    @JsonProperty("Ack")
    @ApiModelProperty(value = "Acknowledgement")
    private String ack;

    @JsonProperty("AckTime")
    @ApiModelProperty(value = "Acknowledgement Time")
    private String ackTime;

    @JsonProperty("User")
    @ApiModelProperty(value = "User")
    private String user;
}
