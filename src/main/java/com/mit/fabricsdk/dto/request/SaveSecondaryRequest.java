package com.mit.fabricsdk.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mit.fabricsdk.dto.BaseRequest;
import com.mit.fabricsdk.entity.SecondaryData;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveSecondaryRequest extends BaseRequest {
    private List<SecondaryData> secondaryData;
}
