/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 通用说明：随机状态码生成器.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/19
 */
public class RandomStatusGenerator {
    private static String valueBeforeMD5 = "";
    private static String valueAfterMD5 = "";
    private static Random myRand;
    private static SecureRandom mySecureRand = new SecureRandom();
    private static String s_id;

    private RandomStatusGenerator() {
    }

    public static String getUniqueState() {
        if (valueAfterMD5.equals("")) {
            getRandomGUID(false);
        }

        return valueAfterMD5;
    }

    private static void getRandomGUID(boolean secure) {
        MessageDigest md5;
        StringBuilder sbValueBeforeMD5 = new StringBuilder(128);

        try {
            md5 = MessageDigest.getInstance("MD5");

            long e = System.currentTimeMillis();
            long rand;
            if (secure) {
                rand = mySecureRand.nextLong();
            } else {
                rand = myRand.nextLong();
            }

            sbValueBeforeMD5.append(s_id);
            sbValueBeforeMD5.append(":");
            sbValueBeforeMD5.append(Long.toString(e));
            sbValueBeforeMD5.append(":");
            sbValueBeforeMD5.append(Long.toString(rand));
            valueBeforeMD5 = sbValueBeforeMD5.toString();
            md5.update(valueBeforeMD5.getBytes());
            byte[] array = md5.digest();
            StringBuilder sb = new StringBuilder(32);

            for (byte anArray : array) {
                int b = anArray & 255;
                if (b < 16) {
                    sb.append('0');
                }

                sb.append(Integer.toHexString(b));
            }

            valueAfterMD5 = sb.toString();
        } catch (Exception ignore) {
        }

    }

    public String toString() {
        String raw = valueAfterMD5.toUpperCase();
        StringBuilder sb = new StringBuilder(64);
        sb.append(raw.substring(0, 8));
        sb.append("-");
        sb.append(raw.substring(8, 12));
        sb.append("-");
        sb.append(raw.substring(12, 16));
        sb.append("-");
        sb.append(raw.substring(16, 20));
        sb.append("-");
        sb.append(raw.substring(20));
        return sb.toString();
    }

    static {
        long secureInitializer = mySecureRand.nextLong();
        myRand = new Random(secureInitializer);

        try {
            s_id = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException var3) {
            var3.printStackTrace();
        }
    }
}

