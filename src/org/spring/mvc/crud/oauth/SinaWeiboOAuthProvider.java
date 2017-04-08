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
import org.spring.mvc.crud.utils.RandomStatusGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.net.URLEncoder;

/**
 * 通用说明：新浪微博登录 OAuth2.0 提供者.
 */
@Service
@OAuthService(name = "SinaWeiboOAuthProvider")
public class SinaWeiboOAuthProvider extends AbstractOAuthProvider implements OAuthProvider {
    private final String CONNECT_STATE_NAME = "sina_weibo_connect_state";
    private final String SINA_WEIBO_UID_PREFIX = "glp_sina_weibo_";

    @Value("#{configProperties['sina_redirect_URI']}")
    private String redirectUri;
    @Value("#{configProperties['sina_weibo_app_key']}")
    private String appKey;
    @Value("#{configProperties['sina_weibo_app_secret']}")
    private String appSecret;
    @Value("#{configProperties['sina_weibo_scope']}")
    private String scope;
    @Value("#{configProperties['sina_weibo_authorizeURL']}")
    private String authorizeUrl;
    @Value("#{configProperties['sina_weibo_accessTokenURL']}")
    private String accessTokenUrl;
    @Value("#{configProperties['sina_weibo_getTokenInfoURL']}")
    private String tokenInfoURL;
    @Value("#{configProperties['sina_weibo_getUserInfoURL']}")
    private String userInfoUrl;

    @Override
    public String getAuthorizeURL(ServletRequest request) throws Exception {
        StringBuilder authorizeUrlStrBuf = new StringBuilder();
        String state = RandomStatusGenerator.getUniqueState();
        ((HttpServletRequest) request).getSession().setAttribute(CONNECT_STATE_NAME, state);

        authorizeUrlStrBuf.append(authorizeUrl.trim());
        authorizeUrlStrBuf.append("?client_id=").append(appKey.trim());
        authorizeUrlStrBuf.append("&response_type=").append("code");
        authorizeUrlStrBuf.append("&redirect_uri=").append(URLEncoder.encode(redirectUri.trim(), "utf-8"));
        authorizeUrlStrBuf.append("&scope=").append(scope.trim());
        authorizeUrlStrBuf.append("&state=").append(state);
        authorizeUrlStrBuf.append("&display=").append("default");
        authorizeUrlStrBuf.append("&forcelogin=").append("true");

        return authorizeUrlStrBuf.toString();
    }

    @Override
    public OAuthAccessToken getAccessToken(ServletRequest request) throws Exception {
        String queryString = ((HttpServletRequest) request).getQueryString();
        if (queryString == null) {
            return new SinaWeiboAccessToken();
        } else {
            String state = (String) ((HttpServletRequest) request).getSession().getAttribute(CONNECT_STATE_NAME);
            if (!StringUtils.isEmpty(state)) {
                String[] authCodeAndState = extractionAuthCodeFromUrl(queryString);
                String returnAuthCode = authCodeAndState[0];
                String returnState = authCodeAndState[1];

                SinaWeiboAccessToken sinaWeiboAccessToken;
                if (!StringUtils.isEmpty(returnState) && returnState.equals(state)) {
                    HttpClient httpClient = new HttpClient();
                    PostMethod postMethod = null;
                    try {
                        postMethod = new PostMethod(accessTokenUrl);
                        postMethod.addParameter("client_id", appKey.trim());
                        postMethod.addParameter("client_secret", appSecret.trim());
                        postMethod.addParameter("grant_type", "authorization_code");
                        postMethod.addParameter("code", returnAuthCode);
                        postMethod.addParameter("redirect_uri", redirectUri.trim());

                        HttpMethodParams httpMethodParams = postMethod.getParams();
                        httpMethodParams.setContentCharset("UTF-8");
                        postMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
                        httpClient.executeMethod(postMethod);

                        String responseStr = new String(postMethod.getResponseBody(), "utf-8");
                        sinaWeiboAccessToken = new SinaWeiboAccessToken(responseStr,SINA_WEIBO_UID_PREFIX);
                    } finally {
                        if (postMethod != null) {
                            postMethod.releaseConnection();
                        }
                    }
                } else {
                    sinaWeiboAccessToken = new SinaWeiboAccessToken();
                }
                return sinaWeiboAccessToken;
            } else {
                return new SinaWeiboAccessToken();
            }
        }
    }

    @Override
    public String getOpenId(String accessToken) throws Exception {
        throw new OAuthException("Not Supported Operation");
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken, String openId) throws Exception {
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = null;
        try {
            NameValuePair[] nameValuePairs = new NameValuePair[]{
                    new NameValuePair("access_token", accessToken),
                    new NameValuePair("uid", openId.substring(SINA_WEIBO_UID_PREFIX.length()))};
            String getMethodParamStr = encodeParameters(nameValuePairs);
            String getUserInfoUrl = userInfoUrl.trim();
            if (!getUserInfoUrl.contains("?")) {
                getUserInfoUrl = getUserInfoUrl + "?" + getMethodParamStr;
            } else {
                getUserInfoUrl = getUserInfoUrl + "&" + getMethodParamStr;
            }
            getMethod = new GetMethod(getUserInfoUrl);
            getMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
            httpClient.executeMethod(getMethod);

            String responseStr = new String(getMethod.getResponseBody(), "utf-8");
            try {
                JSONObject jsonObject = JSON.parseObject(responseStr);
                String errorCode = jsonObject.getString("error_code");
                String errorMessage = jsonObject.getString("error");
                if (!StringUtils.isEmpty(errorCode) || !StringUtils.isEmpty(errorMessage)) {
                    throw new OAuthException(errorCode, errorMessage);
                } else {
                    OAuthUserInfo oAuthUserInfo = new OAuthUserInfo();
                    oAuthUserInfo.setUserName(jsonObject.getString("screen_name"));
                    System.out.println(JSON.toJSONString(jsonObject));
                    return oAuthUserInfo;
                }
            } catch (JSONException ex) {
                throw new OAuthException(ex.getMessage(), ex.getCause());
            }
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    @Override
    public OAuthProviderPlatformKind getPlatformType() {
        return OAuthProviderPlatformKind.SINA_WEIBO;
    }

    @Override
    public String getStateName() {
        return CONNECT_STATE_NAME;
    }
}
