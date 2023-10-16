package com.mit.fabricsdk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mit.fabricsdk.dto.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Haodong Li
 * @date 2023年05月26日 12:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchInsRequest extends BaseRequest {

//    @ApiModelProperty(value = "起始时间", example = "2022-01-01T00:00:00")
//    @NotNull(message = "起始时间不能为空")
//    private String startTime;
//
//    @ApiModelProperty(value = "结束时间", example = "2022-12-31T23:59:59")
//    @NotNull(message = "结束时间不能为空")
//    private String endTime;

    @ApiModelProperty("第一个是开始时间，第二个是结束时间。不能为null，可以写空字符串")
    private  List<String > timeRange;
    @ApiModelProperty(value = "EQ Description")
    private List<String> eqDes;


    @ApiModelProperty(value = "PID")
    private List<String> pid;

    @ApiModelProperty(value = "PID Description")
    private List<String> pidDes;

    @ApiModelProperty(value = "Event Level")
    private List<String> eLevel;

    @ApiModelProperty(value = "Value Display")
    private List<String> valueDisplay;

    @ApiModelProperty(value = "Description")
    private List<String> des;

    @JsonProperty("eTime")
    @ApiModelProperty(value = "Event Time")
    private List<String> eTime;

    @ApiModelProperty(value = "Origin Time")
    private List<String> originTime;

    @ApiModelProperty(value = "Restore Time")
    private List<String> restoreTime;

    @ApiModelProperty(value = "Acknowledgement")
    private List<String> ack;

    @ApiModelProperty(value = "Acknowledgement Time")
    private List<String> ackTime;

    @ApiModelProperty(value = "User")
    private List<String> user;


    public String[] toJSONString() {
        List<String> resList = new ArrayList<>();
        resList.add("QueryInstructions");
//        resList.add((startTime));
//        resList.add(endTime);
        resList.add(getQuotedList(timeRange));
        resList.add(getQuotedList(eqDes));
        resList.add(getQuotedList(pid));
        resList.add(getQuotedList(pidDes));
        resList.add(getQuotedList(eLevel));
        resList.add(getQuotedList(valueDisplay));
        resList.add(getQuotedList(des));
        resList.add(getQuotedList(eTime));
        resList.add(getQuotedList(originTime));
        resList.add(getQuotedList(restoreTime));
        resList.add(getQuotedList(ack));
        resList.add(getQuotedList(ackTime));
        resList.add(getQuotedList(user));
        String res[] =  resList.toArray(new String[0]);
        return res;
    }

    private static String getQuotedList(List<String> list) {
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.toList())
                    .toString();
        } else {
            return "[]";
        }
    }
}