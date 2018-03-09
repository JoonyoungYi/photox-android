package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;
import kr.photox.android.model.Shot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class MissionDetailApi extends ApiBase implements Api {
	private final String TAG = "Mission Deatil Api";

	/**
	 * �ʱ�ȭ
	 */

	public MissionDetailApi() {
		this.API_URL = API.MISSION_DETAIL;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";

	}

	/**
	 * Set Input
	 */

	public void setInput(int id) {
		try {
			this.input_object.put("id", id);

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

			/*
			 * 기본 미션정보를 추가합니다.
			 */

			Mission mission = new Mission();

			if (!object.isNull("title")) {
				String title = object.getString("title");
				mission.setTitle(title);
			}

			if (!object.isNull("description")) {
				String description = object.getString("description");
				mission.setDescription(description);
				Log.d(TAG, description);
			}

			if (!object.isNull("thumbnail_img_url")) {
				String cover_img_url = object.getString("thumbnail_img_url");
				mission.setCoverImgUrl(cover_img_url);
				
			}

			if (!object.isNull("score")) {
				int score = object.getInt("score");
				mission.setScore(score);
			}

			if (!object.isNull("location")) {
				if ((!object.getJSONObject("location").isNull("latitude"))
						&& (!object.getJSONObject("location").isNull(
								"longitude"))) {
					Double latitude = object.getJSONObject("location")
							.getDouble("latitude");
					Double longitude = object.getJSONObject("location")
							.getDouble("longitude");

					Location location = new Location("point");
					location.setLatitude(latitude);
					location.setLongitude(longitude);
					mission.setLocation(location);
				}
			}

			if (!object.isNull("is_todo")) {
				boolean is_todo = object.getBoolean("is_todo");
				mission.setIs_todo(is_todo);
			}

			if (!object.isNull("is_deactivated")) {
				boolean is_deactivated = object.getBoolean("is_deactivated");
				mission.setIs_deactivated(is_deactivated);
			}

			/*
			 * 캠페인들을 추가합니다.
			 */

			if (!object.isNull("campaigns")) {
				JSONArray campaign_array = object.getJSONArray("campaigns");
				ArrayList<Campaign> campaigns = new ArrayList<Campaign>();

				for (int i = 0; i < campaign_array.length(); i++) {
					JSONObject campaign_object = campaign_array
							.getJSONObject(i);
					Campaign campaign = new Campaign();

					if (!campaign_object.isNull("id")) {
						int id = campaign_object.getInt("id");
						campaign.setId(id);
					}

					if (!campaign_object.isNull("title")) {
						String title = campaign_object.getString("title");
						campaign.setTitle(title);
					}

					if (!campaign_object.isNull("icon_img_url")) {
						String icon_img_url = campaign_object
								.getString("icon_img_url");
						campaign.setIconImgUrl(icon_img_url);
					
					}
					
					if (!campaign_object.isNull("total_mission_count")) {
						int total_mission_count = campaign_object.getInt("total_mission_count");
						campaign.setTotal_mission_count(total_mission_count);
					}

					campaigns.add(campaign);
				}

				mission.setCampaigns(campaigns);
			}

			/*
			 * 미션샷들을 추가합니다.
			 */

			if (!object.isNull("shots")) {
				JSONArray shot_array = object.getJSONArray("shots");
				ArrayList<Shot> shots = new ArrayList<Shot>();

				for (int i = 0; i < shot_array.length(); i++) {
					JSONObject shot_object = shot_array.getJSONObject(i);

					int id = shot_object.getInt("missionshot_id");
					String img_url = shot_object.getString("img_url");
					int confirm_result = shot_object.getInt("confirm_result");
					int created_ts = shot_object.getInt("created");

					Shot shot = new Shot(id, confirm_result);
					shot.setImgUrl(img_url);
					shot.setCreatedTs(created_ts);

					shots.add(shot);
				}

				mission.setShots(shots);
			}
			/*
			 * 최종입니다.
			 */

			models.add(mission);

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
