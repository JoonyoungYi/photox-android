package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CampaignDetailApi extends ApiBase implements Api {

	/**
	 * Api를 초기화 합니다.
	 */
	public CampaignDetailApi() {
		this.API_URL = API.CAMPAIGN_DETAIL;
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
	 * ķ���� ��ȯ
	 * 
	 * @return
	 */

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		try {
			Campaign campaign = new Campaign();

			if (!object.isNull("title")) {
				String title = object.getString("title");
				campaign.setTitle(title);
			}

			if (!object.isNull("description")) {
				String description = object.getString("description");
				campaign.setDescription(description);
			}

			if (!object.isNull("icon_img_url")) {
				String icon_img_url = object.getString("icon_img_url");
				campaign.setIconImgUrl(icon_img_url);
			}

			if (!object.isNull("missions")) {
				JSONArray mission_array = object.getJSONArray("missions");
				ArrayList<Mission> missions = new ArrayList<Mission>();

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

					if (!mission_object.isNull("thumbnail_img_url")) {
						String thumbnail_img_url = mission_object
								.getString("thumbnail_img_url");
						mission.setThumnailUrl(thumbnail_img_url);
					}

					missions.add(mission);
				}
				campaign.setMissions(missions);
			}

			models.add(campaign);

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
