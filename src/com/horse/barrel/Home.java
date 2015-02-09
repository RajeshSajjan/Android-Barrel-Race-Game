package com.horse.barrel;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.horse.barrel.R;

/**
 * Class Name: Home.java
 * 
 * 	This activity shows the menu with few image button for start the game, view highscores and settings page.
 */
public class Home extends Activity implements OnClickListener {

	private ImageButton start_image;
	private ImageButton highscore_image;
	private ImageButton settings_image;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		start_image = (ImageButton) findViewById(R.id.start);
		start_image.setOnClickListener(this);

		highscore_image = (ImageButton) findViewById(R.id.highscore);
		highscore_image.setOnClickListener(this);

		settings_image = (ImageButton) findViewById(R.id.settings);
		settings_image.setOnClickListener(this);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout);

		try {
			Drawable diagram = Drawable.createFromStream(getAssets().open("background.png"), null);
			//layout.setBackgroundColor(color.holo_blue_bright);
			layout.setBackgroundDrawable(diagram);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void onClick(View v) {
		int choice=v.getId();
		switch (choice) {
		case R.id.start:
			Intent intent = new Intent(this, BarrelRaceActivity.class);
			//intent.putExtra(EXTRA, position);
		    startActivity(intent);
			break;

		case R.id.highscore:
			Intent intent12 = new Intent(this, HighScore.class);
			//intent.putExtra(EXTRA, position);
		    startActivity(intent12);
			break;

		case R.id.settings:
			Intent intent13 = new Intent(this, SettingsActivity.class);
			//intent.putExtra(EXTRA, position);
		    startActivity(intent13);
			break;
		}
	}
}
