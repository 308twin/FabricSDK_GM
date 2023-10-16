package com.mit.fabricsdk.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Haodong Li
 * @date 2023年05月24日 12:42
 */
public class RemoveElementUtil {
    public static String[] removeFirstElement(String[] args)
    {
        List<String> list1= Arrays.asList(args);
        List<String> arrList = new ArrayList<String>(list1);
        arrList.remove(0);
        String[] arr=(String[]) arrList.toArray(new String[arrList.size()]);
        return arr;
    }
}
