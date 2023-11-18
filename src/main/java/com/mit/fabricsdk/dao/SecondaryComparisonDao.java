package com.mit.fabricsdk.dao;

import com.cloudant.client.api.Search;
import com.mit.fabricsdk.dto.request.SearchSecondaryComparison;
import com.mit.fabricsdk.dto.request.SearchSecondaryRequest;
import com.mit.fabricsdk.entity.SecondaryCompareResult;
import javax.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecondaryComparisonDao extends PagingAndSortingRepository<SecondaryCompareResult, Long>,
        JpaSpecificationExecutor<SecondaryCompareResult> {
    List<SecondaryCompareResult> findBySequence(String sequence);

    default List<SecondaryCompareResult> findByMultipleFields(String channelName, String contractName, String sequence,
            Timestamp generationTimeFrom, Timestamp generationTimeTo, Integer type) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (channelName != null&&!channelName.isEmpty()) {
                predicates.add(cb.equal(root.get("channelName"), channelName));
            }
            if (contractName != null && !contractName.isEmpty()) {
                predicates.add(cb.equal(root.get("contractName"), contractName));
            }
            if (sequence != null&& !sequence.isEmpty()) {
                predicates.add(cb.equal(root.get("sequence"), sequence));
            }
            if (generationTimeFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("generationTime"), generationTimeFrom));
            }
            if (generationTimeTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("generationTime"), generationTimeTo));
            }
            if (type != null && type!=0) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    default Page<SecondaryCompareResult> findByMultipleFieldsWithPagination(String channelName, String contractName,
            String sequence, Timestamp generationTimeFrom, Timestamp generationTimeTo, Integer type,
            Pageable pageable) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // if (channelName != null) {
            //     predicates.add(cb.equal(root.get("channelName"), channelName));
            // }
            // if (contractName != null) {
            //     predicates.add(cb.equal(root.get("contractName"), contractName));
            // }
            // if (sequence != null) {
            //     predicates.add(cb.equal(root.get("sequence"), sequence));
            // }
            // if (generationTimeFrom != null) {
            //     predicates.add(cb.greaterThanOrEqualTo(root.get("generationTime"), generationTimeFrom));
            // }
            // if (generationTimeTo != null) {
            //     predicates.add(cb.lessThanOrEqualTo(root.get("generationTime"), generationTimeTo));
            // }
            // if (type != null&&type!=0) {
            //     predicates.add(cb.equal(root.get("type"), type));
            // }
            if (channelName != null&&!channelName.isEmpty()) {
                predicates.add(cb.equal(root.get("channelName"), channelName));
            }
            if (contractName != null && !contractName.isEmpty()) {
                predicates.add(cb.equal(root.get("contractName"), contractName));
            }
            if (sequence != null&& !sequence.isEmpty()) {
                predicates.add(cb.equal(root.get("sequence"), sequence));
            }
            if (generationTimeFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("generationTime"), generationTimeFrom));
            }
            if (generationTimeTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("generationTime"), generationTimeTo));
            }
            if (type != null && type!=0) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    default List<SecondaryCompareResult> findByStation(String station) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

        // 添加条件，使channel_name列包含station
        predicates.add(cb.like(root.get("channelName"), "%" + station + "%"));

        // 添加条件，使recieveMessage不等于sendMessage
        predicates.add(cb.notEqual(root.get("recieveMessage"), root.get("sendMessage")));

       // 添加条件，使generationTime在当前时间的五秒内
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveSecondsAgo = now.minusSeconds(500);

        // 将LocalDateTime转换为java.util.Date
        Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date fiveSecondsAgoDate = Date.from(fiveSecondsAgo.atZone(ZoneId.systemDefault()).toInstant());

        // predicates.add(cb.between(root.get("generationTime"), fiveSecondsAgoDate, nowDate));

        return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Query(value = "SELECT * FROM secondary_compare_result WHERE recieve_message <> send_message AND generation_time BETWEEN :fiveSecondsAgo AND :now", nativeQuery = true)
    List<SecondaryCompareResult> findByStationAndTimeNative( @Param("fiveSecondsAgo") Timestamp fiveSecondsAgo, @Param("now") Timestamp now);


    @Query("SELECT scr FROM SecondaryCompareResult scr WHERE scr.channelName LIKE :station AND scr.recieveMessage <> scr.sendMessage AND scr.generationTime BETWEEN :fiveSecondsAgo AND :now")
    List<SecondaryCompareResult> findByStationAndTime(@Param("station") String station, @Param("fiveSecondsAgo") Timestamp fiveSecondsAgo, @Param("now") Timestamp now);
}

// public class SecondaryCompareResultSpecification implements
// Specification<SecondaryCompareResult> {

// private final SearchSecondaryComparison criteria;

// public SecondaryCompareResultSpecification(SearchSecondaryComparison
// criteria) {
// this.criteria = criteria;
// }

// @Override
// public Predicate toPredicate(Root<SecondaryCompareResult> root,
// CriteriaQuery<?> query, CriteriaBuilder cb) {
// List<Predicate> predicates = new ArrayList<>();

// if (criteria.getChannelName() != null) {
// predicates.add(cb.equal(root.get("channelName"), criteria.getChannelName()));
// }
// if (criteria.getContractName() != null) {
// predicates.add(cb.equal(root.get("contractName"),
// criteria.getContractName()));
// }
// if (criteria.getSequence() != null) {
// predicates.add(cb.equal(root.get("sequence"), criteria.getSequence()));
// }
// if (criteria.getGenerationTimeFrom() != null) {
// predicates.add(cb.greaterThanOrEqualTo(root.get("generationTime"),
// criteria.getGenerationTimeFrom()));
// }
// if (criteria.getGenerationTimeTo() != null) {
// predicates.add(cb.lessThanOrEqualTo(root.get("generationTime"),
// criteria.getGenerationTimeTo()));
// }
// if (criteria.getType() != null) {
// predicates.add(cb.equal(root.get("type"), criteria.getType()));
// }

// return cb.and(predicates.toArray(new Predicate[0]));
// }
// }