package org.spring.mvc.crud.oauth;

import com.meidusa.fastjson.JSON;
import com.meidusa.fastjson.JSONObject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.spring.mvc.crud.utils.OAuthProviderPlatformKind;
import org.spring.mvc.crud.utils.RandomStatusGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.net.URLEncoder;

/**
 * 通用说明：微信 OAuth2.0 提供者.
 */
@Service
@OAuthService(name = "WeChatOAuthProvider")
public class WeChatOAuthProvider extends AbstractOAuthProvider implements OAuthProvider {
    private final String CONNECT_STATE_NAME = "wechat_connect_state";

    @Value("#{configProperties['wechat_redirect_URI']}")
    private String redirectUri;
    @Value("#{configProperties['wechat_app_ID']}")
    private String appId;
    @Value("#{configProperties['wechat_app_secret']}")
    private String appSecret;
    @Value("#{configProperties['wechat_scope']}")
    private String scope;
    @Value("#{configProperties['wechat_authorizeURL']}")
    private String authorizeUrl;
    @Value("#{configProperties['wechat_accessTokenURL']}")
    private String accessTokenUrl;
    @Value("#{configProperties['wechat_validateTokenURL']}")
    private String validateTokenURL;
    @Value("#{configProperties['wechat_getUserInfoURL']}")
    private String userInfoUrl;

    @Override
    public String getAuthorizeURL(ServletRequest request) throws Exception {
        StringBuilder authorizeUrlStrBuf = new StringBuilder();
        String state = RandomStatusGenerator.getUniqueState();
        ((HttpServletRequest) request).getSession().setAttribute(CONNECT_STATE_NAME, state);
        String states = (String)((HttpServletRequest) request).getSession().getAttribute(CONNECT_STATE_NAME);
        System.out.println(states);
        authorizeUrlStrBuf.append(authorizeUrl.trim());
        authorizeUrlStrBuf.append("?appid=").append(appId.trim());
        authorizeUrlStrBuf.append("&redirect_uri=").append(URLEncoder.encode(redirectUri.trim(), "utf-8"));
        authorizeUrlStrBuf.append("&response_type=").append("code");
        authorizeUrlStrBuf.append("&scope=").append(scope.trim());
        authorizeUrlStrBuf.append("&state=").append(state);
        authorizeUrlStrBuf.append("#wechat_redirect");

        return authorizeUrlStrBuf.toString();
    }

    @Override
    public OAuthAccessToken getAccessToken(ServletRequest request) throws Exception {
        String queryString = ((HttpServletRequest) request).getQueryString();
        if (StringUtils.isEmpty(queryString)) {
            new WeChatAccessToken();
        }

        String state = (String) ((HttpServletRequest) request).getSession().getAttribute(CONNECT_STATE_NAME);
        if (!StringUtils.isEmpty(state)) {
            String[] authCodeAndState = extractionAuthCodeFromUrl(queryString);
            String returnState = authCodeAndState[1];
            String returnAuthCode = authCodeAndState[0];

            WeChatAccessToken weChatAccessToken;
            if (!returnState.equals("") && !returnAuthCode.equals("")) {
                if (!state.equals(returnState)) {
                    weChatAccessToken = new WeChatAccessToken();
                } else {
                    HttpClient httpClient = new HttpClient();
                    PostMethod postMethod = null;
                    try {
                        postMethod = new PostMethod(accessTokenUrl);
                        postMethod.addParameter("appid", appId.trim());
                        postMethod.addParameter("secret", appSecret.trim());
                        postMethod.addParameter("code", returnAuthCode);
                        postMethod.addParameter("grant_type", "authorization_code");
                        HttpMethodParams httpMethodParams = postMethod.getParams();
                        httpMethodParams.setContentCharset("UTF-8");
                        postMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
                        httpClient.executeMethod(postMethod);

                        String responseStr = new String(postMethod.getResponseBody(), "utf-8");
                        return new WeChatAccessToken(responseStr);
                    } finally {
                        if (postMethod != null) {
                            postMethod.releaseConnection();
                        }
                    }
                }
            } else {
                weChatAccessToken = new WeChatAccessToken();
            }
            return weChatAccessToken;
        } else {
            return new WeChatAccessToken();
        }
    }

    @Override
    public String getOpenId(String accessToken) throws Exception {
        throw new OAuthException("Not Supported Operation");
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken, String openId) throws Exception {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod(userInfoUrl.trim());
            postMethod.addParameter("access_token", accessToken);
            postMethod.addParameter("openid", openId);
            postMethod.addParameter("lang", "zh_CN");
            HttpMethodParams httpMethodParams = postMethod.getParams();
            httpMethodParams.setContentCharset("UTF-8");
            postMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
            httpClient.executeMethod(postMethod);
            String responseStr = new String(postMethod.getResponseBody(), "utf-8");

            JSONObject responseJsonObject = JSON.parseObject(responseStr);
            System.out.println(JSON.toJSONString(responseJsonObject));
            OAuthUserInfo oAuthUserInfo = new OAuthUserInfo();
            oAuthUserInfo.setUserName(responseJsonObject.getString("nickname"));
            return oAuthUserInfo;
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

    @Override
    public OAuthProviderPlatformKind getPlatformType() {
        return OAuthProviderPlatformKind.WECHAT;
    }

    @Override
    public String getStateName() {
        return CONNECT_STATE_NAME;
    }
}
