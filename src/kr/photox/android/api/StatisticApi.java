package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StatisticApi extends ApiBase implements Api {
	private static final String TAG = "Statistic Api";

	/**
	 * Api Main List가 초기화됩니다.
	 */

	public StatisticApi() {
		this.API_URL = API.STATISTIC;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";

	}

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		models.add(getOverview());
		models.addAll(getCampaigns());

		return models;
	}

	private Campaign getOverview() {
		Campaign campaign = new Campaign();
		Log.d(TAG, "obj : "+object.toString());

		try {
			if (!object.isNull("total_score")) {
				int total_score = object.getInt("total_score");
				campaign.setTotalScore(total_score);
				Log.d(TAG, "total_score : "+campaign.getTotalScore());
			}

			if (!object.isNull("total_completed_mission_count")) {
				int total_count = object
						.getInt("total_completed_mission_count");
				campaign.setTotal_mission_count(total_count);
				Log.d(TAG, "total_mission_count : "+campaign.getTotal_mission_count());
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return campaign;
	}

	/**
	 * 캠페인들을반환합니다.
	 * 
	 * @return
	 */

	public ArrayList<Campaign> getCampaigns() {
		ArrayList<Campaign> campaigns = new ArrayList<Campaign>();

		try {
			if (!object.isNull("completed_campaigns")) {
				JSONArray campaign_array = object.getJSONArray("completed_campaigns");

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
						String url = campaign_object.getString("icon_img_url");
						campaign.setIconImgUrl(url);
					}

					if (!campaign_object.isNull("total_mission_count")) {
						int total_mission_count = campaign_object
								.getInt("total_mission_count");
						campaign.setTotal_mission_count(total_mission_count);
					}

					if (!campaign_object.isNull("total_score")) {
						int score = campaign_object.getInt("total_score");
						campaign.setTotalScore(score);
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

}
