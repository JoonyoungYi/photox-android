package kr.photox.android.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class JoonLv extends ListView {
	private float xDistance, yDistance, lastX, lastY;

	public JoonLv(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*
	 * 안에 수평 스크롤뷰가 들어갈 때의 퍼포먼스 개선 (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AbsListView#onInterceptTouchEvent(android.view.MotionEvent
	 * )
	 */

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			lastX = ev.getX();
			lastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - lastX);
			yDistance += Math.abs(curY - lastY);
			lastX = curX;
			lastY = curY;
			if (xDistance > yDistance)
				return false;
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	
}