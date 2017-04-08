package org.spring.mvc.crud.oauth;

/**
 * 通用说明：开放授权通用异常.
 *
 */
public class OAuthException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
    private String message;

    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message,Throwable cause) {
        super(message, cause);
    }


    public OAuthException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public OAuthException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
