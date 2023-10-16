package com.mit.fabricsdk.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Haodong Li
 * @date 2023年05月28日 22:12
 */
public class CommonUtil {
    public static String transTime(long timestamp)
    {
        // 将 timestamp 转换为 LocalDateTime 对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 定义要转换的日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // 格式化 LocalDateTime 对象为字符串
        String formattedDateTime = dateTime.format(formatter);
        return formattedDateTime;
    }
}
