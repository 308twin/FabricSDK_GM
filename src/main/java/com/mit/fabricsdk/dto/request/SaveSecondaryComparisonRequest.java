package com.mit.fabricsdk.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.dto.BaseRequest;
import com.mit.fabricsdk.entity.Major;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSecondaryComparisonRequest extends BaseRequest {

    @ApiModelProperty(value = "sequence")
    public String sequence;

    @ApiModelProperty(value = "generationTime")
    public String generationTime;

    @ApiModelProperty(value = "type")
    public Integer type;

    @ApiModelProperty(value = "content")
    public String content;

    @ApiModelProperty(value = "sendMessage")
    public String sendMessage;

    @ApiModelProperty(value = "recieveMessage")
    public String recieveMessage;

    @ApiModelProperty(value = "tampering")
    public Boolean tampering;

    
}
