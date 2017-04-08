/*

 * Copyright 2016 netfinworks.com, Inc. All rights reserved.

 * netfinworks.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

 */
package org.spring.mvc.crud.oauth;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用说明：使用工厂方法创建OAuthProvider实例.
 */
@Service("oauthProviderFactory")
public class DefaultOAuthProviderFactory implements OAuthProviderFactory, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Map<String, Object> oAuthProviderMap = new ConcurrentHashMap<String, Object>();

    @SuppressWarnings("unchecked")
	@Override
    public <T extends OAuthProvider> T createOAuthProvider(String oauthProviderName) {
        return (T) oAuthProviderMap.get(oauthProviderName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        registerOAuthService();
    }

    private void registerOAuthService() {
        Map<String, Object> oAuthServiceMap = applicationContext.getBeansWithAnnotation(OAuthService.class);
        for (Object bean : oAuthServiceMap.values()) {

            String oauthProviderName = getOAuthServiceName(bean);
            if (!StringUtils.isEmpty(oauthProviderName)) {
                oAuthProviderMap.put(oauthProviderName, bean);
            }
        }
    }

    private String getOAuthServiceName(Object bean) {
        if (bean != null) {
            Annotation annotation = AnnotationUtils.getAnnotation(bean.getClass(), OAuthService.class);
            if (annotation != null) {
                return (String) AnnotationUtils.getValue(annotation, "name");
            }
        }

        return "";
    }
}
