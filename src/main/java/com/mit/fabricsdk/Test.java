package com.mit.fabricsdk;

import com.mit.fabricsdk.entity.Major;

import java.util.List;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mit.fabricsdk.utils.JsonUtil;
import org.springframework.util.Base64Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
/**
 * @author Haodong Li
 * @date 2023年05月31日 21:28
 */
public class Test {
    public static void main(String[] args) {
        String jsonString = "[{\\\"GenerationTime\\\":\\\"2022-03-30T14:08:00Z\\\",\\\"IdentificationPoint\\\":\\\"even22t1\\\",\\\"Device\\\":\\\"dev22ice1\\\",\\\"Description\\\":\\\"description1111\\\",\\\"EventLevel\\\":\\\"level1\\\",\\\"Remark\\\":\\\"remark1\\\"},{\\\"GenerationTime\\\":\\\"2022-03-30T14:08:00Z\\\",\\\"IdentificationPoint\\\":\\\"event2\\\",\\\"Device\\\":\\\"device1\\\",\\\"Description\\\":\\\"descript221ion1\\\",\\\"EventLevel\\\":\\\"level1\\\",\\\"Remark\\\":\\\"remark1\\\"}]";
        jsonString = jsonString.replace("\\","");
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(List.class, Major.class);
        Object majors = JsonUtil.toObjectQuietly(jsonString,  Major[].class);
        System.out.println(majors);
    }
}
