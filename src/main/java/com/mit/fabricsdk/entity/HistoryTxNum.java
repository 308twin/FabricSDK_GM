package com.mit.fabricsdk.entity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @author Haodong Li
 * @date 2023年05月27日 12:40
 */
@Entity
@Getter
@Setter
@Table(name = "history_tx_num")
public class HistoryTxNum extends  BaseEntity{

    @ApiModelProperty("数量")
    @Column(columnDefinition = "bigint COMMENT '存证数量'")
    private Long num;

    @ApiModelProperty("Channel名称")
    @Column(columnDefinition = "VARCHAR(100) COMMENT 'Channel名称'")
    private String channel;

    @Override
    public String toString(){
        return  new String("\n数量:"+num+"\nChannel名称:"+channel);
    }
}
