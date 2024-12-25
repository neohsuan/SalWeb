package gov.vghks.sal.util.ldap;

/**
 * LDAPUtil 使用的 Exception
 * 
 * @author Mark Su
 * 
 */
public class LDAPUtilException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 登入失敗
     */
    public static final int LOGIN_FAILURE = 1;

    private int messageCode;

    public LDAPUtilException() {
    }

    public LDAPUtilException(String message) {
	super(message);
    }

    public LDAPUtilException(Throwable cause) {
	super(cause);
    }

    public LDAPUtilException(String message, Throwable cause) {
	super(message, cause);
    }

    public LDAPUtilException(String message, int messageCode) {
	super(message);
	this.messageCode = messageCode;
    }

    /**
     * get 錯誤代碼
     * 
     * @return the messageCode
     */
    public int getMessageCode() {
	return messageCode;
    }

    /**
     * set 錯誤代碼
     * 
     * @param messageCode
     *        the messageCode to set
     */
    public void setMessageCode(int messageCode) {
	this.messageCode = messageCode;
    }
}
