package com.mit.fabricsdk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Haodong Li
 * @date 2023年05月26日 22:13
 */
@Data
public class GetChainHeightRequest {
    @ApiModelProperty(value = "channelName")
    @NotNull(message = "channelName不能为空")
    private String channelName;
}
