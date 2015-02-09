package com.horse.barrel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

// ArrayAdapter is a type of Adapter that works a lot like ArrayList.

public class reafile extends ArrayAdapter<Scoredetails>{
	Context ctx;
	//We must accept the textViewResourceId parameter.
	public reafile(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.ctx = context;
		//Load the data.
			loadArrayFromFile();
	}
	//getView() is the method responsible for building a View out of data. - Rajesh Sajjan
	@Override
	public View getView(final int pos, View convertView, final ViewGroup parent){
		TextView mView = (TextView)convertView;
		//If convertView was null then we have to create a new TextView.
		//If it was not null then we'll use it by setting the appropriate text String to it.
		if(null == mView){ 
			mView = new TextView(parent.getContext());
			mView.setTextSize(20);
		}
		//Set the state name as the text.
		String a = getItem(pos).getFirstname();
		String b = getItem(pos).getScore();
		String c = a+"\t\t"+b;
		mView.setText(c);
		return mView;
	}
	
	//Function to load data from file - Nisha Halyal
	private void loadArrayFromFile(){
		try {		
			// Get input stream and Buffered Reader for our data file.
			String FILENAME = "Output";
			FileInputStream fileIn= ctx.openFileInput(FILENAME);
			InputStreamReader InputRead= new InputStreamReader(fileIn);
			BufferedReader reader = new BufferedReader(InputRead);
			String line;		
			//Read each line
			while ((line = reader.readLine()) != null) {
				//Split to separate the fields
				String[] RowData = line.split(",");
				//Create a object for this row's data.
				Scoredetails cur = new Scoredetails();
				cur.setFirstname(RowData[0]);
				cur.setScore(RowData[1]);
				//Add the object to the ArrayList (in this case we are the ArrayList).
				this.add(cur);
			}
		InputRead.close();
		fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
