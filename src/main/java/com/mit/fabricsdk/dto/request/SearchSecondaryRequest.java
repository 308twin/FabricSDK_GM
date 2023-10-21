package com.mit.fabricsdk.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mit.fabricsdk.dto.BaseRequest;

public class SearchSecondaryRequest extends BaseRequest{
    public String sequence;

    public  String[] toJSONString(){
        List<String> resList = new ArrayList<>();
        resList.add("QuerySecondaryDataBySequence");
        resList.add((sequence));
        return resList.toArray(new String[0]);
    }
}
