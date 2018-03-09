package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CampaignListApi extends ApiBase implements Api {

	/**
	 * Api Main List가 초기화됩니다.
	 */

	public CampaignListApi() {
		this.API_URL = API.CAMPAIGN_LIST;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";

	}

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		try {
			if (!object.isNull("campaigns")) {
				JSONArray campaign_array = object.getJSONArray("campaigns");

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

					if (!campaign_object.isNull("completed_mission_count")) {
						int completed_mission_count = campaign_object
								.getInt("completed_mission_count");
						campaign.setCompleted_mission_count(completed_mission_count);

					}

					if (!campaign_object.isNull("waiting_mission_count")) {
						int waiting_mission_count = campaign_object
								.getInt("waiting_mission_count");
						campaign.setWaiting_mission_count(waiting_mission_count);

					}

					if (!campaign_object.isNull("total_score")) {
						int score = campaign_object.getInt("total_score");
						campaign.setTotalScore(score);
					}

					if (!campaign_object.isNull("completed_score")) {
						int score = campaign_object.getInt("completed_score");
						campaign.setTotalScore(score);
					}

					models.add(campaign);

				}
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
