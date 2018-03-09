package kr.photox.android.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kr.photox.android.manager.ProtocolController;
import kr.photox.android.model.Protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Base64;
import android.util.Log;

public class ApiBase {
	private final String TAG = "Api Base";
	public Location current_location = null;

	public String session_key;
	public String token = "";
	public String enc_key;
	public String enc_iv;

	// public String BASE_URL = "";

	/**
	 * API Type 초기화
	 */

	// public Boolean isTest = false;
	public String API_URL = "init";
	public String PROTOCOL_TYPE = "http";
	public String PROTOCOL_METHOD = "GET";

	public JSONObject object;
	public JSONObject input_object = new JSONObject();

	public final class API {
		public static final String APP_INIT = "init";
		public static final String JOIN = "join";
		public static final String LOGIN = "login";
		public static final String LOGOUT = "logout";
		public static final String STATISTIC = "statistic";
		public static final String TODO_LIST = "todo/list";
		public static final String MAIN_LIST = "main/list";
		public static final String MAIN_SEARCH = "main/search";
		public static final String SHAMPAIGN_DETAIL = "shampaign/detail";
		public static final String CAMPAIGN_LIST = "campaign/list";
		public static final String CAMPAIGN_DETAIL = "campaign/detail";
		public static final String MISSION_DETAIL = "mission/detail";
		public static final String TODO_ADD = "todo/add";
		public static final String TODO_DELETE = "todo/delete";
		public static final String DECAL_LIST = "decal/list";
		public static final String SHOT_LIST = "missionshot/list";
		public static final String SHOT_ADD = "missionshot/add";

		private API() {
		}
	}

	public String getApiType() {
		return this.API_URL;
	}

	/**
	 * Model ����
	 */
	public final class MODEL {
		public static final String MISSION = "MISSION";
		public static final String CAMPAIGN = "CAMPAIGN";

		private MODEL() {
		}
	}

	/**
	 * Execute
	 */

	public void execute() {
		String url = API_URL;
		this.object = getLine(url);

	}

	/**
	 * Line �� ���ͼ� JSON object�� �ٲٱ�
	 */

	public JSONObject getLine(String url) {

		JSONObject object = new JSONObject();
		// JSONObject content = new JSONObject();
		String line = null;
		// String content_str;

		try {
			ProtocolController protocolController = new ProtocolController(url,
					PROTOCOL_METHOD, PROTOCOL_TYPE);

			if (PROTOCOL_TYPE == "httpa") {
				protocolController.setSecureKey(this.session_key, this.token,
						this.enc_key, this.enc_iv);
				if (this.current_location != null) {
					protocolController.setLocation(current_location);
				}
				token = "";
			}

			protocolController.setContent(input_object);

			protocolController.doRequest();
			line = protocolController.getResult();

			object = new JSONObject(line);

			if (PROTOCOL_TYPE == "httpa") {
				line = AES_s_decoding(object.getString("content"));
				object = new JSONObject(line);
				token = object.getString("token");
				line = object.getString("body");

			} else {
				line = object.getString("content");

			}
			object = new JSONObject(line);
			// Log.d(TAG, "content : " + object.toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (API_URL.equals(ApiBase.API.STATISTIC)) {
			Log.d("result", object.toString());
		}
		
		/*
		 * 받아오는 line을 가비지 컬렉터가 컬렉팅 할 수 있게 도와줍니다.
		 */
		
		line = null;
		
		/*
		 * 오브젝트를 반환합니다.
		 */
		
		return object;

	}

	/**
	 * Protocol Model을 반환합니다.
	 */

	public Protocol getProtocol() {
		Protocol protocol = new Protocol();

		try {
			JSONObject result = object.getJSONObject("result");
			Log.d(TAG, "result" + result.toString());

			/*
			 * Result Status를 받아오고 셋팅합니다.
			 */

			String status = result.getString("status");
			protocol.setResult_status(status);
			// Log.d(TAG, "status : " + status);

			/*
			 * Result Message가 있으면 받아오고 셋팅합니다.
			 */

			if (!result.isNull("message")) {
				String message = result.getString("message");
				protocol.setResult_message(message);
				// Log.d("message", message);
			}

			/*
			 * Token을 셋팅합니다.
			 */
			if (!this.token.equals("")) {
				protocol.setToken(token);
				// Log.d("token", token);
			} else if (!object.isNull("csrf_token")) {
				String token = object.getString("csrf_token");
				protocol.setToken(token);
				// Log.d("token", token);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return protocol;
	}

	/**
	 * Set Input
	 */

	public void setSecureKey(String session_key, String token, String enc_key,
			String enc_iv) {
		this.session_key = session_key;
		this.token = token;
		this.enc_key = enc_key;
		this.enc_iv = enc_iv;

	}

	/**
	 * AES decoding
	 */

	public String AES_s_decoding(String json) {
		// byte[] json_bytes = json.getBytes("UTF-8");
		// byte[] enc_key_bytes = enc_key.getBytes("UTF-8");
		// byte[] enc_iv_bytes = enc_iv.getBytes("UTF-8");

		String result = "";

		try {
			byte[] byte_data = Base64.decode(json, 0);

			SecretKeySpec key_spec = new SecretKeySpec(
					enc_key.getBytes("UTF-8"), "AES");
			IvParameterSpec iv_spec = new IvParameterSpec(
					enc_iv.getBytes("UTF-8"));
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.DECRYPT_MODE, key_spec, iv_spec);

			byte[] encryptedData = cipher.doFinal(byte_data);

			result = new String(encryptedData);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return result;

	}

	/**
	 *
	 */

	public String getProtocolType() {
		return this.PROTOCOL_TYPE;
	}

	/**
	 * 
	 */

	public void setCurrentLocation(Location current_location) {
		this.current_location = current_location;
	}

}
