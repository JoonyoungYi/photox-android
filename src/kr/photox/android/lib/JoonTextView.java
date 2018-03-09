package kr.photox.android.lib;

import kr.photox.android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * A custom Text View that lowers the text size when the text is to big for the
 * TextView. Modified version of one found on stackoverflow
 * http://ankri.de/autoscale-textview/
 * 
 * @author Andreas Krings - www.ankri.de
 * @version 1.0
 * 
 */
public class JoonTextView extends TextView {
	private static final String TAG = "Joon TextView";

	/**
	 * 
	 */
	private Paint textPaint;

	private float preferredTextSize;
	private float minTextSize;

	private float text_size;

	private int boldLevel;

	private boolean isAutoScaleActivated;

	public JoonTextView(Context context) {
		this(context, null);

	}

	public JoonTextView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.joonTextViewStyle);

		// Use this constructor, if you do not want use the default style
		// super(context, attrs);
	}

	public JoonTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.textPaint = new Paint();

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.JoonTextView, defStyle, 0);
		this.minTextSize = a.getDimension(R.styleable.JoonTextView_minTextSize,
				10f);
		this.isAutoScaleActivated = a.getBoolean(
				R.styleable.JoonTextView_isAutoScaleActivated, false);
		this.boldLevel = a.getInt(R.styleable.JoonTextView_boldLevel, 1);
		a.recycle();

		this.preferredTextSize = this.getTextSize();

		if (this.boldLevel == 0) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/NanumGothicLight.otf");
			this.setTypeface(tf);

		} else {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/NanumGothic.otf");
			this.setTypeface(tf);

		}

	}

	/**
	 * Set the minimum text size for this view
	 * 
	 * @param minTextSize
	 *            The minimum text size
	 */
	public void setMinTextSize(float minTextSize) {
		this.minTextSize = minTextSize;
	}

	/**
	 * Resize the text so that it fits
	 * 
	 * @param text
	 *            The text. Neither <code>null</code> nor empty.
	 * @param textWidth
	 *            The width of the TextView. > 0
	 */

	// TODO 이 함수 재귀함수로 바꾸고 서치 방식을 바이너리 서치식으로 바꾸어주어야 합니다. 이건너무합니다.
	private void refitText(String text, int textWidth) {
		if (textWidth <= 0 || text == null || text.length() == 0) {
			return;
		}

		
		int targetWidth = textWidth;
		Log.d(TAG, "targetWidth : " + targetWidth);
		
		this.textPaint.set(this.getPaint());

		Log.d(TAG, "this.getHeight() : " + Integer.toString(this.getHeight()));

		int line = ((int) this.textPaint.measureText(text) / targetWidth) + 2;
		float line_float = this.textPaint.measureText(text) / targetWidth;
		Log.d(TAG, "line : " + line);
		Log.d(TAG, "line_float : " + line_float);
		
		
		
	
		float text_height = line * this.getLineHeight();
		Log.d(TAG, "text_height : " + Float.toString(text_height));

		
		Log.d(TAG, "text ; "+this.getText());
		
		
		Log.d(TAG, "line_height : " + this.getLineHeight());

		
		
		
		//while ((this.getHeight() < text_height)){
		// measureText(text) / targetWidth + 1
		// line = (int) this.textPaint.get;

		Rect bounds = new Rect();
		this.textPaint.getTextBounds(text, 0, text.length(), bounds);

		line = (int) bounds.width() / targetWidth + 2;
		Log.d(TAG, "line : " + Integer.toString(line));

		this.text_size = bounds.height();
		Log.d(TAG, "textSize : " + Float.toString(text_size));

		text_height = line * this.getLineHeight() + (line)
				* this.getLineSpacingExtra() * this.getLineSpacingExtra();
		Log.d(TAG, "text_height : " + Float.toString(text_height));

		//this.text_size -= 1;

		this.textPaint.setTextSize(text_size);

		// }

		this.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size);

	}

	@Override
	protected void onTextChanged(final CharSequence text, final int start,
			final int before, final int after) {
		if (this.isAutoScaleActivated) {
			this.refitText(text.toString(), this.getWidth());
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldwidth,
			int oldheight) {
		if (this.isAutoScaleActivated) {
			if (width != oldwidth) {
				this.refitText(this.getText().toString(), width);
			}
		}

	}

}