package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Model;
import kr.photox.android.model.Protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LogoutApi extends ApiBase implements Api {

	/**
	 * Login Api Initiailizing
	 */

	public LogoutApi() {
		this.API_URL = API.LOGOUT;
		this.PROTOCOL_METHOD = "POST";
		this.PROTOCOL_TYPE = "https";

		Log.d("logout Api", "make success");

	}

	/**
	 * Set Input
	 */

	public void setInput(String session_key) {
		
		this.input_object = new JSONObject();

		try {
			this.input_object.put("session_key", session_key);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Protocol Model을 반환합니다.
	 */

	public Protocol getProtocol() {
		Protocol protocol = new Protocol();

		try {
			JSONObject result = object.getJSONObject("result");

			/*
			 * Result Status를 받아오고 셋팅합니다.
			 */

			String status = result.getString("status");
			protocol.setResult_status(status);
			Log.d("status", status);

			/*
			 * Result Message가 있으면 받아오고 셋팅합니다.
			 */

			if (!result.isNull("message")) {
				String message = result.getString("message");
				protocol.setResult_message(message);
				Log.d("message", message);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return protocol;
	}

	/**
	 * Login Model을 반환합니다.
	 */

	public ArrayList<Model> getModels() {

		ArrayList<Model> models = new ArrayList<Model>();

		models.add(getProtocol());

		return models;

	}
}
