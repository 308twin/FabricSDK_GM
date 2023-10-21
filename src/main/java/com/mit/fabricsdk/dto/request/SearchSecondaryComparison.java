package com.mit.fabricsdk.dto.request;

//import com.google.protobuf.Timestamp;
import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSecondaryComparison {
    private String channelName;
    private String contractName;
    private String sequence;
    private Timestamp generationTimeFrom; // 查询开始时间
    private Timestamp generationTimeTo;   // 查询结束时间
    private Integer type;
    private int page = 0;
    private int size = 10;
    private String sortBy;
    private String sortOrder; // "asc" or "desc"
}
