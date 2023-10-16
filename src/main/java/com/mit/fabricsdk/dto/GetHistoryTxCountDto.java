package com.mit.fabricsdk.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Haodong Li
 * @date 2023年05月28日 22:01
 */
@Data
public class GetHistoryTxCountDto {
    private String channelName;
    private List<String> xaxis;
    private List<Long> yaxis;
}
