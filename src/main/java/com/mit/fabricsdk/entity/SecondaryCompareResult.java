package com.mit.fabricsdk.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "secondary_compare_result")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SecondaryCompareResult extends BaseEntity {
    public String sequence;

    @Column(name = "channel_name", nullable = false)
    public String channelName;

    @Column(name = "contract_name", nullable = false)
    public String contractName;

    @Column(name = "generation_time", nullable = false)
    public Timestamp generationTime;
    
    public Integer type;
    public String content;

    @Column(name = "send_message", nullable = false)
    public String sendMessage;

    @Column(name = "recieve_message", nullable = false)
    public String recieveMessage;

    @Column(name = "tampering", nullable = false)
    public Boolean tampering;
}
