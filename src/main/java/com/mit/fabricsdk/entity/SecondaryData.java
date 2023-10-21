package com.mit.fabricsdk.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import lombok.Data;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class SecondaryData {
    private String sequence;
    private String message;
    private String generationTime;
    
}
