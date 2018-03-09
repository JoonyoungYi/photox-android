package kr.photox.android.deprecated;

import kr.photox.android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public final class CameraDecalFragment extends Fragment {

	/**
	 * On Created View Method
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.z_camera_decal_fragment, container,
				false);

	//	private ImageView mIv = (ImageView) v.findViewById(R.id.iv);
	//	mIv.setImageBitmap(bmp);

		return v;

	}
}
