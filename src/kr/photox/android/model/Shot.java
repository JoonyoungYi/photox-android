package kr.photox.android.model;

public class Shot implements Model {

	private int id;
	private int confirm_result;

	private int mission_id;
	private String thumbnail_url;
	private String img_url;
	private String created;
	private int created_ts;

	private String mission_title;

	/**
	 * 
	 */

	public String getModelType() {
		return "SHOT";
	}

	/**
	 * Set Data
	 */

	public Shot(int id, int confirm_result) {
		this.id = id;
		this.confirm_result = confirm_result;
	}

	public Shot() {

	}

	public void setId(int id) {
		this.id = id;
	}

	public void setConfirmResult(int confirm_result) {
		this.confirm_result = confirm_result;
	}

	public void setMissionId(int mission_id) {
		this.mission_id = mission_id;
	}

	public void setThumnailUrl(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}

	public void setImgUrl(String img_url) {
		this.img_url = img_url;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setCreatedTs(int created_ts) {
		this.created_ts = created_ts;
	}

	public void setMission_title(String mission_title) {
		this.mission_title = mission_title;
	}

	/**
	 * Get Data
	 */

	public int getId() {
		return this.id;
	}

	public int getMissionId() {
		return this.mission_id;
	}

	public int getConfirmResult() {
		return this.confirm_result;
	}

	public String getThumbnailUrl() {
		return this.thumbnail_url;
	}

	public String getImgUrl() {
		return this.img_url;
	}

	public String getCreated() {
		return this.created;
	}

	public int getCreatedTs() {
		return this.created_ts;
	}

	public String getMission_title() {
		return mission_title;
	}
}
