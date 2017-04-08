package org.spring.mvc.crud.handlers;

import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spring.mvc.crud.oauth.OAuthAccessToken;
import org.spring.mvc.crud.oauth.OAuthProvider;
import org.spring.mvc.crud.oauth.OAuthProviderFactory;
import org.spring.mvc.crud.oauth.OAuthUserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginHandler {
	
    @Resource(name = "oauthProviderFactory")
    private OAuthProviderFactory oAuthProviderFactory;
    
	@RequestMapping("/redirectAuthPage")
	public String redirectAuthPage(@RequestParam(value="oauth_provider") String oauthProvider
			,Map<String, Object> map,HttpServletRequest request) throws Exception {
		 String oauthProviderName = request.getParameter("oauth_provider");
		 OAuthProvider oAuthProvider = oAuthProviderFactory.createOAuthProvider(oauthProviderName);
		 String url = oAuthProvider.getAuthorizeURL(request);
	   return "redirect:" + url;
	}
	
	  @RequestMapping(value = "afterauth.htm")
	  public String afterAuthLogin(HttpServletRequest request, HttpServletResponse response,Map<String, Object> map) throws Exception {
	        String oAuthProviderClassName = request.getParameter("oauth_provider");
	        OAuthProvider oAuthProvider = oAuthProviderFactory.createOAuthProvider(oAuthProviderClassName);

	        String state = (String)(request.getSession().getAttribute(oAuthProvider.getStateName()));
	        String curState = request.getParameter("state");
	        if (state.equals(curState)) {
	          OAuthAccessToken accessToken = oAuthProvider.getAccessToken(request);
	          String accessCode = accessToken.getAccessToken();
	          String openId = accessToken.getOpenId();
	          if (StringUtils.isEmpty(openId)) {
	              openId =  oAuthProvider.getOpenId(accessCode);
	          }
	          OAuthUserInfo userInfo = oAuthProvider.getUserInfo(accessCode, openId);
	          map.put("username", userInfo.getUserName());
	          map.put("openId", openId);
	        }else{
	        	System.out.println("第三方登录成功跳转回原网站，但state校验失败，可能受到了CSRF攻击！");
	        }
		  return "loginSuccess";
		  
	  }
}
