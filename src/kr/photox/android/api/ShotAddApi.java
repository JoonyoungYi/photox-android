package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Model;
import kr.photox.android.model.Shot;

import org.json.JSONException;

import android.util.Log;

public class ShotAddApi extends ApiBase implements Api {
	private static final String TAG = "Shot Add Api";

	/**
	 * Api를 초기화 합니다.
	 */

	public ShotAddApi() {
		this.API_URL = API.SHOT_ADD;
		this.PROTOCOL_METHOD = "POST";
		this.PROTOCOL_TYPE = "httpa";

	}

	/**
	 * 입력을 넣습니다.
	 */

	public void setInput(int mission_id, int decal_id, String message,
			String img_base64) {
		try {

			this.input_object.put("mission_id", mission_id);
			this.input_object.put("decal_id", decal_id);
			this.input_object.put("message", message);
			this.input_object.put("img_base64", img_base64);

			Log.i(TAG, "base64 length " + img_base64.getBytes().length);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 모델을 반환합니다.
	 * 
	 * @return
	 */

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		try {
			Shot shot = new Shot();

			if (!object.isNull("id")) {
				int id = object.getInt("id");
				shot.setId(id);
			}

			if (!object.isNull("img_url")) {
				String img_url = object.getString("img_url");
				shot.setImgUrl(img_url);
			}

			models.add(shot);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return models;
	}

}
