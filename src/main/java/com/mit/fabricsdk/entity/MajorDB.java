package com.mit.fabricsdk.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import javax.persistence.*;
/**
 * @author Haodong Li
 * @date 2023年05月24日 11:33
 */
@Entity
@Table(name = "major")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MajorDB   {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("generation_time")
    @Column(name = "generation_time", nullable = false)
    @NotNull(message = "GenerationTime cannot be null")
    private String generationTime;

    @JsonProperty("identification_point")
    @Column(name = "identification_point", nullable = false)
    @NotNull(message = "Identification Point cannot be null")
    private String identificationPoint;

    @JsonProperty("device")
    @Column(name = "device", nullable = false)
    @NotNull(message = "Device cannot be null")
    private String device;

    @JsonProperty("description")
    @Column(name = "description", nullable = false)
    @NotNull(message = "Description cannot be null")
    private String description;

    @JsonProperty("event_level")
    @Column(name = "event_level", nullable = false)
    @NotNull(message = "Event Level cannot be null")
    private String eventLevel;

    @JsonProperty("remark")
    @Column(name = "remark", nullable = false)
    private String remark;

}
