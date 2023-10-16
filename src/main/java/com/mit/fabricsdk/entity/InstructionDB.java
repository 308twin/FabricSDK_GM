package com.mit.fabricsdk.entity;

import lombok.*;
import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
/**
 * @author Haodong Li
 * @date 2023年05月24日 12:13
 */
@Entity
@Table(name = "instruction")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstructionDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("generation_time")
    @JsonFormat(pattern = "yyyy/M/d HH:mm:ss")
    @Column(name = "generation_time", nullable = false)
    private LocalDateTime  generationTime;

    @JsonProperty("EQDes")
    @Column(name = "eq_des", nullable = true)
    private String eqDes;

    @JsonProperty("PID")
    @Column(name = "pid", nullable = true)
    private String pid;

    @JsonProperty("PIDDes")
    @Column(name = "pid_des", nullable = true)
    private String pidDes;

    @JsonProperty("ELevel")
    @Column(name = "e_level", nullable = true)
    private String eLevel;

    @JsonProperty("ValueDisplay")
    @Column(name = "value_display", nullable = true)
    private String valueDisplay;

    @JsonProperty("Des")
    @Column(name = "des", nullable = true)
    private String des;

    @JsonProperty("ETime")
    @JsonFormat(pattern = "yyyy/M/d HH:mm:ss SSS")
    @Column(name = "e_time", nullable = true)
    private LocalDateTime eTime;

    @JsonProperty("OriginTime")
    @Column(name = "origin_time", nullable = true)
    private LocalDateTime originTime;

    @JsonProperty("RestoreTime")
    @Column(name = "restore_time", nullable = true)
    private LocalDateTime restoreTime;

    @JsonProperty("Ack")
    @Column(name = "ack", nullable = true)
    private String ack;

    @JsonProperty("AckTime")
    @Column(name = "ack_time", nullable = true)
    private LocalDateTime ackTime;

    @JsonProperty("User")
    @Column(name = "user", nullable = true)
    private String user;
}
