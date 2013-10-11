package com.example.hivebooks;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Glavna extends Activity implements OnClickListener{
	
	Button openButton;
	
	private void inicijaliziraj()
	{
		openButton=(Button)findViewById(R.id.button1);
		openButton.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glavna);
		inicijaliziraj();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.glavna, menu);
		return true;
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
