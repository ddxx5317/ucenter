/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

import org.springframework.util.StringUtils;

import com.meidusa.fastjson.JSON;
import com.meidusa.fastjson.JSONException;
import com.meidusa.fastjson.JSONObject;

/**
 * 通用说明：微信 Access Token 信息类.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/18
 */
public class WeChatAccessToken extends OAuthAccessToken {
    private static final long serialVersionUID = -8956014152136634389L;

    private String scope;

    // 当且仅当该网站应用已获得该用户的userinfo授权时，才会出现该字段
    private String unionId;

    public WeChatAccessToken() {
    }

    public WeChatAccessToken(String responseStr) throws OAuthException{
        try {
            if (!StringUtils.isEmpty(responseStr)) {
                JSONObject jsonObject = JSON.parseObject(responseStr);
                setAccessToken(jsonObject.getString("access_token"));
                setExpireIn(jsonObject.getString("expires_in"));
                setRefreshToken(jsonObject.getString("refresh_token"));
                setOpenId(jsonObject.getString("openid"));

                setScope(jsonObject.getString("scope"));
                setUnionId(jsonObject.getString("unionid"));
            }
        } catch (JSONException ex) {
            throw new OAuthException(ex.getMessage(),ex.getCause());
        }
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
