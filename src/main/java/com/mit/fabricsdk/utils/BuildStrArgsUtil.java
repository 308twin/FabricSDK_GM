package com.mit.fabricsdk.utils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * Description: 传入的类中的每个属性都转换为字符串
 * date: 2023/5/24 14:48
 * @author: Haodong Li
 * @since: JDK 1.8
 */
public class BuildStrArgsUtil {
    public static <T> String[] objectListToString(T obj,String contractName) {
        List<String> resultList = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        resultList.add(contractName);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(obj);
                String fieldValueString = (fieldValue != null) ? fieldValue.toString() : "";
                resultList.add(fieldValueString);
            } catch (IllegalAccessException e) {
                // Handle the exception if needed
            }
        }

        return resultList.toArray(new String[0]);
    }

    public static String jsonTrans(String jsonString){
//        jsonString=jsonString.replace("generation_time","GenerationTime");
//        jsonString=jsonString.replace("IdentificationPoint","identification_point");
//        jsonString=jsonString.replace("Device","device");
//        jsonString=jsonString.replace("Description","description");
//        jsonString=jsonString.replace("EventLevel","event_level");
//        jsonString=jsonString.replace("Remark","remark");
//        jsonString=jsonString.replace("GenerationTime","generation_time");
        jsonString = jsonString.replace("generation_time","GenerationTime");
        jsonString = jsonString.replace("identification_point","IdentificationPoint");
        jsonString = jsonString.replace("device","Device");
        jsonString = jsonString.replace("description","Description");
        jsonString = jsonString.replace("event_level","EventLevel");
        jsonString = jsonString.replace("remark", "Remark");

        return  jsonString;
    }

    public static String jsonToObjectTrans(String jsonString){
        jsonString = jsonString.replace("GenerationTime","generation_time");
        jsonString=jsonString.replace("IdentificationPoint","identification_point");
        jsonString=jsonString.replace("Device","device");
        jsonString=jsonString.replace("Description","description");
        jsonString=jsonString.replace("EventLevel","event_level");
        jsonString=jsonString.replace("Remark","remark");

        return  jsonString;
    }
}
