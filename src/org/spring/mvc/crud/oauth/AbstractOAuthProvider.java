package org.spring.mvc.crud.oauth;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用说明：开放授权提供者骨干实现类.
 *
 */
public abstract class AbstractOAuthProvider implements OAuthProvider {
    /**
     * 从第三方授权成功后的回调地址中解析出 authCode 与 state，子类可根据URL的实际情况重写此方法.
     *
     * @param url 回调地址
     * @return 字符串数组
     * @throws OAuthException 开放授权通用异常
     */
    protected String[] extractionAuthCodeFromUrl(String url) throws OAuthException {
        if (url == null) {
            throw new OAuthException("you pass a null String object");
        } else {
            // QQ以及微信匹配此表达式
            Matcher m = Pattern.compile("code=(\\w+)&state=(\\w+)&?").matcher(url);
            String authCode = "";
            String state = "";
            if (m.find()) {
                authCode = m.group(1);
                state = m.group(2);
            }

            if (StringUtils.isEmpty(authCode) && StringUtils.isEmpty(state)) {
                // 新浪微博匹配此表达式
                m = Pattern.compile("state=(\\w+)&code=(\\w+)&?").matcher(url);
                if (m.find()) {
                    state = m.group(1);
                    authCode = m.group(2);
                }
            }

            return new String[]{authCode, state};
        }
    }

    protected  String encodeParameters(NameValuePair[] nameValuePairs) {
        StringBuilder buf = new StringBuilder();

        for(int j = 0; j < nameValuePairs.length; ++j) {
            if(j != 0) {
                buf.append("&");
            }

            try {
                buf.append(URLEncoder.encode(nameValuePairs[j].getName(), "UTF-8")).append("=").append(URLEncoder.encode(nameValuePairs[j].getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        return buf.toString();
    }
}
