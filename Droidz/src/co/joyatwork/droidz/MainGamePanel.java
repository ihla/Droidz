package co.joyatwork.droidz;

import co.joyatwork.droidz.model.Droid;
import co.joyatwork.droidz.model.ElaineAnimated;
import co.joyatwork.droidz.model.Explosion;
import co.joyatwork.droidz.model.components.Speed;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	//TODO replace this enum with polymorphism to remove if-else branches
	public enum Game {
		DROID,
		ELAINE,
		EXPLOSION
	}
	private final Game game = Game.EXPLOSION; //TODO move to constructor params
	private MainThread thread;
	private Droid droid;
	private ElaineAnimated elaine;
	private static final int EXPLOSION_SIZE = 300;
	//private Explosion[] explosions;
	private Explosion explosion;
	
	// the fps to be displayed
	private String avgFps;
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		if (game == Game.DROID) {
			// create droid and load bitmap
			//droid = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 50, 50);
			droid = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 50, 50);
		}
		else if (game == Game.ELAINE) {
			// create Elaine and load bitmap
			elaine = new ElaineAnimated(
					BitmapFactory.decodeResource(getResources(), R.drawable.walk_elaine)
					, 50, 50	// initial position
					, 5, 5);	// FPS and number of frames in the animation
		}
		else if (game == Game.EXPLOSION) {
			explosion = null; // will be created on touch event
		}

		// create the game loop thread
		thread = new MainThread(getHolder(), this);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged");
		
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
		thread.setRunning(false); //TODO calling method on other thread?
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
				Log.d(TAG, "#");
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (game == Game.DROID) {
			return onTouchEventDroid(event);
		} else if (game == Game.EXPLOSION) {
			return onTouchEventExplosion(event);
		}
		
		return false;		
	}

	/**
	 * Creates new explosion on touch position if no one is alive.
	 * @param event
	 * @return
	 */
	private boolean onTouchEventExplosion(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// handle touch
			// check if explosion is null or if it is already dead
			if (explosion == null || explosion.isDead()) {
				explosion = new Explosion(EXPLOSION_SIZE, (int)event.getX(), (int)event.getY());
			}
		}
		return true;
	}

	private boolean onTouchEventDroid(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// delegating event handling to the droid
			droid.handleActionDown((int) event.getX(), (int) event.getY());

			//check if the gesture happened in lower part of screen (50 pixels? - depends on actual screen size!)
			//TODO remove magic number 50  Do not use hard-coded pixel values in your application code
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false); //TODO calling method on other thread?
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
		if (game == Game.DROID) {
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
		else if (game == Game.ELAINE) {
			elaine.update(System.currentTimeMillis());
		}
		else if (game == Game.EXPLOSION) {
			// update explosions
			if (explosion != null && explosion.isAlive()) {
				explosion.update(getHolder().getSurfaceFrame());
			}
		}
	}

	public void render(Canvas canvas) {
		//if display orientation changed, canvas is null 'cause surface destroyed!
		if (canvas == null)
			return;
		// fills the canvas with black
		canvas.drawColor(Color.BLACK);
		if (game == Game.DROID) {
			droid.draw(canvas);
		}
		else if (game == Game.ELAINE) {
			elaine.draw(canvas);
		}
		else if (game == Game.EXPLOSION) {
			// render explosions
			if (explosion != null) {
				explosion.draw(canvas);
			}
		}
		// display fps
		displayFps(canvas, avgFps);
	}
	
	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			paint.setTextSize(25);
			canvas.drawText(fps, this.getWidth() - 100, this.getHeight() - 30, paint); //TODO text position in pixels 50,20?
		}
	}
}
