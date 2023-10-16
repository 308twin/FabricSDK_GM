package com.mit.fabricsdk.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Haodong Li
 * @date 2023年05月24日 14:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest {
    @ApiModelProperty(value = "channel")
    @NotNull(message = "channel不能为空")
    private String channelName;

    @ApiModelProperty(value = "contract")
    @NotNull(message = "contract不能为空")
    private String contractName;


}
