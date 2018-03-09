package kr.photox.android.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import kr.photox.android.model.Decal;
import kr.photox.android.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

public class DecalListApi extends ApiBase implements Api {

	/**
	 * Api를 초기화 합니다.
	 */
	public DecalListApi() {
		this.API_URL = API.DECAL_LIST;
		this.PROTOCOL_METHOD = "GET";
		this.PROTOCOL_TYPE = "httpa";

	}

	/**
	 * Set Input
	 */

	public void setInput(int mission_id, String message) {
		try {
			this.input_object.put("mission_id", mission_id);
			this.input_object.put("message", message);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public int getMissionId() {
		try {
			return this.input_object.getInt("mission_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String getMessage() {
		try {
			return this.input_object.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get Models
	 * 
	 * @return
	 */

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();

		try {
			if (!object.isNull("decals")) {
				JSONArray decal_array = object.getJSONArray("decals");

				/*
				 * 일단 임시로 데칼이 최대 7개까지만 올 수 있도록 설정했습니다. 메모리 관리는 루루 캐시에 대해서 더 공부해
				 * 보아야 할듯 합니다.
				 */

				double maxMemory = Runtime.getRuntime().maxMemory() / 1000000;
				// double allocateMemory = Debug.getNativeHeapAllocatedSize();

				int max_decals = 20;

				if (maxMemory > 89) {
					max_decals += 2;
				}

				if (maxMemory > 100) {
					max_decals += 2;
				}

				/*
				 * 
				 */

				for (int i = 0; i < Math.min(decal_array.length(), max_decals); i++) {
					JSONObject decal_object = decal_array.getJSONObject(i);
					Decal decal = new Decal();

					if (!decal_object.isNull("id")) {
						int id = decal_object.getInt("id");
						decal.setId(id);
					}

					if (!decal_object.isNull("type")) {
						int type = decal_object.getInt("type");
						decal.setType(type);
					}

					if (!decal_object.isNull("img_base64")) {
						decal.setImgPath(saveBitmapToFilePath(decal_object
								.getString("img_base64")));
					}

					models.add(decal);

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

	/**
	 * 비트맵을 파일로 저장합니다.
	 * 
	 * @param bitmap
	 * @return
	 * @throws IOException
	 */

	private String saveBitmapToFilePath(String img_base64) {
		
		/*
		 * 
		 */
		
		byte[] img_data = Base64.decode(img_base64, 0);
		img_base64 = null;
		//Bitmap bitmap = BitmapFactory.decodeByteArray(img_data, 0,
		//		img_data.length);
		//img_data = null;

		/*
		 * 
		 */

		String file_path = Environment.getExternalStorageDirectory().toString()
				+ File.separator.toString() + "photoX"
				+ File.separator.toString() + ".cache"
				+ File.separator.toString()
				+ Long.toString(System.currentTimeMillis());

		/*
		 * 비트맵을 저장할 수 있는 형태로 바꿉니다.
		 */
/*
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);

		/*
		 * 저장할 파일을 생성합니다.
		 */

		File f = new File(file_path);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * 파일에 이미지를 저장합니다.
		 */

		FileOutputStream fo;
		try {
			fo = new FileOutputStream(f);
			//fo.write(bytes.toByteArray());
			
			fo.write(img_data);
			fo.close();

			return file_path;
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * 
		 */

		return null;

	}

}
