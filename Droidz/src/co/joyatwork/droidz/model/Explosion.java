package co.joyatwork.droidz.model;

//TODO remove dependencies on android
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Simulates explosion of particles
 * 
 */
public class Explosion {

	private static final String TAG = Explosion.class.getSimpleName();
	
	public static final int STATE_ALIVE 	= 0;	// at least 1 particle is alive
	public static final int STATE_DEAD 		= 1;	// all particles are dead
	
	private Particle[] particles;			// particles in the explosion
	private int x, y;						// the explosion's origin
	private int size;						// number of particles
	private int state;						// whether it's still active or not
											// An explosion is alive if it has at least one particle alive
	
	public Explosion(int particleNr, int x, int y) {
		Log.d(TAG, "Explosion created at " + x + "," + y);
		this.state = STATE_ALIVE;
		this.particles = new Particle[particleNr];
	 	for (int i = 0; i < this.particles.length; i++) {
			Particle p = new Particle(x, y);
			this.particles[i] = p;
		}
	 	this.size = particleNr;
	}

	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}

	public boolean isDead() {
		return this.state == STATE_DEAD;
	}

	
	public int getState() {
		return state;
	}

	public void update(Rect container) {
		if (this.state != STATE_DEAD) {
			boolean isDead = true;
			for (int i = 0; i < this.particles.length; i++) {
				if (this.particles[i].isAlive()) {
					this.particles[i].update(container);
					isDead = false;
				}
			}
			if (isDead) //if no particle is alive, the explosion is dead
				this.state = STATE_DEAD;
		}
	}

	public void draw(Canvas canvas) {
		for(int i = 0; i < this.particles.length; i++) {
			if (this.particles[i].isAlive()) {
				this.particles[i].draw(canvas);
			}
		}
		// display border
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		canvas.drawLines(new float[]{
				0,0, canvas.getWidth()-1,0, 
				canvas.getWidth()-1,0, canvas.getWidth()-1,canvas.getHeight()-1, 
				canvas.getWidth()-1,canvas.getHeight()-1, 0,canvas.getHeight()-1,
				0,canvas.getHeight()-1, 0,0
		}, paint);
	}

}

