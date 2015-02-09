package com.horse.barrel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.horse.barrel.R;

/**
 * Class Name: FinalActivity.java
 * This activity presents the final screen once user has either won or lost the game
 * 
 */
public class FinalActivity extends Activity implements OnClickListener {

	private Button replay;
	private Button home;

	String won = "Congratulations..You Won";
	String lost = "Game Over..You Loose";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);
		
		replay = (Button) findViewById(R.id.replay);
		replay.setOnClickListener(this);

		home = (Button) findViewById(R.id.home1);
		home.setOnClickListener(this);
		
		@SuppressWarnings("unused")
		TextView scored = (TextView) findViewById(R.id.score);

		TextView scoredView = (TextView) findViewById(R.id.scoreView);
		String time = (String) getIntent().getExtras().get("time");

		TextView highview = (TextView) findViewById(R.id.highscoreView);
		
		SharedPreferences highPref = getSharedPreferences("findhighscore", 0);
		String score = highPref.getString("highscore", "NO SCORE");
		
		highview.setText(score);
		highview.setTextColor(Color.GREEN);
		
		TextView resultView = (TextView) findViewById(R.id.result);

		String a;
		a=(String) getIntent().getExtras().get("win");
		if ( a.equals("win")) {
			resultView.setText(won);
			scoredView.setText(time);
			scoredView.setTextColor(Color.GREEN);
			resultView.setTextColor(Color.GREEN);
		} else {
			resultView.setText(lost);
			scoredView.setText(time);
			scoredView.setTextColor(Color.RED);
			resultView.setTextColor(Color.RED);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.replay:
			Intent intent1 = new Intent(this, BarrelRaceActivity.class);
			//intent.putExtra(EXTRA, position);
		    startActivity(intent1);
			finish();
			break;
		
		case R.id.home1:
			Intent intent11 = new Intent(this, Home.class);
			//intent.putExtra(EXTRA, position);
		    startActivity(intent11);
		}
	}
}
