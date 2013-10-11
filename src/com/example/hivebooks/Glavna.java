package com.example.hivebooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Glavna extends Activity implements OnClickListener{
	
	private Button openButton;
	private LinearLayout drzacPolica;
	private LinearLayout polica;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	private int policaCounter;
	private LayoutParams parametri;
	public static String sdCard;
	private String imeFajla;
	
	
	private void inicijaliziraj()
	{
		sdCard = Environment.getExternalStorageDirectory().toString();
		policaCounter=0;
		drzacPolica=(LinearLayout)findViewById(R.id.ShelfHolder);
		openButton=(Button)findViewById(R.id.button1);
		openButton.setOnClickListener(this);
		police.clear();
        drzacPolica.removeAllViews();
	}
	
	private void dodajPolicu()
	{
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
        polica.setBackgroundResource(R.drawable.shelf);
        police.add(polica);
        policaCounter++;
        parametri = new LayoutParams(LayoutParams.MATCH_PARENT, 210);
        polica.setLayoutParams(parametri);
        drzacPolica.addView(polica);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glavna);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.glavna, menu);
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inicijaliziraj();
		
		for (int i = 0; i < 5; i++)
            dodajPolicu();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
			case R.id.button1:
				Intent openFileManager = new Intent(Glavna.this,MainActivity.class);
				Glavna.this.startActivity(openFileManager);
				break;
		}
	}

}
