/*
 * Copyright 2012 Roman Nurik + Nick Butcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.photox.android.lib;

import java.util.ArrayList;

import kr.photox.android.model.Model;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * A custom ScrollView that can accept a scroll listener.
 */
public class ObservableScrollView extends ScrollView {
	private final String TAG = "Observable ScrollView";

	/**
	 * 
	 */
	private int mTop_margin_height = 1080;
	private int velocity_x;
	private boolean mIsStop = false;
	private boolean mIsFling;

	/**
	 * 
	 */

	private OnScrollCompletionListener onScrollCompletionListener;

	public interface OnScrollCompletionListener {
		void onScrollCompletion(boolean isCompleted, int top_margin_height,
				int y);
	}

	public void setOnScrollCompletionListenter(
			ObservableScrollView.OnScrollCompletionListener listener) {
		this.onScrollCompletionListener = listener;
	}

	private Callbacks mCallbacks;

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (mCallbacks != null) {
			mCallbacks.onScrollChanged();
		}

		/*
         * 
         */

		if (mIsFling) {
			if (Math.abs(y - oldy) < 2) {

				Log.d(TAG, "y :" + y);
				Log.d(TAG, "old y :" + oldy);

				if (velocity_x > 1000) {

					ObservableScrollView.this.smoothScrollTo(0,
							mTop_margin_height);

					Log.i(TAG, "velocity_x : " + velocity_x);

				} else if (velocity_x < -1000) {

					ObservableScrollView.this.smoothScrollTo(0, 0);

				}
				
				mIsFling = false;
			}

			

		}

		if (y == oldy) {
			mIsStop = true;
		} else {
			mIsStop = false;
		}

	}

	@Override
	public int computeVerticalScrollRange() {
		return super.computeVerticalScrollRange();
	}

	public void setCallbacks(Callbacks listener) {
		mCallbacks = listener;
	}

	public static interface Callbacks {
		public void onScrollChanged();
	}

	/**
	 * 
	 */

	public void setTopMarginHeight(int height) {
		this.mTop_margin_height = height;
	}

	/**
     * 
     */

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

			if (mIsStop) {
				onScrollCompletionListener.onScrollCompletion(true,
						mTop_margin_height, this.getScrollY());
			}

			break;
		}

		return super.onTouchEvent(ev);
	}

}
