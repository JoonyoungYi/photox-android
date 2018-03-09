package kr.photox.android.view;

import kr.photox.android.R;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.utils.ImageFetcher;
import kr.photox.android.utils.ImageCache.ImageCacheParams;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ShotActivity extends SherlockFragmentActivity {
	private static final String TAG = "Shot Activity";

	/**
	 * Image Cache
	 */

	private static final String IMAGE_CACHE_DIR = "thumbs";
	private int mImageThumbSize;
	private ImageFetcher mImageFetcher;

	/**
	 * 
	 */
	ImageView mIv;

	/**
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shot_activity);

		/*
		 * 
		 */

		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_full_size);
		ImageCacheParams cacheParams = new ImageCacheParams(this,
				IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f);

		mImageFetcher = new ImageFetcher(this, mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.base_empty_photo);
		mImageFetcher.addImageCache(this.getSupportFragmentManager(),
				cacheParams);

		/*
		 * 
		 */

		int shot_id = getIntent().getExtras().getInt("shot_id");
		String img_url = getIntent().getExtras().getString("img_url");
		/*
		 * 
		 */

		mIv = (ImageView) findViewById(R.id.iv);

		/*
		 * 
		 */

		int window_width = ((ApplicationManager) getApplicationContext())
				.getWindowSize("width");

		/*
		 * 
		 */

		mIv.setLayoutParams(new LinearLayout.LayoutParams(window_width,
				window_width));

		/*
		 * 
		 */

		mImageFetcher.loadImage(img_url, mIv);

	}

	/**
	 * 
	 */

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	public void onPause() {
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

}
