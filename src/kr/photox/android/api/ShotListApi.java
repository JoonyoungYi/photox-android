package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Model;
import kr.photox.android.model.Shot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShotListApi extends ApiBase implements Api {
	private static final String TAG = "Shot List Api";

	/**
	 * Api Main List가 초기화됩니다.
	 */

	public ShotListApi() {
		this.API_URL = API.SHOT_LIST;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";

	}

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		try {
			JSONArray shot_array = object.getJSONArray("missionshots");

			for (int i = 0; i < shot_array.length(); i++) {
				JSONObject shot_object = shot_array.getJSONObject(i);
				Shot shot = new Shot();

				if (!shot_object.isNull("id")) {
					int id = shot_object.getInt("id");
					shot.setId(id);
				}

				if (!shot_object.isNull("mission_id")) {
					int mission_id = shot_object.getInt("mission_id");
					shot.setMissionId(mission_id);
				}

				if (!shot_object.isNull("mission_title")) {
					String mission_title = shot_object
							.getString("mission_title");
					shot.setMission_title(mission_title);
				}

				if (!shot_object.isNull("img_url")) {
					String img_url = shot_object.getString("img_url");
					shot.setImgUrl(img_url);
				}

				if (!shot_object.isNull("confirm_result")) {
					int confirm_result = shot_object.getInt("confirm_result");
					shot.setConfirmResult(confirm_result);
				}

				if (!shot_object.isNull("created")) {
					String created = shot_object.getString("created");
					shot.setCreated(created);
				}

				models.add(shot);

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
