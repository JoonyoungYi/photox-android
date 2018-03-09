package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Login;
import kr.photox.android.model.Model;

import org.json.JSONException;

import android.util.Log;

public class LoginApi extends ApiBase implements Api {
	private final String TAG = "Login Api";

	/**
	 * Login Api Initiailizing
	 */

	public LoginApi() {
		this.API_URL = API.LOGIN;
		this.PROTOCOL_METHOD = "POST";
		this.PROTOCOL_TYPE = "https";

		Log.d(TAG, "login Api make success");

	}

	/**
	 * Set Input
	 */

	public void setInput(String login_type, String login_nonce,
			String auto_key, String login_key, String login_secret) {

		Log.d(TAG, "input login_type : " + login_type);

		// this.input_object = new JSONObject();

		try {
			this.input_object.put("login_type", login_type);
			this.input_object.put("login_nonce", login_nonce);
			this.input_object.put("auto_key", auto_key);
			this.input_object.put("login_key", login_key);
			this.input_object.put("login_secret", login_secret);

			// this.input_object.put("gp_session_key", gp_session_key);
			// this.input_object.put("fb_session_key", fb_session_key);
			// this.input_object.put("email", email);
			// this.input_object.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Login Model을 반환합니다.
	 */

	public ArrayList<Model> getModels() {

		ArrayList<Model> models = new ArrayList<Model>();

		Login login = new Login();

		try {

			/*
			 * 오토키가 있으면 받아오고 셋팅합니다.
			 */

			if (!object.isNull("auto_key")) {
				String auto_key = object.getString("auto_key");
				login.setAuto_key(auto_key);
				Log.d("auto_key", auto_key);
			}
			/*
			 * Session Key를 셋팅합니다.
			 */
			if (!object.isNull("session_key")) {
				String session_key = object.getString("session_key");
				login.setSession_key(session_key);
				Log.d(TAG, "session_key : " + session_key);
			}
			/*
			 * Enc_key 를 셋팅합니다.
			 */
			if (!object.isNull("enc_key")) {
				String enc_key = object.getString("enc_key");
				login.setEnc_key(enc_key);
				Log.d("enc_key", enc_key);
			}
			/*
			 * Enc_IV 를 셋팅합니다.
			 */
			if (!object.isNull("enc_iv")) {
				String enc_iv = object.getString("enc_iv");
				login.setEnc_iv(enc_iv);
				Log.d("enc_iv", enc_iv);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		models.add(login);
		// models.add(getProtocol());

		return models;

	}

}
