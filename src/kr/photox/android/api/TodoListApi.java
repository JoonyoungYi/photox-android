package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TodoListApi extends ApiBase implements Api {
	private final String TAG = "Todo List Api";

	/**
	 * Api를 초기화 합니다.
	 */

	public TodoListApi() {
		this.API_URL = API.TODO_LIST;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";
	}

	/**
	 * 검색된 캠페인들을 반환합니다.
	 * 
	 * @return
	 */

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		try {
			if (!object.isNull("todos")) {
				JSONArray todo_array = object.getJSONArray("todos");

				for (int i = 0; i < todo_array.length(); i++) {
					JSONObject todo_object = todo_array.getJSONObject(i);

					if (!todo_object.isNull("type")) {

						if (todo_object.getString("type").equals("mission")) {
							Mission mission = new Mission();

							if (!todo_object.isNull("id")) {
								int id = todo_object.getInt("id");
								mission.setId(id);
								Log.i(TAG, "id"+id);
							}

							if (!todo_object.isNull("title")) {
								String title = todo_object.getString("title");
								mission.setTitle(title);
							}

							if (!todo_object.isNull("is_deactivated")) {
								boolean is_deactivated = todo_object
										.getBoolean("is_deactivated");
								mission.setIs_deactivated(is_deactivated);
							}

							models.add(mission);

						} else if (todo_object.getString("type").equals("campaign")){
							Campaign campaign = new Campaign();

							if (!todo_object.isNull("id")) {
								int id = todo_object.getInt("id");
								campaign.setId(id);
							}

							if (!todo_object.isNull("title")) {
								String title = todo_object.getString("title");
								campaign.setTitle(title);
							}

							if (!todo_object.isNull("is_deactivated")) {
								// boolean is_deactivated = todo_object
								// .getBoolean("is_deactivated");
								// campaign.setIs_deactivated(is_deactivated);
							}

							models.add(campaign);
						}

					}

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
