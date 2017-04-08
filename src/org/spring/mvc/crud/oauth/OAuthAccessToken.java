/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

import java.io.Serializable;

/**
 * 通用说明：通用的 Access Token 信息类.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/18
 */
public class OAuthAccessToken implements Serializable {
    private static final long serialVersionUID = -7671139420025357420L;

    private String accessToken = "";
    private String expireIn = "";
    private String refreshToken = "";
    private String openId;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(String expireIn) {
        this.expireIn = expireIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String toString() {
        return "AccessToken [accessToken=" + this.accessToken + ", refreshToken=" + this.refreshToken +", expireIn=" + this.expireIn + ", openId=" +this.openId + "]";
    }
}
