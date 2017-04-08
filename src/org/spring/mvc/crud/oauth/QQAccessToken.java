/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

import com.meidusa.fastjson.JSON;
import com.meidusa.fastjson.JSONException;
import com.meidusa.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用说明：QQ Access Token 信息类.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/19
 */
public class QQAccessToken extends OAuthAccessToken {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QQAccessToken(){
    }

    public QQAccessToken(String responseStr) {
        try {
            JSONObject responseJsonObj = JSON.parseObject(responseStr);
            setAccessToken(responseJsonObj.getString("access_token"));
            setExpireIn(responseJsonObj.getString("expires_in"));
            setRefreshToken(responseJsonObj.getString("refresh_token"));
            setOpenId(responseJsonObj.getString("openid"));
        } catch (JSONException ex) {
            Matcher matcher = Pattern.compile("^access_token=(\\w+)&expires_in=(\\w+)&refresh_token=(\\w+)$").matcher(responseStr);
            if(matcher.find()) {
                setAccessToken(matcher.group(1));
                setExpireIn(matcher.group(2));
                setRefreshToken(matcher.group(3));
            } else {
                Matcher matcher1 = Pattern.compile("^access_token=(\\w+)&expires_in=(\\w+)$").matcher(responseStr);
                if(matcher1.find()) {
                    setAccessToken(matcher.group(1));
                    setExpireIn(matcher.group(2));
                }
            }
        }
    }
}
