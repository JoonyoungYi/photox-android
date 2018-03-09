package kr.photox.android.api;

import kr.photox.android.model.Shot;

import org.json.JSONException;
import org.json.JSONObject;

public class ShotDetailApi extends ApiBase {

	/**
	 * �ʱ�ȭ
	 */
	private static JSONObject object;
	private int id;

	public ShotDetailApi() {
		String url = API_URL + "shot_detail/api.json";
		this.object = getLine(url);

	}
	
	/**
	 * Input ���ڸ� �޾ƿɴϴ�.
	 */

	public void setInput(int id) {
		this.id = id;
	}

	/**
	 * �̼� ��ȯ
	 * 
	 * @return
	 */

	public Shot getShot() {
		Shot shot = new Shot();

		try {
			int mission_id = object.getInt("mission_id");
			String img_url = object.getString("original_img_url");
			int confirm_result = object.getInt("confirm_result");
			int created_ts = object.getInt("created");

			shot.setMissionId(mission_id);
			shot.setConfirmResult(confirm_result);
			shot.setImgUrl(img_url);
			shot.setCreatedTs(created_ts);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shot;
	}

}
