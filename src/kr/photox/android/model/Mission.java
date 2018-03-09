package kr.photox.android.model;

import java.util.ArrayList;

import android.location.Location;

public class Mission implements Model {

	/**
	 * 변수들을 선언하고 초기화하는 부분입니다.
	 */
	// private static final long serialVersionUID = -3615580263214328975L;
	private int id;
	private String title;

	private boolean is_todo = false;
	private boolean is_deactivated = false;

	private String cover_img_url = null;
	private String thumbnail_img_url = null;

	private String description;
	private int score;

	private Location location = null;
	private double distance = -1;

	private ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
	private ArrayList<Shot> shots = new ArrayList<Shot>();

	private int completed_ts;

	/**
	 * 
	 */

	public String getModelType() {
		return "MISSION";
	}

	/**
	 * SetData
	 */

	public Mission() {

	}

	public Mission(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCoverImgUrl(String cover_img_url) {
		this.cover_img_url = cover_img_url;
	}

	public void setIs_todo(boolean is_todo) {
		this.is_todo = is_todo;
	}

	public void setIs_deactivated(boolean is_deactivated) {
		this.is_deactivated = is_deactivated;
	}

	public void setThumnailUrl(String thumbnail_url) {
		this.thumbnail_img_url = thumbnail_url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setDistance(Location current_location) {
		if ((this.location != null) && (current_location != null)) {
			this.distance = this.location.distanceTo(current_location);
		}
	}

	public void setCampaigns(ArrayList<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	public void setShots(ArrayList<Shot> shots) {
		this.shots = shots;
	}

	public void setCompleted_ts(int completed_ts) {
		this.completed_ts = completed_ts;
	}

	/**
	 * Get Data
	 */

	public int getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getCoverImgUrl() {
		return this.cover_img_url;
	}

	public String getThumbnailUrl() {
		return this.thumbnail_img_url;
	}

	public boolean getIs_todo() {
		return is_todo;
	}

	public boolean getIs_deactivated() {
		return is_deactivated;
	}

	public String getDescription() {
		return this.description;
	}

	public int getScore() {
		return this.score;
	}

	public Location getLocation() {
		return this.location;
	}

	public double getDistance() {
		return this.distance;
	}

	public ArrayList<Campaign> getCampaigns() {
		return this.campaigns;
	}

	public ArrayList<Shot> getShots() {
		return this.shots;
	}

	public int getCompleted_ts() {
		return completed_ts;
	}

}
