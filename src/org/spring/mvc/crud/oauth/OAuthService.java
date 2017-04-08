/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */

package org.spring.mvc.crud.oauth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通用说明：标记注解，使用此注解的 Bean 为一个第三方登录服务提供者.
 *
 * @author <a href="mailto:shucunbin@netfinworks.com">matrix</a>
 * @version 1.0.0  2016/10/11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OAuthService {
    String name() default "";
}
