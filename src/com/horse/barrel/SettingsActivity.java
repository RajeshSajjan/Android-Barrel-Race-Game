package com.horse.barrel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.horse.barrel.R;
/**
 * Class Name: SettingsActivity.java
 * This activity presents the settings screen where player can select color of barrel and horse.
 * 
 */
public class SettingsActivity extends Activity implements
		OnItemSelectedListener {
	
	private Spinner horseSetting;
	private Spinner barrelSetting;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		SharedPreferences settingsPref = getSharedPreferences("settingColor", 0);
		editor = settingsPref.edit();
		
		barrelSetting = (Spinner) findViewById(R.id.barrel);
		horseSetting = (Spinner) findViewById(R.id.horse);
		
		ArrayAdapter<CharSequence> adapterBarrel = ArrayAdapter.createFromResource(this, R.array.barrel_color_array,android.R.layout.simple_spinner_item);
		adapterBarrel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		barrelSetting.setAdapter(adapterBarrel);

		ArrayAdapter<CharSequence> adapterHorse = ArrayAdapter.createFromResource(this, R.array.barrel_color_array,android.R.layout.simple_spinner_item);
		adapterHorse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		horseSetting.setAdapter(adapterHorse);

		barrelSetting.setOnItemSelectedListener(this);
		horseSetting.setOnItemSelectedListener(this);	
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		int choice=parent.getId();
		switch (choice) {
		case R.id.barrel:
			editor.putString("barrelColor", parent.getItemAtPosition(pos).toString());
			editor.commit();
			break;
		case R.id.horse:
			editor.putString("horseColor", parent.getItemAtPosition(pos).toString());
			editor.commit();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
