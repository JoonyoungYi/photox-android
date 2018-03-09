package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Model;

import org.json.JSONException;

public class TodoAddApi extends ApiBase implements Api {

	/**
	 * 초기화
	 */

	public TodoAddApi() {
		this.API_URL = API.TODO_ADD;
		this.PROTOCOL_METHOD = "POST";
		this.PROTOCOL_TYPE = "httpa";
	}

	/**
	 * Set Input
	 */

	public void setInput(String type, int id) {
		try {
			this.input_object.put("type", type);
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

		return models;
	}
}
