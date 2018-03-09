package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ShampaignDetailApi extends ApiBase implements Api {
	
	private static final String TAG = "Shampaign Detail Api";

	/**
	 * Api를 초기화 합니다.
	 */
	public ShampaignDetailApi() {
		this.API_URL = API.SHAMPAIGN_DETAIL;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";

	}

	/**
	 * Set Input
	 */

	public void setInput(int id) {
		try {
			this.input_object.put("id", id);
			Log.i(TAG, "id"+ id);

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

			JSONArray mission_array = object.getJSONArray("missions");

			for (int i = 0; i < mission_array.length(); i++) {
				JSONObject mission_object = mission_array.getJSONObject(i);

				Mission mission = new Mission();

				int id = mission_object.getInt("id");
				mission.setId(id);

				String title = mission_object.getString("title");
				mission.setTitle(title);

				String thumbnail_url = mission_object
						.getString("thumbnail_img_url");
				mission.setCoverImgUrl(thumbnail_url);
				
				int score = mission_object.getInt("score");
				mission.setScore(score);

				models.add(mission);
			}

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
