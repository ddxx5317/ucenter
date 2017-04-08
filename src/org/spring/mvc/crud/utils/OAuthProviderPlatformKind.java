/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */

package org.spring.mvc.crud.utils;

/**
 * 通用说明：开放授权提供者平台类型枚举类.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/9/7
 */
public enum OAuthProviderPlatformKind {
    QQ("QQ_OPENID", "51", "腾讯QQ"),
    WECHAT("WECHAT_OPENID", "52", "微信"),
    SINA_WEIBO("SINA_WEIBO_OPENID", "53", "新浪微博"),;

    private String name;
    private String code;
    private String description;

    OAuthProviderPlatformKind(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
