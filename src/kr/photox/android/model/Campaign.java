package kr.photox.android.model;

import java.util.ArrayList;

import android.util.Log;

public class Campaign implements Model {
	private static final String TAG = "Campaign";
	// , Serializable
	// private static final long serialVersionUID = 3458450758776315920L;

	/**
	 * 
	 */

	private int id = -1;
	private String title = null;
	private String description;
	private String icon_img_url;

	private int completed_score;
	private int total_score = -1;

	private int total_mission_count = -1;
	private int waiting_mission_count;
	private int completed_mission_count;

	private boolean isShampaign = false;
	private ArrayList<Mission> missions = new ArrayList<Mission>();

	/**
	 * 
	 */
	public Campaign() {

	}

	public String getModelType() {
		if (isShampaign) {
			return "SHAMPAIGN";
		} else {
			return "CAMPAIGN";
		}
	}

	/**
	 * SetData
	 */

	public void setId(int id) {
		this.id = id;
		Log.i(TAG, "id" + id);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIconImgUrl(String icon_img_url) {
		this.icon_img_url = icon_img_url;
	}

	public void setTotal_mission_count(int total_mission_count) {
		this.total_mission_count = total_mission_count;
	}

	public void setCompleted_mission_count(int completed_mission_count) {
		this.completed_mission_count = completed_mission_count;
	}

	public void setTotalScore(int score) {
		this.total_score = score;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIsShampaign(boolean isShampaign) {
		this.isShampaign = isShampaign;
	}

	public void setMissions(ArrayList<Mission> missions) {
		this.missions = missions;
	}

	public void setCompleted_score(int completed_score) {
		this.completed_score = completed_score;
	}

	public void setWaiting_mission_count(int waiting_mission_count) {
		this.waiting_mission_count = waiting_mission_count;
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

	public String getIconImgUrl() {
		return this.icon_img_url;
	}

	public int getTotal_mission_count() {
		return this.total_mission_count;
	}

	public int getCompleted_mission_count() {
		return completed_mission_count;
	}

	public int getTotalScore() {
		return this.total_score;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean getIsShampaign() {
		return isShampaign;
	}

	public ArrayList<Mission> getMissions() {
		return this.missions;
	}

	public int getCompleted_score() {
		return completed_score;
	}

	public int getWaiting_mission_count() {
		return waiting_mission_count;
	}

}
