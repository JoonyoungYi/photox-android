package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MainListApi extends ApiBase implements Api {

	private final String TAG = "Main List API";

	/**
	 * Api Main List가 초기화됩니다.
	 */

	public MainListApi() {
		this.API_URL = API.MAIN_LIST;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";
		// this.isTest = true;

	}

	/**
	 * 모델을 반환합니다. 반환하는 것 샴페인으로 통일! 뷰가 바뀌면서 샴페인으로 통일하는 것이 더 아름다워짐!
	 * 
	 */

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		/**
		 * Todo Section 이 있으면 추가합니다.
		 */

		if (getTodo() != null) {
			models.add(getTodo());
		}

		/**
		 * 추천하는 샴페인들을 띄웁니다.
		 */

		models.addAll(getShampaigns());

		return models;

	}

	/**
	 * Todo Mission 만 반환합니다.
	 * 
	 * @return
	 */

	public Campaign getTodo() {

		try {
			if (!object.isNull("todo")) {
				Log.d(TAG, "add Todo");

				JSONObject todo_object = object.getJSONObject("todo");
				Mission todo = new Mission();

				if (!todo_object.isNull("id")) {
					int mission_id = todo_object.getInt("id");
					todo.setId(mission_id);
				}

				if (!todo_object.isNull("title")) {
					String mission_title = todo_object.getString("title");
					todo.setTitle(mission_title);
				}

				if (!todo_object.isNull("cover_img_url")) {
					String cover_img_url = todo_object
							.getString("cover_img_url");
					todo.setCoverImgUrl(cover_img_url);
				}

				if (!todo_object.isNull("score")) {
					int score = todo_object.getInt("score");
					todo.setScore(score);
				}

				Campaign shamapaign = new Campaign();
				shamapaign.setIsShampaign(true);
				shamapaign.setTitle("Nearby");
				shamapaign.getMissions().add(todo);

				return shamapaign;

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 샴페인들을 반환합니다.
	 * 
	 * @return
	 */

	public ArrayList<Campaign> getShampaigns() {
		ArrayList<Campaign> shampaigns = new ArrayList<Campaign>();
		Log.d(TAG, "add Shampaigns");

		try {
			JSONArray shampaign_array = object.getJSONArray("shampaigns");
			Log.d(TAG, "get Shampaigns : getJsonText make Json Array Success");

			for (int i = 0; i < shampaign_array.length(); i++) {
				JSONObject shampaign_object = shampaign_array.getJSONObject(i);
				Campaign shampaign = new Campaign();
				shampaign.setIsShampaign(true);

				if (!shampaign_object.isNull("id")) {
					int id = shampaign_object.getInt("id");
					Log.d(TAG, Integer.toString(id));
					shampaign.setId(id);
				}

				if (!shampaign_object.isNull("title")) {
					String title = shampaign_object.getString("title");
					shampaign.setTitle(title);
				}

				/*
				 * 미션들을 추가합니다.
				 */

				JSONArray mission_array = shampaign_object
						.getJSONArray("missions");
				ArrayList<Mission> missions = new ArrayList<Mission>();

				for (int j = 0; j < mission_array.length(); j++) {
					JSONObject mission_object = mission_array.getJSONObject(j);
					Mission mission = new Mission();

					if (!mission_object.isNull("id")) {
						int id = mission_object.getInt("id");
						mission.setId(id);
					}

					if (!mission_object.isNull("title")) {
						String title = mission_object.getString("title");
						mission.setTitle(title);
					}

					if (!mission_object.isNull("cover_img_url")) {
						String cover_img_url = mission_object
								.getString("cover_img_url");
						mission.setCoverImgUrl(cover_img_url);
					}

					if (!mission_object.isNull("score")) {
						int score = mission_object.getInt("score");
						mission.setScore(score);
						Log.d(TAG, "mission score : " + Integer.toString(score));
					}

					missions.add(mission);
				}

				shampaign.setMissions(missions);

				/*
				 * 
				 */
				shampaigns.add(shampaign);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shampaigns;
	}

}
