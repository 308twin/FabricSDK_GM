package com.mit.fabricsdk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Haodong Li
 * @date 2023年05月26日 17:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitRequest {
    @ApiModelProperty(value = "channelName")
    @NotNull(message = "channelName不能为空")
    private String channelName;

    @ApiModelProperty(value = "orgName")
    @NotNull(message = "orgName不能为空")
    private String orgName;

}
