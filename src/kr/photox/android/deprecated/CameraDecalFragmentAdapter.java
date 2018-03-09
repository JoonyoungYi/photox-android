package kr.photox.android.deprecated;

import java.util.ArrayList;

import kr.photox.android.api.DecalListApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Decal;
import kr.photox.android.model.Model;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CameraDecalFragmentAdapter extends PagerAdapter {
	private final String TAG = "Camera Decal Fragment Adapter";

	/**
	 * 
	 */
	private Context mContext = null;
	private ArrayList<Decal> mDecals;

	/**
	 * 카메라 데칼 프레그먼트 어댑터를 설정할 때에는 반드시 어플리케이션 컨텍스트를 보내야 합니다. 액티비티 컨텍스트를 보냈을 때 생기는
	 * 문제들에 대해서는 책임지지 않습니다.
	 * 
	 * @param ctx
	 * @param fm
	 */

	public CameraDecalFragmentAdapter(Context ctx, ArrayList<Decal> decals) {
		super();
		mContext = ctx;
		mDecals = new ArrayList<Decal>();
		mDecals.add(new Decal());

		//loadDecals(id);
	}

	@Override
	public int getCount() {
		return mDecals == null ? 0 : mDecals.size();
	}

	@Override
	public Object instantiateItem(View pager, int position) {
		ImageView iv = new ImageView(mContext);
		iv.setImageBitmap(mDecals.get(position).getImg());
		((ViewPager) pager).addView(iv);
		return iv;

	}

	// 뷰 객체 삭제.
	@Override
	public void destroyItem(View pager, int position, Object view) {
		((ViewPager) pager).removeView((View) view);
	}

	// instantiateItem메소드에서 생성한 객체를 이용할 것인지
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}


}