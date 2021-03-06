package co.joyatwork.droidz.model;

import co.joyatwork.droidz.model.components.Speed;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
	
	private final Speed speed;

	/**
	 * Simple animation of walking Elaine (aka sprite)
	 * @param bitmap is image containing all frames in one bitmap
	 * @param x coordinate of Elaine's placement on the display surface
	 * @param y coordinate of Elaine's placement on the display surface
	 * @param fps is sprite frames per second
	 * @param frameCount is count of frames in the bitmap image
	 */
	public ElaineAnimated(Bitmap bitmap, int x, int y, int fps, int frameCount) {
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
		//let Elaine walk with speed of the fraction of sprite width along the x-axis only
		//TODO using Speed class for such simple thing is a non-sense
		speed = new Speed(spriteWidth/3, 0);
	}
	
	/**
	 * called periodically from main loop
	 * @param gameTime is used to calculate sprite frame tick to increment pointer to the frame to be displayed
	 * @param screenWidth 
	 */
	public void update(long gameTime, int screenWidth) {
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			// increment the frame
			currentFrame++;
			if (currentFrame >= frameNr) {
				currentFrame = 0;
			}
			//move Elaine along x axis
			x += speed.getXv();
			if (x >= screenWidth) {
				x = 0;
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
		final float imageX = 50;
		final float imageY = 150;
		// where to draw the sprite
		Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
		
		//display all frames and highlight the current frame
		canvas.drawBitmap(bitmap,  imageX, imageY, null);
		Paint paint = new Paint();
		paint.setARGB(50, 0, 255, 0);// 50 means 75% transparent
		canvas.drawRect( imageX + (currentFrame * destRect.width())
						,imageY
						,imageX + (currentFrame * destRect.width()) + destRect.width()
						,imageY + destRect.height()
						,paint );
	}

	private int getY() {
		return y;
	}

	private int getX() {
		return x;
	}
	
}
