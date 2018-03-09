package kr.photox.android.model;

public class Protocol implements Model {

	private String result_status;
	private String result_message;

	private String token = "";

	public String getModelType() {
		return "PROTOCOL";
	}

	/**
	 * SetData
	 */

	public Protocol() {

	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setResult_status(String result_status) {
		this.result_status = result_status;
	}

	public void setResult_message(String result_message) {
		this.result_message = result_message;
	}

	/**
	 * Get Data
	 * 
	 * @return
	 */

	public String getToken() {
		return token;
	}

	public String getResult_status() {
		return result_status;
	}

	public String getResult_message() {
		return result_message;
	}
}
