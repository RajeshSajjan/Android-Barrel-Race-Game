package com.horse.barrel;

import java.util.concurrent.atomic.AtomicReference;

import android.os.SystemClock;
import android.os.Vibrator;

/**
 * Class Name: BarrelRaceModel.java
 * @author Nisha Halyal, Rajesh Sajjan
 *  This activity continuously redraws the court based on the position of the horse and also calculates velocity and acceleration required to the horse.
 */
public class BarrelRaceModel {
	private final float meterpixel = 10;
	private final int ballRadius;
	private String timeString;
	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	private long timeInMilliseconds = 0L;
	private long updatedTime = 0L;
	long startTime = 0L;
	
	//not required i guess
	/*public boolean barrelLCompleted = false;
	public boolean barrelRCompleted = false;
	public boolean barrelMCompleted = false;*/

	public int leftbar_flag[] = new int[4];
	public int rightbar_flag[] = new int[4];
	public int midbar_flag[] = new int[4];
	public int roundStateChanged[] = new int[3];
	public static boolean alternateAxis = false;
	public static boolean alternateAxis1 = false;
	public static boolean alternateAxis2 = false;
	public static boolean alternateAxis3 = false;
	

	int[][] traceMatrix = null;
	
	// these are public, so make sure you synchronize on LOCK
	// when reading these. I made them public since you need to
	// get both X and Y in pairs, and this is more efficient than
	// getter methods. With two getters, you'd still need to
	// synchronize.
	public float ballPixelX, ballPixelY;

	private int pixelWidth, pixelHeight;

	public int getPixelWidth() {
		return pixelWidth;
	}

	public void setPixelWidth(int pixelWidth) {
		this.pixelWidth = pixelWidth;
	}

	public int getPixelHeight() {
		return pixelHeight;
	}

	public void setPixelHeight(int pixelHeight) {
		this.pixelHeight = pixelHeight;
	}

	// values are in meters/second
	private float Xspeed, Yspeed;

	// typical values range from -10...10, but could be higher or lower if
	// the user moves the phone rapidly
	private float accelX, accelY;

	/**
	 * When the ball hits an edge, multiply the velocity by the rebound. A value
	 * of 1.0 means the ball bounces with 100% efficiency. Lower numbers
	 * simulate balls that don't bounce very much.
	 */
	private static final float rebound = 0.5f;
	private static final float STOP_BOUNCING_VELOCITY = 2f;
	private volatile long lastTimeMs = -1;
	public final Object LOCK = new Object();
	private static int BOTTOM_PADDING;
	private boolean bouncedX = false;
	private boolean bouncedY = false;
	private AtomicReference<Vibrator> vibratorRef = new AtomicReference<Vibrator>();
	
	public BarrelRaceModel(int ballRadius) {
		this.ballRadius = ballRadius;
	}
	public void setAccel(float ax, float ay) {
		synchronized (LOCK) {
			this.accelX = ax;
			this.accelY = ay;
		}
	}
	public void setSize(int width, int height) {
		synchronized (LOCK) {
			this.pixelWidth = width;
			this.pixelHeight = height;
			traceMatrix = new int[width + 100][height + 100];
		}
	}

	public int getBallRadius() {
		return ballRadius;
	}

	/**
	 * Call this to move the ball to a particular location on the screen. This
	 * resets the velocity to zero, but the acceleration doesn't change so the
	 * ball should start falling shortly.
	 */
	public void moveBall(int ballX, int ballY) {
		synchronized (LOCK) {
			this.ballPixelX = ballX;
			this.ballPixelY = ballY;
			Xspeed = 0;
			Yspeed = 0;
		}
	}

	public boolean LooseUser() {
		double distancemiddleX = BarrelRaceActivity.midbarX - ballPixelX;
		double distancemiddleY = BarrelRaceActivity.midbarY - ballPixelY;
		double distFromMiddleBarrel = distancemiddleX * distancemiddleX + distancemiddleY * distancemiddleY;	
		double distanceleftX = BarrelRaceActivity.leftbarX - ballPixelX;
		double distanceleftY = BarrelRaceActivity.leftbarY - ballPixelY;
		double distFromLeftBarrel = distanceleftX * distanceleftX + distanceleftY * distanceleftY;
		double distancerightX = BarrelRaceActivity.rightbarX - ballPixelX;
		double distancerightY = BarrelRaceActivity.rightbarY - ballPixelY;
		double distFromRightBarrel = distancerightX * distancerightX + distancerightY * distancerightY;	

		int sumOfRadius = ballRadius + BarrelRaceActivity.barrel_size;
		sumOfRadius = sumOfRadius * sumOfRadius;
		if (sumOfRadius >= distFromLeftBarrel
				|| sumOfRadius >= distFromMiddleBarrel
				|| sumOfRadius >= distFromRightBarrel) {
			moveBall((int) ballPixelX + 1, (int) ballPixelY + 1);
			Vibrator v = vibratorRef.get();
			if (v != null) {
				v.vibrate(35L);
			}
			return true;
		} else
			return false;
	}

	public void calculatelayout() {
		float lWidth, lHeight, layoutX, layoutY, layoutaccelX, layoutaccelY, layoutvelocityX, layoutvelocityY;
		synchronized (LOCK) {
			BOTTOM_PADDING = BarrelRaceActivity.BOTTOM_PADDING;
			lWidth = pixelWidth;
			lHeight = pixelHeight;
			layoutX = ballPixelX;
			layoutY = ballPixelY;
			layoutvelocityX = Xspeed;
			layoutvelocityY = Yspeed;
			layoutaccelX = accelX;
			layoutaccelY = -accelY;
		}
		if (lWidth <= 0 || lHeight <= 0) {
			return;
		}
		long curTime = System.currentTimeMillis();
		if (lastTimeMs < 0) {
			lastTimeMs = curTime;
			return;
		}
		long spentms = curTime - lastTimeMs;
		lastTimeMs = curTime;
		
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

		// update the velocity
		layoutvelocityX += ((spentms * layoutaccelX) / 1000) * meterpixel;
		layoutvelocityY += ((spentms * layoutaccelY) / 1000) * meterpixel;

		// update the position
		layoutX += ((layoutvelocityX * spentms) / 1000) * meterpixel;
		layoutY += ((layoutvelocityY * spentms) / 1000) * meterpixel;

		bouncedX = false;
		bouncedY = false;

		if (layoutY - ballRadius < 0) {
			layoutY = ballRadius;
			layoutvelocityY = -layoutvelocityY * rebound;
			bouncedY = true;
		}
		else if (layoutY + ballRadius > (lHeight - BOTTOM_PADDING)) {
			layoutY = lHeight - ballRadius - BOTTOM_PADDING;
			layoutvelocityY = -layoutvelocityY * rebound;
			bouncedY = true;
		}
		if (bouncedY && Math.abs(layoutvelocityY) < STOP_BOUNCING_VELOCITY) {
			layoutvelocityY = 0;
			bouncedY = false;
		}
		if (layoutX - ballRadius < 0) {
			layoutX = ballRadius;
			layoutvelocityX = -layoutvelocityX * rebound;
			bouncedX = true;
		} else if (layoutX + ballRadius > lWidth) {
			layoutX = lWidth - ballRadius;
			layoutvelocityX = -layoutvelocityX * rebound;
			bouncedX = true;
		}
		if (bouncedX && Math.abs(layoutvelocityX) < STOP_BOUNCING_VELOCITY) {
			layoutvelocityX = 0;
			bouncedX = false;
		}
		
		synchronized (LOCK) {
			ballPixelX = layoutX;
			ballPixelY = layoutY;
			Xspeed = layoutvelocityX;
			Yspeed = layoutvelocityY;
		}

		if (bouncedX || bouncedY) {
			Vibrator v = vibratorRef.get();
			startTime -= 5000; // 5 seconds penalty
			if (v != null) {
				v.vibrate(20L);
			}
		}
		// timer
		timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
		updatedTime = timeInMilliseconds;
		int secs = (int) (updatedTime / 1000);
		int mins = secs / 60;
		secs = secs % 60;
		int milliseconds = (int) (updatedTime % 1000);
		timeString = new String("" + mins + ":" + String.format("%02d", secs)+ ":" + String.format("%03d", milliseconds));
	}

	public int[] isCompletedCircle() {
		float ballX, ballY;
		float leftbarX, leftbarY;
		float rightbarX, rightbarY;
		float midbarX, midbarY;

		synchronized (LOCK) {
			ballX = (int) ballPixelX;
			ballY = (int) ballPixelY;
			leftbarX = (int) BarrelRaceActivity.leftbarX;
			leftbarY = (int) BarrelRaceActivity.leftbarY;
			midbarX = (int) BarrelRaceActivity.midbarX;
			midbarY = (int) BarrelRaceActivity.midbarY;
			rightbarX = (int) BarrelRaceActivity.rightbarX;
			rightbarY = (int) BarrelRaceActivity.rightbarY;
		}

		// Logic for left ball to be circled
		// include y as 212 in if condition
		
		System.out.println("Inside iscompleted\n");
		System.out.println("ballX "+ballX);
		System.out.println("ballY "+ballY);
		
		if (ballX > (leftbarX - 100) && ballX <= leftbarX && ballY > 180 ) {

			if (!alternateAxis) {
				leftbar_flag[0] = 1;
				System.out.println("inside left");
				alternateAxis = false;
			}

		}
		if (ballX < (leftbarX + 100) && ballX > leftbarX && ballY > 180) {

			if (!alternateAxis1) {
				leftbar_flag[1] = 1;
				System.out.println("inside right");
				alternateAxis1 = false;
			}

		}

		if (ballY > (leftbarY - 100) && ballY <= leftbarY && ballX > 180) {

			if (!alternateAxis2) {
				leftbar_flag[2] = 1;
				System.out.println("inside bottom");
				alternateAxis2 = false;
			}

		}

		if (ballY < (leftbarY + 100) && ballY > leftbarY && ballX > 180) {

			if (!alternateAxis3) {
				leftbar_flag[3] = 1;
				System.out.println("inside top");
				alternateAxis3 = false;
			}

		}

		if (leftbar_flag[0] + leftbar_flag[1] + leftbar_flag[2]
				+ leftbar_flag[3] == 4) {
			roundStateChanged[0] = 1;
			leftbar_flag[0] = 0;
			leftbar_flag[1] = 0;
			leftbar_flag[2] = 0;
			leftbar_flag[3] = 0;

		}

		// For Right ball to be circled logic

		if (ballX > (rightbarX - 100) && ballX <= rightbarX && ballY > 180 ) {
			if (!alternateAxis) {
				rightbar_flag[0] = 1;
				alternateAxis = false;
			}
		}
		if (ballX < (rightbarX + 100) && ballX > rightbarX && ballY > 180) {
			if (!alternateAxis1) {
				rightbar_flag[1] = 1;
				alternateAxis1 = false;
			}
		}

		if (ballY > (rightbarY - 100) && ballY <= rightbarY && ballX > 500 ) {
			if (!alternateAxis2) {
				rightbar_flag[2] = 1;
				alternateAxis2 = false;
			}
		}

		if (ballY < (rightbarY + 100) && ballY > rightbarY && ballX > 500) {
			if (!alternateAxis3) {
				rightbar_flag[3] = 1;
				alternateAxis3 = false;
			}
		}

		if (rightbar_flag[0] + rightbar_flag[1] + rightbar_flag[2]
				+ rightbar_flag[3] == 4) {
			roundStateChanged[1] = 1;
			rightbar_flag[0] = 0;
			rightbar_flag[1] = 0;
			rightbar_flag[2] = 0;
			rightbar_flag[3] = 0;
		}

		// Logic for Middle ball to be Circle

		if (ballX > (midbarX - 100) && ballX <= midbarX && ballY > 500) {
			if (!alternateAxis) {
				midbar_flag[0] = 1;
				alternateAxis = false;
			}
		}
		if (ballX < (midbarX + 100) && ballX > midbarX && ballY > 500) {
			if (!alternateAxis1) {
				midbar_flag[1] = 1;
				alternateAxis1= false;
			}
		}

		if (ballY > (midbarY - 100) && ballY <= midbarY && ballX > 300 && ballX < 450) {
			if (!alternateAxis2) {
				midbar_flag[2] = 1;
				alternateAxis2 = false;
			}
		}

		if (ballY < (midbarY + 100) && ballY > midbarY && ballX > 300  && ballX < 450) {
			if (!alternateAxis3) {
				midbar_flag[3] = 1;
				alternateAxis3 = false;
			}
		}

		if (midbar_flag[0] + midbar_flag[1] + midbar_flag[2]
				+ midbar_flag[3] == 4) {
			roundStateChanged[2] = 1;
			midbar_flag[0] = 0;
			midbar_flag[1] = 0;
			midbar_flag[2] = 0;
			midbar_flag[3] = 0;
		} 
		return roundStateChanged;
	}

	public void setVibrator(Vibrator v) {
		vibratorRef.set(v);
	}
}
