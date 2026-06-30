package com.xiaomei.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 业务编号生成器
 * 格式: 前缀 + yyyyMMdd + 4位序号
 */
public class PrimaryGenerater {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
    private static int sequence = 0;
    private static String lastDate = "";

    public static synchronized String generate(String prefix) {
        String today = SDF.format(new Date());
        if (!today.equals(lastDate)) {
            lastDate = today;
            sequence = 0;
        }
        sequence++;
        return prefix + today + String.format("%04d", sequence);
    }
}
