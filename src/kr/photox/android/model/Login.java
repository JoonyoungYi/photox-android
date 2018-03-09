package kr.photox.android.model;

public class Login implements Model {

	private String app_version;
	private int server_status;
	private String login_nonce;
	private String auto_key = null;

	private String session_key;
	private String enc_key;
	private String enc_iv;

	/**
	 * 
	 */
	public Login() {

	}

	public String getModelType() {
		return "LOGIN";
	}

	/**
	 * SetData
	 */

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public void setServer_status(int server_status) {
		this.server_status = server_status;
	}

	public void setLogin_nonce(String login_nonce) {
		this.login_nonce = login_nonce;
	}

	public void setAuto_key(String auto_key) {
		this.auto_key = auto_key;
	}

	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}

	public void setEnc_key(String enc_key) {
		this.enc_key = enc_key;
	}

	public void setEnc_iv(String enc_iv) {
		this.enc_iv = enc_iv;
	}

	/**
	 * Get Data
	 * 
	 * @return
	 */

	public String getApp_version() {
		return app_version;
	}

	public int getServer_status() {
		return server_status;
	}

	public String getLogin_nonce() {
		return login_nonce;
	}

	public String getAuto_key() {
		return auto_key;
	}

	public String getSession_key() {
		return session_key;
	}

	public String getEnc_key() {
		return enc_key;
	}

	public String getEnc_iv() {
		return enc_iv;
	}

}
