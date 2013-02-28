package co.joyatwork.droidz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	private MainThread thread;

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
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
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//check if the gesture happened in lower part of screen (50 pixels? - depends on actual screen size!)
			//TODO remove magic number 50
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
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

}
