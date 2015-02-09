package com.horse.barrel;

import java.io.IOException;

import android.R.color;
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
 * Class Name: MAinActivity.java
 * This activity presents the starting screen of the game. On clicking start button it will show menu page.
 * 
 */
public class MainActivity extends Activity implements OnClickListener {
	private ImageButton start;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start = (ImageButton) findViewById(R.id.images_button);
		start.setOnClickListener(this);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout);
		try {
			Drawable d = Drawable.createFromStream(
					getAssets().open("Barrel_Horse.png"), null);
			layout.setBackgroundColor(color.holo_blue_bright);
			layout.setBackgroundDrawable(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.images_button:
			startActivity(new Intent(MainActivity.this, Home.class));

			break;

	}
}
}
