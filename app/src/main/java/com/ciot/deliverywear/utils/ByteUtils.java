package com.ciot.deliverywear.utils;


/**
 * 字节处理工具类
 *
 * @description:
 * @author: zhenglong.zhu
 * @update: 2018/11/26
 */
public class ByteUtils {
    /**
     * 方法说明：短整形转字节数组
     *
     * @param s 短整型
     * @return 两位的字节数组
     */
    public static byte[] short2bytes(short s) {
        int temp = s;
        byte[] b = new byte[2];
        int len = b.length;
        for (int i = 0; i < len; i++) {
            b[len - 1 - i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return b;
    }

    /**
     * 方法说明：字节数组转短整形
     *
     * @param bytes 两位的字节数组
     * @return 短整型
     */
    public static short bytes2short(byte[] bytes) {
        short s = 0;
        short s0 = (short) (bytes[1] & 0xff);
        short s1 = (short) (bytes[0] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * 方法说明： 整型转字节数组
     *
     * @param i 整型
     * @return 四位的字节数组
     */
    public static byte[] int2bytes(int i) {
        byte[] bt = new byte[4];
        bt[3] = (byte) (0xff & i);
        bt[2] = (byte) ((0xff00 & i) >> 8);
        bt[1] = (byte) ((0xff0000 & i) >> 16);
        bt[0] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    /**
     * 方法说明： 字节数组转整形
     *
     * @param bytes 四位的字节数组
     * @return 整型
     */
    public static int bytes2int(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);
        return num;
    }

    /**
     * 将字节数组转化为字符串
     *
     * @param buffer 字节数组
     * @return 字符串
     */
    public static String byte2hex(byte[] buffer) {
        String h = "";
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }

    /**
     * 校验和 (从prior到crc前一个字节的相加，结果取低八位)
     *
     * @param data
     * @return 校验和
     */

    public static byte getCheck(byte[] data) {
        int sum = 0;
        if (data != null) {
            for (int i = 1; i < data.length - 2; i++) {
                sum += data[i];
            }
        }
        byte bCheck = (byte) (sum & 0xFF);
        return bCheck;
    }

    /**
     * 合并数组
     *
     * @param a
     * @param b
     * @return
     */
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 将一个int数据转为按小端顺序排列的字节数组
     *
     * @param data int数据
     * @return 按小端顺序排列的字节数组
     */
    public static byte[] changeByteSmall(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = {b1, b2, b3, b4};
        return bytes;
    }

    /**
     * 将一个int数据转为按大端顺序排列的字节数组
     *
     * @param data int数据
     * @return 按大端顺序排列的字节数组
     */
    public static byte[] changeByteBig(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = {b4, b3, b2, b1};
        return bytes;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }


    public static byte[] short2Bytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }


    public static short bytes2Short(byte[] b) {
        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }
}
