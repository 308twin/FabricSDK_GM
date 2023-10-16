package com.mit.fabricsdk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.dto.BaseRequest;
import com.mit.fabricsdk.entity.IpfsFileInfo;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveIpfsFileRequest extends BaseRequest{

    @JsonProperty("IpfsFileInfo")
    private IpfsFileInfo ipfsFileInfo;
}
