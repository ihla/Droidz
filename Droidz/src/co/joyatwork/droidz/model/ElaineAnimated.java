package co.joyatwork.droidz.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ElaineAnimated {
	private static final String TAG = ElaineAnimated.class.getSimpleName();

	private Bitmap bitmap;		// the animation sequence
	private Rect sourceRect;	// the rectangle to be drawn from the animation bitmap
	private int frameNr;		// number of frames in animation
	private int currentFrame;	// the current frame
	private long frameTicker;	// the time of the last frame update
	private int framePeriod;	// milliseconds between each frame (1000/fps)

	private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
	private int spriteHeight;	// the height of the sprite

	private int x;				// the X coordinate of the object (top left of the image)
	private int y;				// the Y coordinate of the object (top left of the image)

	/**
	 * Simple animation of walking Elaine (aka sprite)
	 * @param bitmap is image containing all frames in one bitmap
	 * @param x coordinate of Elaine's placement on the display surface
	 * @param y coordinate of Elaine's placement on the display surface
	 * @param width of one frame
	 * @param height of one frame
	 * @param fps is sprite frames per second
	 * @param frameCount is count of frames in the bitmap image
	 */
	public ElaineAnimated(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		currentFrame = 0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth() / frameCount;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		framePeriod = 1000 / fps;
		frameTicker = 0L;
	}
	
	/**
	 * called periodically from main loop
	 * @param gameTime is used to calculate sprite frame tick to increment pointer to the frame to be displayed
	 */
	public void update(long gameTime) {
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			// increment the frame
			currentFrame++;
			if (currentFrame >= frameNr) {
				currentFrame = 0;
			}
		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
	}
	
	/**
	 * draws current sprite frame in rectangle on given coordinates
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		// where to draw the sprite
		Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
	}

	private int getY() {
		return y;
	}

	private int getX() {
		return x;
	}
	
}
