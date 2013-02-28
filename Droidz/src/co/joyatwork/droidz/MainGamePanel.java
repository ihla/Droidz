package co.joyatwork.droidz;

import co.joyatwork.droidz.model.Droid;
import co.joyatwork.droidz.model.components.Speed;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	private MainThread thread;
	private Droid droid;

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		
		// create droid and load bitmap
		droid = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 50, 50);

		// create the game loop thread
		thread = new MainThread(getHolder(), this);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * when surface view is created we start main loop thread
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");

		thread.setRunning(true);
		thread.start();
	}

	/**
	 * when surface view is destroyed we shut down the main loop thread cleanly
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		Log.d(TAG, "surfaceDestroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown		
		while (retry) {
			try {
				thread.join();
				retry = false;
				Log.d(TAG, "Thread shut down");
			}
			catch(InterruptedException e) {
				//try again shutting down the thread
				Log.d(TAG, ".");
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// delegating event handling to the droid
			droid.handleActionDown((int) event.getX(), (int) event.getY());

			//check if the gesture happened in lower part of screen (50 pixels? - depends on actual screen size!)
			//TODO remove magic number 50  Do not use hard-coded pixel values in your application code
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity) getContext()).finish();
			} else {
				/* Note: The screen is a rectangle with the upper left coordinates at (0,0) 
				 * and the lower right coordinates at (getWidth(), getHeight()).
				 */
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// the gestures
			if (droid.isTouched()) {
				// the droid was picked up and is being dragged
				droid.setX((int) event.getX());
				droid.setY((int) event.getY());
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (droid.isTouched()) {
				droid.setTouched(false);
			}
		}
		return true;		
	}

	public void update() {
		// check collision with right wall if heading right
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
				&& droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
			droid.getSpeed().toggleXDirection();
		}
		// check collision with left wall if heading left
		if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
				&& droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
			droid.getSpeed().toggleXDirection();
		}
		// check collision with bottom wall if heading down
		if (droid.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
				&& droid.getY() + droid.getBitmap().getHeight() / 2 >= getHeight()) {
			droid.getSpeed().toggleYDirection();
		}
		// check collision with top wall if heading up
		if (droid.getSpeed().getyDirection() == Speed.DIRECTION_UP
				&& droid.getY() - droid.getBitmap().getHeight() / 2 <= 0) {
			droid.getSpeed().toggleYDirection();
		}
		// Update the lone droid
		droid.update();
	}

	public void render(Canvas canvas) {
		// fills the canvas with black
		canvas.drawColor(Color.BLACK);
		droid.draw(canvas);
	}

}
