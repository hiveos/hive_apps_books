package com.example.hivebooks;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	String bookName;
	EditText edittekst;
	Button button1;
	File bookRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edittekst=(EditText)findViewById(R.id.editText1);
		button1=(Button)findViewById(R.id.button1);
		
		bookRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HIVE/Books/");
		if(!bookRoot.exists()){
			bookRoot.mkdirs();
		}
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bookName=edittekst.getText().toString();
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HIVE/Books/"+bookName+".pdf");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
