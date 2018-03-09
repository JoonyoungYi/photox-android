package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class MainSearchApi extends ApiBase implements Api {
	private final String TAG = "Main Search Api";

	

	/**
	 * Api를 초기화 합니다.
	 */

	public MainSearchApi() {
		this.API_URL = API.MAIN_SEARCH;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";
	}

	/**
	 * Set Input
	 */

	public void setInput(String keyword) {
		try {
			this.input_object.put("keyword", keyword);

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
		ArrayList<Model> models = getCampaigns();
		models.addAll(getMissions());

		return models;
	}

	/**
	 * 검색된 캠페인들을 반환합니다.
	 * 
	 * @return
	 */

	public ArrayList<Model> getCampaigns() {
		ArrayList<Model> campaigns = new ArrayList<Model>();

		try {
			if (!object.isNull("found_campaigns")) {
				JSONArray campaign_array = object
						.getJSONArray("found_campaigns");

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
						int total_mission_count = campaign_object
								.getInt("total_mission_count");
						campaign.setTotal_mission_count(total_mission_count);
					}

					campaigns.add(campaign);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return campaigns;
	}

	/**
	 * 검색된 미들을 반환합니다.
	 * 
	 * @return
	 */

	public ArrayList<Model> getMissions() {
		ArrayList<Model> missions = new ArrayList<Model>();

		try {

			if (!object.isNull("found_missions")) {
				JSONArray mission_array = object.getJSONArray("found_missions");

				for (int i = 0; i < mission_array.length(); i++) {
					JSONObject mission_object = mission_array.getJSONObject(i);

					Mission mission = new Mission();

					if (!mission_object.isNull("id")) {
						int id = mission_object.getInt("id");
						mission.setId(id);
					}

					if (!mission_object.isNull("title")) {
						String title = mission_object.getString("title");
						mission.setTitle(title);
					}

					if (!mission_object.isNull("score")) {
						int score = mission_object.getInt("score");
						mission.setScore(score);
					}

					if (!mission_object.isNull("location")) {
						if ((!mission_object.getJSONObject("location").isNull(
								"latitude"))
								&& (!mission_object.getJSONObject("location")
										.isNull("longitude"))) {
							Double latitude = mission_object.getJSONObject(
									"location").getDouble("latitude");
							Double longitude = mission_object.getJSONObject(
									"location").getDouble("longitude");

							Location location = new Location("point");
							location.setLatitude(latitude);
							location.setLongitude(longitude);
							mission.setLocation(location);
							mission.setDistance(this.current_location);
						}
					}
					missions.add(mission);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return missions;
	}

}
