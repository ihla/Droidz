package co.joyatwork.droidz.model;

import co.joyatwork.droidz.model.components.Speed;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Droid {
	private static final String TAG = Droid.class.getSimpleName();
	private Bitmap bitmap; // the actual bitmap
	private Speed speed; //the speed of movement
	private int x; // the X coordinate
	private int y; // the Y coordinate
	private boolean touched; // if droid is touched/picked up

	public Droid(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.speed = new Speed();
		this.x = x;
		this.y = y;
	}

	public void update() {
		if (!touched) {
			x += (speed.getXv() * speed.getxDirection());
			y += (speed.getYv() * speed.getyDirection());
		}
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public Speed getSpeed() {
		return speed;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
				y - (bitmap.getHeight() / 2), null);
		//Log.d(TAG, "drawing at " + (x - (bitmap.getWidth() / 2) + ", " + (y - (bitmap.getHeight() / 2))));
	}
	
	//TODO better to choose android-agnostic name of touch event
	public void handleActionDown(int eventX, int eventY) {
		//TODO refactor to make this brutal logic better understandable
		if (eventX >= (x - bitmap.getWidth() / 2)
				&& (eventX <= (x + bitmap.getWidth() / 2))) {
			if (eventY >= (y - bitmap.getHeight() / 2)
					&& (y <= (y + bitmap.getHeight() / 2))) {
				// droid touched
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}
		if (isTouched())
			Log.d(TAG, "touched at" + eventX + ", " + eventY);
	}
}
