/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.utils;

import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.NameValuePair;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用说明：第三方登录会用到的工具类.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/12
 */
public final class OAuthUtils {
    private OAuthUtils(){}

    public static String encodeURL(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * 从第三方授权成功后的回调地址中解析出 authCode 与 state.
     *
     * @param url 回调地址
     * @return 字符串数组
     * @throws Exception 
     * @throws OAuthException 开放授权通用异常
     */
    public static String[] extractionAuthCodeFromUrl(String url) throws Exception {
        if (url == null) {
            throw new Exception("you pass a null String object");
        } else {
            Matcher m = Pattern.compile("code=(\\w+)&state=(\\w+)&?").matcher(url);
            String authCode = "";
            String state = "";
            if (m.find()) {
                authCode = m.group(1);
                state = m.group(2);
            }

            return new String[]{authCode, state};
        }
    }

    public static String encodeParameters(NameValuePair[] nameValuePairs) {
        StringBuilder buf = new StringBuilder();

        for(int j = 0; j < nameValuePairs.length; ++j) {
            if(j != 0) {
                buf.append("&");
            }

            try {
                buf.append(URLEncoder.encode(nameValuePairs[j].getName(), "UTF-8")).append("=").append(URLEncoder.encode(nameValuePairs[j].getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        return buf.toString();
    }

}