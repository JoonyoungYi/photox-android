package kr.photox.android.deprecated;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.photox.android.manager.ApplicationManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class ImageController {
	private final String TAG = "Image Controller";

	/**
	 * 
	 */

	private String file_path = "";
	private static int bitmap_position;
	// private Bitmap mBitmap = null;
	int window_width = -1;

	/**
	 * url을 통해 이미지를 반환하는 작업을 실시합니다. 만약 url에 해당하는 이미지가 캐시되어 있다면, 캐시된 이미지를 반환합니다.
	 * 
	 * @param url
	 */

	public ImageController(Context ctx, String url, int resized_weight, Boolean isPath,
			int width) {
		window_width = width;
		window_width *= 0.75;

		new ImageController(ctx, url, window_width / resized_weight, -1, isPath, width);

	}

	public ImageController(Context ctx, String url, int resized_width, int resized_height,
			boolean isPath, int width) {

		window_width = width;
		window_width *= 0.75;

		Bitmap bitmap = null;

		/*
		 * url이 url이라면 url에 해당하는 파일명을 불러옵니다. 아니라면, 파일 패쓰를 그대로 사용합니다.
		 */

		if (isPath) {
			this.file_path = url;
		} else {
			this.file_path = getFilePath(url);
		}

		Log.d(TAG, "file_path is : " + this.file_path);

		/*
		 * 메모리에 존재한다면, 메모리에서 이미지를 리턴합니다. 파일이 존재한다면, 파일에서 이미지를 리턴합니다. 파일이 존재하지
		 * 않는다면, url에서 비트맵을 얻어옵니다. 파일을 리사이징도 해줍니다.
		 */

		ApplicationManager am = (ApplicationManager) ctx.getApplicationContext();
/*
		if (am.cached_bitmap_path.contains(this.file_path)) {
			this.bitmap_position = am.cached_bitmap_path
					.indexOf(this.file_path);
			Log.d(TAG, "there is cached_bitmap file");

		} else {

			if (isFileExists(this.file_path)) {
				bitmap = decodeBitmapFromFile(this.file_path);

			} else {
				bitmap = decodeBitmapFromUrl(url);
			}

			bitmap = getResized(bitmap, resized_width, resized_height);

			/*
			 * 캐싱 위치를 정합니다.
			 */
/*
			this.bitmap_position = am.cached_bitmap.size();
			Log.i(TAG, "bitmap position : "+ this.bitmap_position);

			/*
			 * 캐싱을 해둡니다.
			 */
/*
			am.cached_bitmap.add(bitmap);
			am.cached_bitmap_path.add(this.file_path);

			/*
			 *  
			 */
/*
			saveBitmap(bitmap);

		}*/

	}

	/**
	 * 이미지를 반환합니다.
	 */

	public int getBitmapPosition() {
		Log.i(TAG, "bitmap position : "+ this.bitmap_position);
		return this.bitmap_position;
	}

	/**
	 * url로 부터 기준에 따라 file path 를 반환합니다.
	 * 
	 * @param url
	 * @return
	 */

	private String getFilePath(String url) {
		// String[] token = url.split(File.separator);
		// String path = token[token.length - 1];
		String path = SHA256_s(url);

		Log.d(TAG, "path is " + path);

		path = Environment.getExternalStorageDirectory().toString()
				+ File.separator.toString() + "photoX"
				+ File.separator.toString() + ".cache"
				+ File.separator.toString() + path;

		Log.d(TAG, "path is " + path);

		return path;
	}

	public String SHA256_s(String url) {
		String txtClipher = "";

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			md.update(url.getBytes("UTF-8"));

			byte byteData[] = md.digest();
			byte[] byteData_new = new byte[byteData.length];

			for (int i = 0; i < byteData.length; i++) {
				byteData_new[i] = (byte) (byteData[i] & 0xff);
			}

			txtClipher = Base64.encodeToString(byteData_new, 0).replace("\n",
					"");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return txtClipher;
	}

	/**
	 * 지정된 file_path에 파일이 있는지 없는지를 체크합니다.
	 * 
	 * @param file_path
	 * @return
	 */

	private Boolean isFileExists(String file_path) {
		File f = new File(file_path);

		if (f.isFile()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Path를 입력받으면 일단 비트맵 이미지를 해독해서 불러옵니다. 이렇게 반환하는 비트맵은 오리지널 이미지입니다.
	 */

	private Bitmap decodeBitmapFromFile(String path) {
		Bitmap bitmap = null;
		if (path != "") {
			try {
				Log.d(TAG, "Path is " + path);
				/*
				 * 비트맵 옵션을 설정합니다.
				 */
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inDither = true;

				/*
				 * path에서 파일을 불러옵니다.
				 */
				bitmap = BitmapFactory.decodeFile(path, options);

				Log.d(TAG, "Bitmap decode is success!");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			/*
			 * Path 가 ""이면 path 가 없다고 반환합니다.
			 */
			Log.e(TAG, "There is no path");
		}

		return bitmap;
	}

	/**
	 * 
	 * @param url_str
	 * @return
	 */

	private Bitmap decodeBitmapFromUrl(String url_str) {

		/*
		 * url_str을 URL객체로 변환합니다.
		 */

		URL url = null;

		try {
			url = new URL(url_str);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		/*
		 * 
		 */

		Bitmap bitmap = null;

		try {
			URLConnection conn = url.openConnection();
			conn.connect();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inDither = true;
			options.inPreferredConfig = Config.ARGB_8888;

			BufferedInputStream bis = new BufferedInputStream(
					conn.getInputStream());
			bitmap = BitmapFactory.decodeStream(bis, null, options);
			bis.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * 비트맵을 파일로 저장합니다.
	 * 
	 * @param bitmap
	 * @return
	 * @throws IOException
	 */

	private void saveBitmap(Bitmap bitmap) {
		/*
		 * 비트맵을 저장할 수 있는 형태로 바꿉니다.
		 */
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);

		/*
		 * 저장할 파일을 생성합니다.
		 */
		File f = new File(this.file_path);
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
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 리사이징 된 이미지를 반환합니다. resized_width와 resized_height가 -1이면 디폴트 값으로 판단하고
	 * 계산합니다. 아직 미완성임 근데 ㅋㅋㅋㅋ
	 */

	private Bitmap getResized(Bitmap bitmap, int resized_width,
			int resized_height) {

		/*
		 * 일단 -1값이 들어오지 않는다는 가정하에 로직을 만들어보았습니다.
		 */

		int bitmap_width = bitmap.getWidth();
		int bitmap_height = bitmap.getHeight();
		float bitmap_ratio = bitmap_width / bitmap_height;
		float reszied_ratio = resized_width / resized_height;

		/*
		 * 
		 */

		if (resized_width == -1) {
			if (resized_height == -1) {
				resized_height = 100;
			}

			bitmap = resizedByHeight(bitmap, bitmap_width, bitmap_height,
					resized_height);

		} else if (resized_height == -1) {
			bitmap = resizedByWidth(bitmap, bitmap_width, bitmap_height,
					resized_width);

		} else if ((bitmap_ratio > reszied_ratio)
				&& (bitmap_height > resized_height)) {
			/*
			 * 원본사진의 가로길이가 비율상 큰 경우에는 리사이징을 높이 기준으로 해야 합니다.
			 */

			bitmap = resizedByHeight(bitmap, bitmap_width, bitmap_height,
					resized_height);

		} else if ((bitmap_ratio <= reszied_ratio)
				&& (bitmap_width > resized_width)) {
			/*
			 * 원본사진의 세길이가 비율상 큰 경우에는 리사이징을 가로 기준으로 해야 합니다.
			 */
			bitmap = resizedByWidth(bitmap, bitmap_width, bitmap_height,
					resized_width);

		}

		return bitmap;
	}

	private Bitmap resizedByHeight(Bitmap bitmap, int bitmap_width,
			int bitmap_height, int resized_height) {
		Bitmap resized = Bitmap.createScaledBitmap(bitmap, bitmap_width
				* resized_height / bitmap_height, resized_height, false);

		return resized;
	}

	private Bitmap resizedByWidth(Bitmap bitmap, int bitmap_width,
			int bitmap_height, int resized_width) {
		Bitmap resized = Bitmap.createScaledBitmap(bitmap, resized_width,
				bitmap_height * resized_width / bitmap_width, false);

		return resized;

	}

}
