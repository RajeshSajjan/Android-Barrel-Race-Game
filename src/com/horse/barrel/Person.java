package com.horse.barrel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.horse.barrel.R;
/**
 * Class Name: person.java
 * This activity comes up if the player wins the game, it asks to store the name.
 * 
 */
public class Person extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
	}
	
	
	public void sendMessage(View view) throws IOException {
        // Do something in response to button
    	EditText firstname = (EditText) findViewById(R.id.first_name);
    	if(firstname.getText().toString().equals(""))
    	{
    		Toast.makeText(view.getContext(),"Firstname cannot be null", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	String time =  (String) getIntent().getExtras().get("time");
    	int timer =   (Integer) getIntent().getExtras().get("score");
    	String message = firstname.getText().toString()+","+time+","+timer+"\n";
    	
    	
    	String FILENAME = "Output";
    	/*
    	FileInputStream ileIn= openFileInput(FILENAME);
		InputStreamReader InputRead= new InputStreamReader(ileIn);
		BufferedReader reader = new BufferedReader(InputRead);
		String line;
		int count=0;
		while ((line = reader.readLine()) != null) {
			count++;
		}
		reader.close();
		InputRead.close();
		
		if ( count  < 1)
		{
    	FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
    	fos.write(message.getBytes());
    	fos.close();
		}
		else{ */
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
	    	fos.write(message.getBytes());
	    	fos.close();
			
			FileInputStream ileIn1= openFileInput(FILENAME);
			InputStreamReader InputRead1= new InputStreamReader(ileIn1);		
			BufferedReader reader1 = new BufferedReader(InputRead1);
			
	        Map<String, String> map=new TreeMap<String, String>();
	        String line1="";
	        while((line1=reader1.readLine())!=null){
	                map.put(getField(line1),line1);
	        }
	        reader1.close();
	        InputRead1.close();
	        
	        
	        //BufferedWriter writer = new BufferedWriter(new FileWriter("fileToWrite1.txt"));
	        FileOutputStream fos1 = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	        for(String val : map.values()){
	                fos1.write(val.getBytes());      
	                fos1.write("\n".getBytes());
	                
	       // }
	       
		}
	        fos1.close();
		
    	System.out.println("Wrote successfully");
    	
    	String currTimeString = (String) getIntent().getExtras().get("time");
    	Intent intent = new Intent(this, FinalActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.putExtra("win", "win");
		intent.putExtra("time", currTimeString);
    	startActivity(intent);
    }


	private String getField(String line1) {
		// TODO Auto-generated method stub
		String[] Rowdata=line1.split(",");
        return Rowdata[2];
	}
	
}
