package com.horse.barrel;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.horse.barrel.R;
/**
 * Class Name: Highscore.java
 * This activity presents the Highscore screen which has a list of players name an their score. Sorted with highest scorer at top.
 * 
 */
public class HighScore extends Activity {
	reafile mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		ListView mList = (ListView)findViewById(R.id.listView1);
		
		//Create Adapter
		System.out.println("here");
		mAdapter = new reafile(this, -1);
		
		//attach our Adapter to the ListView. This will populate all of the rows.
		mList.setAdapter(mAdapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.high_score, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
