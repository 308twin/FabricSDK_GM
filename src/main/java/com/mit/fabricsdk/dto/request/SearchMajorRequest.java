package com.mit.fabricsdk.dto.request;
import com.mit.fabricsdk.dto.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Haodong Li
 * @date 2023年05月24日 14:06
 */
@Data
public class SearchMajorRequest extends BaseRequest {

    @ApiModelProperty(value = "起始时间", example = "2022-01-01T00:00:00")
    @NotNull(message = "起始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2022-12-31T23:59:59")
    @NotNull(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "身份点列表")
    private List<String> identificationPoints;

    @ApiModelProperty(value = "设备列表")
    private List<String> devices;

    @ApiModelProperty(value = "描述列表")
    private List<String> descriptions;

    @ApiModelProperty(value = "事件级别列表")
    private List<String> eventLevels;


    public  String[] toJSONString(){
        List<String> resList = new ArrayList<>();
        resList.add("QueryEvents");
        resList.add((startTime));
        resList.add(endTime);
        resList.add(identificationPoints.stream().map(s -> "\"" + s + "\"")
                .collect(Collectors.toList()).toString());
        resList.add(devices.stream().map(s -> "\"" + s + "\"")
                .collect(Collectors.toList()).toString());
        resList.add(descriptions.stream().map(s -> "\"" + s + "\"")
                .collect(Collectors.toList()).toString());
        resList.add(eventLevels.stream().map(s -> "\"" + s + "\"")
                .collect(Collectors.toList()).toString());
        return resList.toArray(new String[0]);
    }
}
