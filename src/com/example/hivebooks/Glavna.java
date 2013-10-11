package com.example.hivebooks;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Glavna extends Activity {

	private Button openButton;
	private LinearLayout drzacPolica;
	private LinearLayout polica;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	private int policaCounter;
	private LayoutParams parametri;
	public static String sdCard;
	private String imeFajla;

	private void inicijaliziraj() {
		sdCard = Environment.getExternalStorageDirectory().toString();
		policaCounter = 0;
		drzacPolica = (LinearLayout) findViewById(R.id.ShelfHolder);
		police.clear();
		drzacPolica.removeAllViews();
	}

	private void dodajPolicu() {
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
		getMenuInflater().inflate(R.menu.glavna, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_open_pdf:
			Intent openFileManager = new Intent(Glavna.this, MainActivity.class);
			Glavna.this.startActivity(openFileManager);
			break;
		default:
			return false;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inicijaliziraj();

		for (int i = 0; i < 5; i++)
			dodajPolicu();
	}

}
