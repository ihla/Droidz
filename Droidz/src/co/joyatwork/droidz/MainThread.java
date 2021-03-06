package co.joyatwork.droidz;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();

	/* desired fps
	 * achieved fps will be possible lower due to multi-threading overhead
	 * achieved fps depends also on rendering time which increases with picture size!
	 */
	private final static int MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	private SurfaceHolder surfaceHolder;
	private MainGamePanel gamePanel;

	private boolean running;

	public void setRunning(boolean running) {
		this.running = running;
	}

	/*
	 * Stuff for stats 
	 * TODO factor out to helper class
	 */
	// private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private DecimalFormat df = new DecimalFormat("0"); // 1 digit
	// we'll be reading the stats every second
	private final static int STAT_INTERVAL = 1000; // ms
	// the average will be calculated by storing
	// the last n FPSs
	private final static int FPS_HISTORY_NR = 10;
	// last time the status was stored
	private long lastStatusStore = 0;
	// the status time counter
	private long statusIntervalTimer = 0l;
	// number of frames skipped since the game started
	private long totalFramesSkipped = 0l;
	// number of frames skipped in a store cycle (1 sec)
	private long framesSkippedPerStatCycle = 0l;

	// number of rendered frames in an interval
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	// the last FPS values
	private double fpsStore[];
	// the number of times the stat has been read
	private long statsCount = 0;
	// the average FPS since the game started
	private double averageFps = 0.0;

	/**
	 * The statistics - it is called every cycle, it checks if time since last
	 * store is greater than the statistics gathering period (1 sec) and if so
	 * it calculates the FPS for the last period and stores it.
	 * 
	 * It tracks the number of frames per period. The number of frames since the
	 * start of the period are summed up and the calculation takes part only if
	 * the next period and the frame count is reset to 0.
	 * 
	 * TODO factor out to helper class
	 */
	private void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;

		// check the actual time
		statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);

		if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
			// calculate the actual frames pers status check interval
			double actualFps = (double) (frameCountPerStatCycle / (STAT_INTERVAL / 1000));

			// stores the latest fps in the array
			fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;

			// increase the number of times statistics was calculated
			statsCount++;

			double totalFps = 0.0;
			// sum up the stored fps values
			for (int i = 0; i < FPS_HISTORY_NR; i++) {
				totalFps += fpsStore[i];
			}

			// obtain the average
			if (statsCount < FPS_HISTORY_NR) {
				// in case of the first 10 triggers
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			// saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			// resetting the counters after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;

			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
			// Log.d(TAG, "Average FPS:" + df.format(averageFps));
			gamePanel.setAvgFps("FPS: " + df.format(averageFps));
		}
	}

	private void initTimingElements() {
		// Initialize timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			fpsStore[i] = 0.0;
		}
		Log.d(TAG + ".initTimingElements()",
				"Timing elements for stats initialised");
	}

	/* end of stuff for stats */

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");

		// Initialize timing elements for stat gathering
		// TODO factor out to helper class
		initTimingElements();

		long beginTime; // the time when the cycle begun
		long timeDiff; // the time it took for the cycle to execute
		int sleepTime; // ms to sleep (<0 if we're behind)
		int framesSkipped; // number of frames being skipped

		sleepTime = 0;

		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try { //TODO why is this try block here? lockCanvas() doesn't throw exception but returns null if not locked!
				//>>> start measuring of frame duration
				beginTime = System.currentTimeMillis();
				framesSkipped = 0; // resetting the frames skipped
				// update game state
				this.gamePanel.update();

				// render state to the screen
				// draws the canvas on the panel
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) { // calling UI methods on non-UI
												// thread
					this.gamePanel.render(canvas);
				}
				// calculate how long did the cycle take
				timeDiff = System.currentTimeMillis() - beginTime;
				//<<< stop measuring of frame duration
				// calculate sleep time
				sleepTime = (int) (FRAME_PERIOD - timeDiff);

				if (sleepTime > 0) {
					// if sleepTime > 0 we're OK
					try {
						// send the thread to sleep for a short period
						// very useful for battery saving
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}

				/*
				 * if game is behind, we update the game only and skip
				 * rendering, we call update() as many times as many frames we
				 * need to skip
				 */
				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
					// we need to catch up
					// update without rendering
					this.gamePanel.update();
					// add frame period to check if in next frame
					sleepTime += FRAME_PERIOD;
					framesSkipped++;
				}

				if (framesSkipped > 0) {
					Log.d(TAG, "Skipped:" + framesSkipped);

				}
				/**
				 * for statistics TODO factor out to helper class
				 */
				framesSkippedPerStatCycle += framesSkipped;
				// calling the routine to store the gathered statistics
				storeStats();

			} finally {
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} // end finally
		}
	}

}
