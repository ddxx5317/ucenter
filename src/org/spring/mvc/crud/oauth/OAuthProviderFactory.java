/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

/**
 * 通用说明：开放授权提供者抽象工厂类.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/9/6
 */
public interface OAuthProviderFactory {
    <T extends OAuthProvider> T createOAuthProvider(String className);
}
