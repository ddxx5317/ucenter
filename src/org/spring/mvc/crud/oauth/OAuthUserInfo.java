/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

/**
 * 通用说明：第三方平台用户信息.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/14
 */
public class OAuthUserInfo {
    // 昵称
    private String userName;

    // 如需其它信息可在此继续添加


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
