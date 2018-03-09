package kr.photox.android.view;

import kr.photox.android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MMapActivity extends Activity {

	/**
	 * UI Reference Init
	 */
	private View mActionbar;
	private TextView mTitleTv;
	private ImageButton mBackBtn;
	private ImageButton mRightBtn;

	/**
	 * 
	 */

	private MapView mapView = null;
	LatLng LOC;
	// static final LatLng MUMBAI = new LatLng(19.0144100, 72.8479400);
	private GoogleMap map;

	// static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	// static final LatLng KIEL = new LatLng(53.551, 9.993);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_map_activity);
		
		Double latitude = getIntent().getExtras().getDouble("latitude");
		Double longitude = getIntent().getExtras().getDouble("longitude");

		LOC = new LatLng(latitude, longitude);
		
		/*
		 * UI Reference
		 */

		mActionbar = (View) findViewById(R.id.actionbar);
		mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);

		/*
		 * 기본 UI의 Visibillity를 설정합니다.
		 */

		mTitleTv.setText("위치 확인하기");
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_left);
		mRightBtn.setVisibility(View.INVISIBLE);

		/*
		 * 
		 */

		mBackBtn.setOnClickListener(onClickListener);

		/*
		 * 
		 */

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		Marker marker = map.addMarker(new MarkerOptions().position(LOC)
				.title("LOCATION").icon(BitmapDescriptorFactory
										.fromResource(R.drawable.ic_launcher)));
		//Marker kiel = map.addMarker(new MarkerOptions()
		//		.position(KIEL)
		//		.title("Kiel")
		//		.snippet("Kiel is cool")
		//		.icon(BitmapDescriptorFactory
		//				.fromResource(R.drawable.ic_launcher)));

	
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(LOC, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

	}

	/**
	 * Btn OnClickListener
	 */

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			clickListenerHandler(v.getId());
		}
	};

	private void clickListenerHandler(int id) {

		switch (id) {
		case R.id.actionbar_left_btn:
			onBackPressed();
			break;
		default:
			break;
		}
	};

}
