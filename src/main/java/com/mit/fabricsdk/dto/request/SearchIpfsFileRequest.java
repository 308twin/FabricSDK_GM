/*
 * @Description: 
 * @Version: 2.0
 * @Author: Haodong Li
 * @Date: 2023-07-12 04:35:46
 * @LastEditors: Haodong Li
 * @LastEditTime: 2023-07-28 15:19:24
 */
package com.mit.fabricsdk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.dto.BaseRequest;
import com.mit.fabricsdk.entity.Instruction;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.milagro.amcl.RSA2048.private_key;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchIpfsFileRequest extends BaseRequest {

    @JsonProperty("StartTime")
    @NotNull(message = "startTime cannot be null")
    private String startTime;

    @JsonProperty("EndTime")
    @NotNull(message = "endTime cannot be null")
    private String endTime;
    
    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("Hash")
    private String hash;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("Extension")
    private String extension;

    @JsonProperty("Account")
    private String account;

    @JsonProperty("Station")
    private String station;

    @JsonProperty("Description")
    private String description;

     public  String[] toJSONString(){
        List<String> resList = new ArrayList<>();
        resList.add("QueryFileInfos");
        resList.add(startTime);
        resList.add(endTime);
        resList.add(fileName);
        resList.add(hash);
        resList.add(key);
        resList.add(extension);
        resList.add(account);
        resList.add(station);
        resList.add(description);
        return resList.toArray(new String[0]);
    }
}
