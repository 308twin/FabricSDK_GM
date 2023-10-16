package com.mit.fabricsdk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.fabricsdk.component.ChannelInfo;
import com.mit.fabricsdk.component.SmartContract;
import com.mit.fabricsdk.dao.InstructionDao;
import com.mit.fabricsdk.dto.BaseResponse;
import com.mit.fabricsdk.dto.request.SaveInsRequest;
import com.mit.fabricsdk.dto.request.SaveMajorRequest;
import com.mit.fabricsdk.dto.request.SearchInsRequest;
import com.mit.fabricsdk.dto.request.SearchMajorRequest;
import com.mit.fabricsdk.dto.response.AddResponse;
import com.mit.fabricsdk.entity.Instruction;
import com.mit.fabricsdk.entity.InstructionDB;
import com.mit.fabricsdk.entity.Major;
import com.mit.fabricsdk.service.SmartContractService;
import com.mit.fabricsdk.utils.BuildStrArgsUtil;
import com.mit.fabricsdk.utils.RunableUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.jpa.domain.Specification;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import javax.persistence.criteria.Predicate;
@RestController
public class DbController {
    @Autowired
    SmartContractService smartContractService;

    @Autowired
    InstructionDao instructionDao;


    @PostMapping(value = "api/blockchain/instruction/savemysql",produces = "application/json")
    @ApiOperation("BatchAddInstructions")
    public BaseResponse<Object> saveInsDb(@RequestBody @Valid List<InstructionDB> instructionDBs){
        List<String> finishTime = new ArrayList<>();
        for (InstructionDB instructionDB : instructionDBs) {
            instructionDao.save(instructionDB);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(date);
            finishTime.add(currentTime);
        }
        return new BaseResponse<Object>(finishTime, "新增成功");
    }

    @SneakyThrows
    @PostMapping (value = "api/blockchain/instruction/searchmysql",produces = "application/json")
    @ApiOperation("Instruction查找major")
    public BaseResponse<Object> searchInsDb(@RequestBody @Valid SearchInsRequest request){
        
        // Specification<InstructionDB> spec = (root, query, builder) -> {
        //     Instant startTime = Instant.from(formatter.parse(request.getTimeRange().get(0)));
        //     Instant endTime = Instant.from(formatter.parse(request.getTimeRange().get(1)));

        //     // 构建时间段查询条件
        //     if (startTime != null && endTime != null) {
        //         return builder.between(root.get("generationTime"), startTime, endTime);
        //     } else if (startTime != null) {
        //         return builder.greaterThanOrEqualTo(root.get("generationTime"), startTime);
        //     } else if (endTime != null) {
        //         return builder.lessThanOrEqualTo(root.get("generationTime"), endTime);
        //     } else {
        //         return null; // 未提供时间段条件
        //     }
        // };


        Specification<InstructionDB> spec = (root, query, builder) -> {
            //DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTimeRange().get(0) != null && request.getTimeRange().get(1) != null) {
                // 构建第一个时间字段的条件
                //predicates.add(builder.between(root.get("generationTime"), request.getTimeRange().get(0)), request.getTimeRange().get(1));
                LocalDateTime startTime = LocalDateTime.parse(request.getTimeRange().get(0), DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"));
                LocalDateTime endTime = LocalDateTime.parse(request.getTimeRange().get(1), DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"));
                predicates.add(builder.between(root.get("generationTime"), startTime, endTime));
                //predicates.add(builder.between(root.get("generationTime"), request.getTimeRange().get(0), request.getTimeRange().get(1)));
            }

            if (request.getEqDes() != null && !request.getEqDes().isEmpty()) {
                predicates.add(root.get("eqDes").in(request.getEqDes()));
            }
        
            if (request.getPid() != null && !request.getPid().isEmpty()) {
                predicates.add(root.get("pid").in(request.getPid()));
            }
        
            if (request.getPidDes() != null && !request.getPidDes().isEmpty()) {
                predicates.add(root.get("pidDes").in(request.getPidDes()));
            }
        
            if (request.getELevel() != null && !request.getELevel().isEmpty()) {
                predicates.add(root.get("eLevel  ").in(request.getELevel()));
            }
        
            if (request.getValueDisplay() != null && !request.getValueDisplay().isEmpty()) {
                predicates.add(root.get("valueDisplay").in(request.getValueDisplay()));
            }
        
             if (request.getDes() != null && !request.getDes().isEmpty()) {
                predicates.add(root.get("des").in(request.getDes()));
            }
        
            // if (request.getETime()!= null&&request.getETime().size()!=0 && request.getETime().get(0) != null && request.getETime().get(1) != null) {
            //     predicates.add(builder.between(root.get("eTime"), Instant.from(formatter.parse(request.getETime().get(0))), Instant.from(formatter.parse(request.getETime().get(1)))));                
            // }
        
            // if (request.getOriginTime() != null&&request.getOriginTime().size()!=0 &&request.getOriginTime().get(0) != null && request.getOriginTime().get(1) != null) {
            //     predicates.add(builder.between(root.get("originTime"), Instant.from(formatter.parse(request.getOriginTime().get(0))), Instant.from(formatter.parse(request.getOriginTime().get(1)))));                 
            // }
        
            // if (request.getRestoreTime()!= null&&request.getRestoreTime().size()!=0 &&request.getRestoreTime().get(0) != null && request.getRestoreTime().get(1) != null) {
            //     predicates.add(builder.between(root.get("restoreTime"), Instant.from(formatter.parse(request.getRestoreTime().get(0))), Instant.from(formatter.parse(request.getRestoreTime().get(1)))));  
            // }
        
            

            if (request.getAck() != null && !request.getAck().isEmpty()) {
                predicates.add(root.get("ack").in(request.getAck()));
            }

        
            //  if (request.getAckTime()!= null&&request.getAckTime().size()!=0 &&request.getAckTime().get(0) != null && request.getAckTime().get(1) != null) {
            //     predicates.add(builder.between(root.get("ackTime"), Instant.from(formatter.parse(request.getAckTime().get(0))), Instant.from(formatter.parse(request.getAckTime().get(1)))));  
            // }

            if (request.getUser() != null && !request.getUser().isEmpty()) {
                predicates.add(root.get("user").in(request.getUser()));
            }
        
            // 将所有条件组合成一个 AND 条件
           // Predicate finalPredicate = builder.and(predicates.toArray(new Predicate[0]));


            return builder.and(predicates.toArray(new Predicate[0]));
        };

        return new BaseResponse<Object>(instructionDao.findAll(spec), "查询成功");
    }

}
