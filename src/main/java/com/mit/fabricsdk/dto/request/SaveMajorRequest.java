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
/**
 * @author Haodong Li
 * @date 2023年05月25日 15:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMajorRequest extends BaseRequest {
    @ApiModelProperty(value = "majors")
    List<Major> majors;
    public String[] toJSONString(String method){
        List<String> resList = new ArrayList<>();
        resList.add(method);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(majors);
            resList.add(jsonString);
            //resList.add(escapeString(jsonString));
            return resList.toArray(new String[0]);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] toJSONString(){
        List<String> resList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(majors);
            resList.add(jsonString);
            //resList.add(escapeString(jsonString));
            return resList.toArray(new String[0]);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  String escapeString(String jsonString) {
        return jsonString.replace("\"", "\\\"");
    }
}
