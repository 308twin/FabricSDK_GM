/*
 * @Description: 
 * @Version: 2.0
 * @Author: Haodong Li
 * @Date: 2023-07-11 08:55:27
 * @LastEditors: Haodong Li
 * @LastEditTime: 2023-07-27 16:49:37
 */
package com.mit.fabricsdk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpfsFileInfo {
    @JsonProperty("GenerationTime")
    @ApiModelProperty(value = "GenerationTime")
    private String generationTime;

    @JsonProperty("FileName")
    @NotNull(message = "FileName cannot be null")
    private String fileName;

    @JsonProperty("Hash")    
    @NotNull(message = "Hash cannot be null")
    private String hash;

    @JsonProperty("Key")    
    @NotNull(message = "Key cannot be null")
    private String key;

    @JsonProperty("Extension")
    @NotNull(message = "extension cannot be null")
    private String extension;

    @JsonProperty("Account")
    private String account;

    @JsonProperty("Station")
    private String station;

    @JsonProperty("Description")
    private String description;
}
