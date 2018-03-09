package kr.photox.android.model;

import android.graphics.Bitmap;

public class Decal implements Model {

	private int id;
	private int type;
	private Bitmap img;
	private String img_path;

	/**
	 * SetData
	 */

	public Decal() {

	}

	/**
	 * 
	 */

	public void setId(int id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setImgPath(String img_path) {
		this.img_path = img_path;
	}

	public void setImg(Bitmap bitmap) {
		this.img = bitmap;
	}

	public String getModelType() {
		return "DECAL";
	}

	/**
	 * Get Data
	 */

	public int getId() {
		return this.id;
	}

	public int getType() {
		return this.type;
	}

	public Bitmap getImg() {
		return img;
	}
	
	public String getImgPath(){
		return this.img_path;
	}
}
