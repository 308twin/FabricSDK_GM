package com.mit.fabricsdk.dto.request;

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

/**
 * @author Haodong Li
 * @date 2023年05月26日 12:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveInsRequest extends BaseRequest {
    @ApiModelProperty(value = "instructions")
    List<Instruction> instructions;

    public String[] toJSONString(){
        List<String> resList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(instructions);
            resList.add(jsonString);
            //resList.add(escapeString(jsonString));
            return resList.toArray(new String[0]);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
