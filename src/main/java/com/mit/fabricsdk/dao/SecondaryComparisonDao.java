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
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
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