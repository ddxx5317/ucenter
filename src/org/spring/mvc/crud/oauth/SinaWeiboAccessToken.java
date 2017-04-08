/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

import com.meidusa.fastjson.JSON;
import com.meidusa.fastjson.JSONException;
import com.meidusa.fastjson.JSONObject;

/**
 * 通用说明：新浪微博 Access Token 信息类.
 *
 */
public class SinaWeiboAccessToken extends OAuthAccessToken {
    private static final long serialVersionUID = 5878260820519949918L;

    public SinaWeiboAccessToken() {
    }

    public SinaWeiboAccessToken(String responseStr,String openIdPrefix) throws OAuthException {
        try {
            JSONObject responseJsonObj = JSON.parseObject(responseStr);
            setAccessToken(responseJsonObj.getString("access_token"));
            setExpireIn(responseJsonObj.getString("expires_in"));
            setOpenId(openIdPrefix + responseJsonObj.getString("uid"));
        } catch (JSONException ex) {
            throw new OAuthException(ex.getMessage(), ex.getCause());
        }
    }
}
