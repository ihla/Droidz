package co.joyatwork.droidz.model;

//TODO remove this dependency on android api
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint; 
import android.graphics.Rect;

/**
 * Particle is small rectangle moving with speed and direction.
 * Particles are used to simulate explosion.
 * An explosion is nothing more than a bunch of particles scattered across the screen, originating from a single point.
 *
 */
public class Particle {
	public static final int STATE_ALIVE = 0;	// particle is alive
	public static final int STATE_DEAD = 1;		// particle is dead

	public static final int DEFAULT_LIFETIME 	= 200;	// play with this
	public static final int MAX_DIMENSION		= 7;	// the maximum width or height
	public static final int MAX_SPEED			= 10;	// maximum speed (per update)

	private int state;			// particle is alive or dead
	private float width;		// width of the particle
	private float height;		// height of the particle
	private float x, y;			// horizontal and vertical position
	private double xv, yv;		// vertical and horizontal velocity
	private int age;			// current age of the particle
	private int lifetime;		// particle dies when it reaches this value
	private int color;			// the color of the particle
	private Paint paint;		// internal use to avoid instantiation
	
	public Particle(int x, int y) {
		this.x = x;
		this.y = y;
		this.state = Particle.STATE_ALIVE;
		this.width = randomInteger(1, MAX_DIMENSION);
		this.height = this.width;
		this.lifetime = DEFAULT_LIFETIME;
		this.age = 0;
		/* 
		 * To set the speed I have used 2 random numbers for the 2 components of the speed vector (vx and vy). 
		 * The smoothing is needed because if both components are near the maximum value 
		 * then the resulting magnitude will be over the max speed. 
		 * You could use simple trigonometric functions with a random degree instead of this.
		 * i.e. randomize magnitude and use cos/sin functions to calculate xv and yv
		 */
		this.xv = (randomDouble(0, MAX_SPEED * 2) - MAX_SPEED);
		this.yv = (randomDouble(0, MAX_SPEED * 2) - MAX_SPEED);
		// smoothing out the diagonal speed
		//TODO ItÕs very amateurish, use trigonometric functions instead of this
		if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
			xv *= 0.7;
			yv *= 0.7;
		}
		this.color = Color.argb(255, randomInteger(0, 255), randomInteger(0, 255), randomInteger(0, 255));
		this.paint = new Paint(this.color);
	}

	/**
	 * Return an double that ranges from min inclusive to max exclusive.
	 * <p>
	 * Math.random() returns uniformly distributed pseudo-random doubles from interval 0.0 inclusive to 1.0 exclusive.
	 * <p>
	 * (max - min) * Math.random() returns random numbers from interval 0 inclusive to (max-min) exclusive.
	 * @param min
	 * @param max
	 * @return
	 */
	static double randomDouble(double min, double max) {
		return min + (max - min) * Math.random();
	}

	/**
	 * Return an integer that ranges from min inclusive to max inclusive.
	 * <p>
	 * Math.random() returns uniformly distributed pseudo-random doubles from interval 0.0 inclusive to 1.0 exclusive.
	 * <p>
	 * Math.random() * (max - min + 1) returns value greater than max-min (because of adding 1*Math.random()).
	 * When converted to integer, the decimal fraction is cut off, thus the random value by Math.random() * (max - min + 1)
	 * is from interval 0 inclusive to (max-min) inclusive.
	 * @param min
	 * @param max
	 * @return
	 */
	static int randomInteger(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}
	
	/**
	 * Every update, the position is set according to the speed
	 * and the alpha component of the particleÕs color is decremented.
	 * In other words the particle is being faded.
	 * If the age exceeded the lifetime or the opacity is 0
	 * (that means that it is completely transparent) the particle is declared dead.
	 */
	public void update() {
		if (this.state != STATE_DEAD) {
			this.x += this.xv;
			this.y += this.yv;

			/* extract alpha - see bitwise gems here http://lab.polygonal.de/?p=81:
			example
			int color = 0xff336699;
			int alpha = color >>> 24;
			int red   = color >>> 16 & 0xFF;
			int green = color >>>  8 & 0xFF;
			int blue  = color & 0xFF;
			*/
			int a = this.color >>> 24;
			// Opacity values: 0 = transparent, 255 = completely opaque
			a -= 2; // fade by 2
			if (a <= 0) { // if reached transparency kill the particle
				this.state = STATE_DEAD;
			} else {
				this.color = (this.color & 0x00ffffff) + (a << 24);		// set the new alpha
				this.paint.setAlpha(a);
				this.age++; // increase the age of the particle
			}
			if (this.age >= this.lifetime) {	// reached the end if its life
				this.state = STATE_DEAD;
			}
		}
	}
	
	/**
	 * Updates position of particle within the rectangle on the surface.
	 * Checks current position and if it is out of rectangle, changes direction to opposite.
	 * @param container
	 */
	public void update(Rect container) {
		// update with collision
		if (this.isAlive()) {
			if (this.x <= container.left || this.x >= container.right - this.width) {
				this.xv *= -1;
			}
			// Bottom is 480 and top is 0 !!!
			if (this.y <= container.top || this.y >= container.bottom - this.height) {
				this.yv *= -1;
			}
		}
		update();
	}
	
	/**
	 * draws particle on given position in given color
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		paint.setColor(this.color);
		//canvas.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, paint);
		canvas.drawCircle(this.x, this.y, this.width, paint);
	}

	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}
	public boolean isDead() {
		return this.state == STATE_DEAD;
	}
}
