package org.spring.mvc.crud.oauth;

import com.meidusa.fastjson.JSON;
import com.meidusa.fastjson.JSONException;
import com.meidusa.fastjson.JSONObject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.spring.mvc.crud.utils.OAuthProviderPlatformKind;
import org.spring.mvc.crud.utils.OAuthUtils;
import org.spring.mvc.crud.utils.RandomStatusGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用说明：QQ 登录 OAuth2.0 提供者.
 */
@Service
@OAuthService(name = "QQOAuthProvider")
public class QQOAuthProvider extends AbstractOAuthProvider implements OAuthProvider {
    private final String CONNECT_STATE_NAME = "qq_connect_state";

    @Value("#{configProperties['qq_redirect_URI']}")
    private String redirectUri;
    @Value("#{configProperties['qq_app_ID']}")
    private String appId;
    @Value("#{configProperties['qq_app_KEY']}")
    private String appKey;
    @Value("#{configProperties['qq_scope']}")
    private String scope;
    @Value("#{configProperties['qq_baseURL']}")
    private String baseUrl;
    @Value("#{configProperties['qq_authorizeURL']}")
    private String authorizeUrl;
    @Value("#{configProperties['qq_accessTokenURL']}")
    private String accessTokenUrl;
    @Value("#{configProperties['qq_getOpenIDURL']}")
    private String openIdUrl;
    @Value("#{configProperties['qq_getUserInfoURL']}")
    private String userInfoUrl;

    public String getAuthorizeURL(ServletRequest request) throws Exception {
        // 此处不能直接调用 Oauth.getAuthorizeURL 方法，因此其中的redirect_uri未进行encode，这会引发一个问题：同一个会话内，首次QQ登录成功，下次再登录不能正常跳转至QQ授权登录页面.
        // 下面的逻辑照搬 getAuthorizeURL 方法的实现，除了redirect_uri的值进行了encode.
        StringBuilder authorizeUrlStrBuf = new StringBuilder();
        String state = RandomStatusGenerator.getUniqueState();
        ((HttpServletRequest) request).getSession().setAttribute(CONNECT_STATE_NAME, state);
        if (!StringUtils.isEmpty(scope)) {
            authorizeUrlStrBuf.append(authorizeUrl.trim());
            authorizeUrlStrBuf.append("?client_id=").append(appId.trim());
            authorizeUrlStrBuf.append("&redirect_uri=").append(OAuthUtils.encodeURL(redirectUri.trim()));
            authorizeUrlStrBuf.append("&response_type=").append("code");
            authorizeUrlStrBuf.append("&state=").append(state);
            authorizeUrlStrBuf.append("&scope=").append(scope);
        } else {
            authorizeUrlStrBuf.append(authorizeUrl.trim());
            authorizeUrlStrBuf.append("?client_id=").append(appId.trim());
            authorizeUrlStrBuf.append("&redirect_uri=").append(OAuthUtils.encodeURL(redirectUri.trim()));
            authorizeUrlStrBuf.append("&response_type=").append("code");
            authorizeUrlStrBuf.append("&state=").append(state);
        }

        return authorizeUrlStrBuf.toString();
    }

    @Override
    public OAuthAccessToken getAccessToken(ServletRequest request) throws Exception {
        String queryString = ((HttpServletRequest)request).getQueryString();
        if (queryString == null) {
            return new QQAccessToken();
        } else {
            String state = (String)((HttpServletRequest)request).getSession().getAttribute(CONNECT_STATE_NAME);
            if (!StringUtils.isEmpty(state)) {
                String[] authCodeAndState = extractionAuthCodeFromUrl(queryString);
                String returnState = authCodeAndState[1];
                String returnAuthCode = authCodeAndState[0];

                QQAccessToken qqAccessToken;
                if(!StringUtils.isEmpty(returnState) && returnState.equals(state)) {
                    HttpClient httpClient = new HttpClient();
                    PostMethod postMethod = null;
                    try {
                        postMethod = new PostMethod(accessTokenUrl);
                        postMethod.addParameter("client_id", appId.trim());
                        postMethod.addParameter("client_secret", appKey.trim());
                        postMethod.addParameter("grant_type", "authorization_code");
                        postMethod.addParameter("code", returnAuthCode);
                        postMethod.addParameter("redirect_uri", redirectUri.trim());

                        HttpMethodParams httpMethodParams = postMethod.getParams();
                        httpMethodParams.setContentCharset("UTF-8");
                        postMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
                        httpClient.executeMethod(postMethod);

                        String responseStr = new String(postMethod.getResponseBody(), "utf-8");
                        qqAccessToken = new QQAccessToken(responseStr);
                    } finally {
                        if (postMethod != null) {
                            postMethod.releaseConnection();
                        }
                    }
                } else {
                    qqAccessToken = new QQAccessToken();
                }

                return qqAccessToken;
            } else {
                return new OAuthAccessToken();
            }
        }
    }

    @Override
    public String getOpenId(String accessToken) throws Exception {
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = null;
        try{
            NameValuePair[] nameValuePairs = new NameValuePair[]{new NameValuePair("access_token",accessToken)};
            String getMethodParamStr = encodeParameters(nameValuePairs);

            String getOpenIdUrl = openIdUrl;
            if(!getOpenIdUrl.contains("?")) {
                getOpenIdUrl = getOpenIdUrl + "?" + getMethodParamStr;
            } else {
                getOpenIdUrl = getOpenIdUrl + "&" + getMethodParamStr;
            }
            getMethod = new GetMethod(getOpenIdUrl);
            getMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
            httpClient.executeMethod(getMethod);

            String openId;
            String responseStr = new String(getMethod.getResponseBody(), "utf-8");
            Matcher m = Pattern.compile("\"openid\"\\s*:\\s*\"(\\w+)\"").matcher(responseStr);
            if(m.find()) {
                openId = m.group(1);
                return openId;
            } else {
                throw new OAuthException("获取QQ开发平台用户的openId发生异常，响应内容：" + responseStr);
            }
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken, String openId) throws Exception {
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = null;
        try{
            NameValuePair[] nameValuePairs = new NameValuePair[]{
                    new NameValuePair("openid", openId),
                    new NameValuePair("oauth_consumer_key", appId),
                    new NameValuePair("access_token", accessToken),
                    new NameValuePair("format", "json")};
            String getMethodParamStr = encodeParameters(nameValuePairs);
            String getUserInfoUrl = userInfoUrl;
            if(!getUserInfoUrl.contains("?")) {
                getUserInfoUrl = getUserInfoUrl + "?" + getMethodParamStr;
            } else {
                getUserInfoUrl = getUserInfoUrl + "&" + getMethodParamStr;
            }
            getMethod = new GetMethod(getUserInfoUrl);
            getMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
            httpClient.executeMethod(getMethod);

            String responseStr = new String(getMethod.getResponseBody(), "utf-8");
            try{
                OAuthUserInfo oAuthUserInfo = new OAuthUserInfo();
                JSONObject jsonObject = JSON.parseObject(responseStr);
                if(jsonObject != null) {
                    int returnCode = jsonObject.getIntValue("ret");
                    if (returnCode == 0) {
                        oAuthUserInfo.setUserName(jsonObject.getString("nickname"));
                        System.out.println(JSON.toJSONString(jsonObject));
                    }
                }

                return oAuthUserInfo;
            } catch (JSONException ex) {
                throw new OAuthException(ex.getMessage(),ex.getCause());
            }
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    @Override
    public OAuthProviderPlatformKind getPlatformType() {
        return OAuthProviderPlatformKind.QQ;
    }

    @Override
    public String getStateName() {
        return CONNECT_STATE_NAME;
    }
}
