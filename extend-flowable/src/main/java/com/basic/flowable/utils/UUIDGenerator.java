package com.basic.flowable.utils;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * @description UUID 生成器，拼接 IP、JVM 标识、时间与自增计数生成 32 位十六进制唯一标识
 * @author yanbin
 * @date 2023/10/19
 */
public class UUIDGenerator implements Serializable {

    /**
     * @return String 32 位十六进制的 UUID 字符串
     * @description 依次拼接 IP、JVM 标识、高低位时间与自增计数，生成 32 位 UUID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String generate() {
        return new StringBuilder(32)
                .append(format(getIP()))
                .append(format(getJVM()))
                .append(format(getHiTime()))
                .append(format(getLoTime()))
                .append(format(getCount()))
                .toString();
    }

    private static final int IP;

    static {
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    private static short counter = (short) 0;

    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

    /**
     * @return String 8 位零填充的十六进制字符串
     * @description 把整型值格式化为 8 位零填充的十六进制字符串
     * @params [intval] 待格式化的整型值
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static final String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    /**
     * @return String 4 位零填充的十六进制字符串
     * @description 把短整型值格式化为 4 位零填充的十六进制字符串
     * @params [shortval] 待格式化的短整型值
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static final String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    private static final int getJVM() {
        return JVM;
    }

    /**
     * @return short 自增前的计数值
     * @description 在类锁内递增计数器取下一个序号，计数溢出为负时先归零
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static final short getCount() {
        synchronized (UUIDGenerator.class) {
            if (counter < 0) {
                counter = 0;
            }
            return counter++;
        }
    }

    /**
     * Unique in a local network
     */
    private static final int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    private static final short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    private static final int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    /**
     * @return int 合并后的整型值
     * @description 把 IP 地址的 4 个字节合并成一个整型值
     * @params [bytes] IP 地址字节数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static final int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }
}
