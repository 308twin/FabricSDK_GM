package com.mit.fabricsdk.entity;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
 * @date 2023年05月24日 11:33
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Major   {

//    private String generationTime;
//    private String identificationPoint;
//    private String device;
//    private String description;
//    private String eventLevel;
//    private String remark;

    @JsonProperty("generation_time")
    @ApiModelProperty(value = "generation_time")
    @NotNull(message = "GenerationTime cannot be null")
    private String generationTime;

    @JsonProperty("identification_point")
    @ApiModelProperty(value = "identification_point")
    @NotNull(message = "Identification Point cannot be null")
    private String identificationPoint;

    @JsonProperty("device")
    @ApiModelProperty(value = "device")
    @NotNull(message = "Device cannot be null")
    private String device;

    @JsonProperty("description")
    @ApiModelProperty(value = "description")
    @NotNull(message = "Description cannot be null")
    private String description;

    @JsonProperty("event_level")
    @ApiModelProperty(value = "event_level")
    @NotNull(message = "Event Level cannot be null")
    private String eventLevel;

    @JsonProperty("remark")
    @ApiModelProperty(value = "remark")
    private String remark;

}
