package kr.photox.android.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class JoonHsv extends HorizontalScrollView {
	private final String TAG = "JoonHSV";

	private int velocity_x;

	private boolean mIsFling;

	public JoonHsv(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);

		if ((mIsFling)) {
			if (Math.abs(x - oldx) < 2 || x >= getMeasuredHeight() || x == 0) {

				if (velocity_x > 2000) {
					JoonHsv.this.smoothScrollTo(
							((JoonHsv.this.getScrollX() / 1080) + 1) * 1080, 0);
					Log.i(TAG, "velocity_x : " + velocity_x);

				} else if (velocity_x < -2000) {
					JoonHsv.this.smoothScrollTo(
							((JoonHsv.this.getScrollX() / 1080) + 0) * 1080, 0);
					Log.i(TAG, "velocity_x : " + velocity_x);

				} else if ((JoonHsv.this.getScrollX() % 1080 >= 540)) {
					JoonHsv.this.smoothScrollTo(
							((JoonHsv.this.getScrollX() / 1080) + 1) * 1080, 0);

				} else {
					JoonHsv.this.smoothScrollTo(
							((JoonHsv.this.getScrollX() / 1080) + 0) * 1080, 0);
				}

				mIsFling = false;
			}
		}

	}

	@Override
	public void fling(int velocityX) {
		super.fling(velocityX);

		Log.d(TAG, "fling called");

		velocity_x = velocityX;
		mIsFling = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		Log.i(TAG, "scroll x : " + this.getScrollX());
		Log.i(TAG, "scroll y : " + this.getScrollY());

		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "motion event action down called");

			// start_x = ev.getX();
			// start_ts = System.currentTimeMillis();
			break;

		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "motion event action move called");
			// move_count += 1;
			break;

		case MotionEvent.ACTION_CANCEL:
			Log.d(TAG, "motion event action cancel called");

			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "motion event action up called");
			// end_x = ev.getX();
			// end_ts = System.currentTimeMillis();

			break;
		}

		return super.onTouchEvent(ev);
	}

}