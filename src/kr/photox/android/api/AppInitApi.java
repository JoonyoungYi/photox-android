package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Login;
import kr.photox.android.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AppInitApi extends ApiBase implements Api{

	/**
	 * Api 
	 */

	public AppInitApi() {
		this.API_URL = API.APP_INIT;
		this.PROTOCOL_METHOD = "POST";
		this.PROTOCOL_TYPE = "https";

	}

	/**
	 * Set Input
	 */

	public void setInput(String os_version, String app_version, String uuid) {

		this.input_object = new JSONObject();

		try {
			this.input_object.put("os_type", "android");
			this.input_object.put("os_version", os_version);
			this.input_object.put("app_version", app_version);
			this.input_object.put("uuid", uuid);
			this.input_object.put("extra_info","");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ���� �����ɴϴ�.
	 */

	public ArrayList<Model> getModels() {

		ArrayList<Model> models = new ArrayList<Model>();

		Login login = new Login();

		try {
			String app_version = object.getString("app_version");
			Log.d("app_version", app_version);
			int server_status = object.getInt("server_status");
			Log.d("server_status", Integer.toString(server_status));
			String login_nonce = object.getString("login_nonce");
			Log.d("login_nounce", login_nonce);
			
			login.setApp_version(app_version);
			login.setServer_status(server_status);
			login.setLogin_nonce(login_nonce);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		models.add(login);

		return models;

	}

}
