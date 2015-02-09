package com.horse.barrel;

import java.io.IOException;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;


import com.horse.barrel.R;

/**
 * Class Name: BarrelRaceActivity.java
 * @author Rajesh Sajjan , Nisha Halyal
 *  This activity starts the game and sets up the barrels, horse and court. And it continously monitors accelerometer meter for vales of harse position.
 */
public class BarrelRaceActivity extends Activity implements Callback,SensorEventListener, android.view.View.OnClickListener {
	private GameRun gamer;
	private static final int horse_size = 35;
	public static final int barrel_size = 40;
	private ImageButton play;
	private ImageButton reset;
	public boolean leftbar_color = false;
	public boolean rightbar_color = false;
	public boolean midbar_color = false;
	public static int BOTTOM_PADDING;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private final BarrelRaceModel horse_barrel = new BarrelRaceModel(horse_size);
	private Paint backgroundPaint;
	private Paint courtpaint;
	private Paint horsepaint;
	private long sensorupdate = -1;
	public static int leftbarX, leftbarY, rightbarX, rightbarY, midbarX, midbarY;
	private SensorManager mSensorManager;
	private Sensor Accelerator;
	private TextView timeView;
	private Handler timeHandler = new Handler();
	private Paint leftbar_paint;
	private Paint rightbar_paint;
	private Paint midbar_paint;
	private Runnable Threadupdate;
	private static Long savedTime = 9223372036854775807L;
	private Long currTime = 0L;
	@SuppressWarnings("unused")
	private String minTime;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bouncing_ball);
		
		// setting vibrator
		Vibrator vib = (Vibrator) getSystemService(Activity.VIBRATOR_SERVICE);
		horse_barrel.setVibrator(vib);

		// making court
		surface = (SurfaceView) findViewById(R.id.bouncing_ball_surface);

		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		int displayWidth = display.widthPixels;
		int displayHeight = display.heightPixels;
		// this weird logic makes it adaptable for landscape mode
		displayWidth = Math.min(displayHeight, displayWidth);

		LayoutParams laypar = surface.getLayoutParams();
		laypar.width = displayWidth;
		BOTTOM_PADDING = horse_size * 2 + 10;
		laypar.height = displayWidth + BOTTOM_PADDING; 
		surface.setLayoutParams(laypar);

		holder = surface.getHolder();
		surface.getHolder().addCallback(this);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Accelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, Accelerator,SensorManager.SENSOR_DELAY_GAME);
		
		SharedPreferences settingsPref = getSharedPreferences("settingColor", 0);
		backgroundPaint = new Paint();		
		backgroundPaint.setColor(Color.WHITE);
		horsepaint = new Paint();
		horsepaint.setAntiAlias(true);

		if (settingsPref.getString("horseColor", "Black").equals("Red")) {
			horsepaint.setColor(Color.RED);
		} else if (settingsPref.getString("horseColor", "Black").equals("Blue")) {
			horsepaint.setColor(Color.BLUE);
		}
		else if (settingsPref.getString("horseColor", "Black").equals("Grey")) {
			horsepaint.setColor(Color.GRAY);
		}
		else if (settingsPref.getString("horseColor", "Black").equals("Yellow")) {
			horsepaint.setColor(Color.YELLOW);
		}else {
			horsepaint.setColor(Color.BLACK);
		}

		rightbar_paint = new Paint();
		rightbar_paint.setAntiAlias(true);
		midbar_paint = new Paint();
		midbar_paint.setAntiAlias(true);
		leftbar_paint = new Paint();
		leftbar_paint.setAntiAlias(true);

		if (settingsPref.getString("barrelColor", "Yellow").equals("Red")) {
			leftbar_paint.setColor(Color.RED);
			midbar_paint.setColor(Color.RED);
			rightbar_paint.setColor(Color.RED);
		} else if (settingsPref.getString("barrelColor", "Yellow").equals("Black")) {
			leftbar_paint.setColor(Color.BLACK);
			midbar_paint.setColor(Color.BLACK);
			rightbar_paint.setColor(Color.BLACK);
		} 
		else if (settingsPref.getString("barrelColor", "Yellow").equals("Gray")) {
			leftbar_paint.setColor(Color.GRAY);
			midbar_paint.setColor(Color.GRAY);
			rightbar_paint.setColor(Color.GRAY);
		}
		else if (settingsPref.getString("barrelColor", "Yellow").equals("Yellow")) {
			leftbar_paint.setColor(Color.YELLOW);
			midbar_paint.setColor(Color.YELLOW);
			rightbar_paint.setColor(Color.YELLOW);
		}else {
			leftbar_paint.setColor(Color.BLUE);
			midbar_paint.setColor(Color.BLUE);
			rightbar_paint.setColor(Color.BLUE);
		}

		courtpaint = new Paint();
		courtpaint.setColor(Color.BLACK);
		courtpaint.setAntiAlias(true);
		courtpaint.setStrokeWidth(15);
		// Starting position
		horse_barrel.moveBall(laypar.width / 2, laypar.height-40);

		timeView = (TextView) findViewById(R.id.time);
		try {
			Drawable dim = Drawable.createFromStream(getAssets().open("time_slice.png"), null);
			timeView.setBackgroundDrawable(dim);
		} catch (IOException e) {
			e.printStackTrace();
		}
		play = (ImageButton) findViewById(R.id.play);
		play.setOnClickListener(this);
		reset = (ImageButton) findViewById(R.id.reset);
		reset.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mSensorManager.registerListener(this, Accelerator,SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, Accelerator,SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mSensorManager != null && this != null) {
				mSensorManager.unregisterListener(this);
			}
		} catch (Exception e) {
			System.out.println("exceptions"+e.getMessage());
		}
		horse_barrel.setVibrator(null);
		horse_barrel.setAccel(0, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		horse_barrel.setSize(width, height);}

	public void surfaceCreated(SurfaceHolder holder) {
		draw();}

	private void draw() {
		Canvas c = null;
		try {
			c = holder.lockCanvas();
			if (c != null) {
				CourtDraw(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}

	private void CourtDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		c.drawRect(0, 0, width, height, backgroundPaint); // White background
		c.drawLine(10, 0, 10, height, courtpaint); // left border
		c.drawLine(width - 10, 0, width - 10, height, courtpaint); // right border
		c.drawLine(0, 10, width, 10, courtpaint); // top border
		c.drawLine(0, height - BOTTOM_PADDING, width / 2 - BOTTOM_PADDING / 2,height - BOTTOM_PADDING, courtpaint);
		c.drawLine(width / 2 + BOTTOM_PADDING / 2, height - BOTTOM_PADDING,width, height - BOTTOM_PADDING, courtpaint);
		

		rightbarX = width * 3 / 4;
		rightbarY = height / 4;
		midbarX = width / 2;
		midbarY = height * 2 / 3;
		leftbarX = width / 4;
		leftbarY = height / 4;
		
		System.out.println("leftbarX "+leftbarX);
		System.out.println("leftbarY "+leftbarY);
		System.out.println("rightbarX "+rightbarX);
		System.out.println("rightbarY "+rightbarY);
		System.out.println("midbarX "+midbarX);
		System.out.println("midbarY "+midbarY);

		if (leftbar_color) {
			leftbar_paint.setColor(Color.CYAN);
		}
		c.drawCircle(leftbarX, leftbarY, barrel_size, leftbar_paint);// left barrel

		if (rightbar_color) {
			rightbar_paint.setColor(Color.CYAN);
		}
		c.drawCircle(rightbarX, rightbarY, barrel_size,rightbar_paint);//right barrel
		
		if (midbar_color) {
			midbar_paint.setColor(Color.CYAN);
		}
		c.drawCircle(midbarX, midbarY, barrel_size,midbar_paint);// bottom barrel
		float horseX, horseY;
		synchronized (horse_barrel.LOCK) {
			horseX = horse_barrel.ballPixelX;
			horseY = horse_barrel.ballPixelY;
		}
		c.drawCircle(horseX, horseY, horse_size, horsepaint);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			horse_barrel.setSize(0, 0);
		} finally {
			gamer = null;
		}
	}


	private class GameRun extends Thread {
		private volatile boolean running = true;
		private String currTimeString;

		public void run() {
			while (running) {
				draw();
				horse_barrel.calculatelayout();

				int roundStateChanged[] = new int[3];
				roundStateChanged = horse_barrel.isCompletedCircle();

				if (roundStateChanged[0] == 1) {
					leftbar_color = true;	
				}

				if (roundStateChanged[1] == 1) {
					rightbar_color = true;
				}

				if (roundStateChanged[2] == 1) {
					midbar_color = true;
				}

				if (leftbar_color && rightbar_color && midbar_color) {
					if (horse_barrel.ballPixelX <= (horse_barrel.getPixelWidth() / 2) + 15
							&& horse_barrel.ballPixelX >= (horse_barrel.getPixelWidth() / 2) - 15
							&& horse_barrel.ballPixelY >= horse_barrel.getPixelHeight()/2) {
						timeHandler.removeCallbacks(Threadupdate);

						currTime = horse_barrel.getUpdatedTime();

						if (savedTime > currTime) {
							savedTime = currTime;
						}
						
						int seconds = (int) (currTime / 1000);
						int minutes = seconds / 60;
						seconds = seconds % 60;
						int milliseconds1 = (int) (currTime % 1000);
						currTimeString = new String("" + minutes + ":"+ String.format("%02d", seconds) + ":"+ String.format("%03d", milliseconds1));

						Intent winIntent = new Intent(BarrelRaceActivity.this,Person.class);
						winIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						winIntent.putExtra("win", "win");
						winIntent.putExtra("time", currTimeString);
						String[] RowData = currTimeString.split(":");
						int a = Integer.parseInt(RowData[0]);
						int b = Integer.parseInt(RowData[1]);
						a = a * 60;
						int c = a + b;
						winIntent.putExtra("score", c);
						startActivity(winIntent);
					}
				}
				if (horse_barrel.LooseUser()) {
					timeHandler.removeCallbacks(Threadupdate);
					Intent lost = new Intent(BarrelRaceActivity.this,FinalActivity.class);
					lost.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					lost.putExtra("win", "loose");
					lost.putExtra("time", horse_barrel.getTimeString());
					lost.putExtra("updatedtime", horse_barrel.getUpdatedTime());
					startActivity(lost);
					gamestop();
					finish();
				}
			}
		}

		public void gamestop() {
			running = false;
			interrupt();
		}
	}

	@Override
	public void onClick(View v) {
		int choice = v.getId();
		switch (choice) {
		case R.id.play:
			horse_barrel.setStartTime(SystemClock.uptimeMillis());
			gamer = new GameRun();
			gamer.start();
			Threadupdate = new Runnable() {
				public void run() {
					timeView.setText(horse_barrel.getTimeString());
					timeHandler.postDelayed(this, 0);
				}
			};
			timeHandler.postDelayed(Threadupdate, 0);
			play.setEnabled(false);
			break;
		case R.id.reset:
			startActivity(new Intent(this, BarrelRaceActivity.class));
		}
	}
	@Override
	public void onSensorChanged(SensorEvent evt) {
		if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			if (sensorupdate == -1 || (curTime - sensorupdate) > 50) {
				sensorupdate = curTime;
				horse_barrel.setAccel(-evt.values[0], -evt.values[1]);
			}
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		System.out.println("Accuracy changed");
	}
}
