/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

import javax.servlet.ServletRequest;

import org.spring.mvc.crud.utils.OAuthProviderPlatformKind;

/**
 * 通用说明：开放授权提供者接口.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/9/5
 */
public interface OAuthProvider {
    String getAuthorizeURL(ServletRequest request) throws Exception;

    OAuthAccessToken getAccessToken(ServletRequest request) throws Exception;

    String getOpenId(String accessToken) throws Exception;

    OAuthUserInfo getUserInfo(String accessToken, String openId) throws Exception;


    /**
     * 获取分配给第三方平台的平台类型.
     *
     * @return 平台类型枚举项
     */
    OAuthProviderPlatformKind getPlatformType();

    String getStateName();
}
