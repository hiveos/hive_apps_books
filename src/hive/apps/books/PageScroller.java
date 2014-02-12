package hive.apps.books;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PageScroller extends Activity implements OnClickListener {

	private Menu menu;
	Stranica stranicaView;
	static Bitmap izgledStranice;
	Button nextButton, previousButton;
	static Boolean drawing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_scroller);
		ucitajStranicu(1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		stranicaView = (Stranica) findViewById(R.id.stranicaView);
		nextButton = (Button) findViewById(R.id.buttonNext);
		previousButton = (Button) findViewById(R.id.buttonPrevious);
		nextButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);
		ucitajStranicu(Glavna.strNaKojojSeNalazimo);
		postaviStranicu();
	}

	void postaviStranicu() {
		stranicaView.setImageBitmap(Glavna.bmpStranica);
	}

	void spremiStranicu(int brStranice) {
		File gdjeSpremiti = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Book_Drawings/"+Glavna.obradjujemo+"/");
		if (!gdjeSpremiti.exists())
			gdjeSpremiti.mkdirs();
		File stranica = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Book_Drawings/"+Glavna.obradjujemo+"/page" + brStranice + ".png");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(stranica);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Stranica.MyBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		Stranica.mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
	}

	void ucitajStranicu(int brStranice) {
		File odakleUcitati = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Book_Drawings/"+Glavna.obradjujemo+"/");
		if (!odakleUcitati.exists())
			odakleUcitati.mkdirs();
		File stranica = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Book_Drawings/"+Glavna.obradjujemo+"/page" + brStranice + ".png");
		if (stranica.exists())
			Glavna.LoadaniDrawing = BitmapFactory.decodeFile(stranica
					.getAbsolutePath());
		else {
			Glavna.LoadaniDrawing = Bitmap.createBitmap(10, 10,
					Bitmap.Config.ARGB_8888);
			Glavna.LoadaniDrawing.recycle();
		}
	}
	
	public void lijevo() {
		spremiStranicu(Glavna.strNaKojojSeNalazimo);
		Glavna.strNaKojojSeNalazimo++;
		Log.d("NALAZIMO SE NA: ", Glavna.strNaKojojSeNalazimo + "");
		if (Glavna.strNaKojojSeNalazimo <= Glavna.stranice.length) {
			Glavna.ucitajStranice();
			postaviStranicu();
			ucitajStranicu(Glavna.strNaKojojSeNalazimo);
		} else
			Glavna.strNaKojojSeNalazimo = Glavna.stranice.length;
		Stranica.paths.clear();
		ucitajStranicu(Glavna.strNaKojojSeNalazimo);
	}

	public void desno() {
		spremiStranicu(Glavna.strNaKojojSeNalazimo);
		Glavna.strNaKojojSeNalazimo--;
		Log.d("NALAZIMO SE NA: ", Glavna.strNaKojojSeNalazimo + "");
		if (Glavna.strNaKojojSeNalazimo >= 1) {
			Glavna.ucitajStranice();
			postaviStranicu();
			ucitajStranicu(Glavna.strNaKojojSeNalazimo);
		} else
			Glavna.strNaKojojSeNalazimo = 1;
		Stranica.paths.clear();
		ucitajStranicu(Glavna.strNaKojojSeNalazimo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.page_scroller, menu);
		setTitle(Glavna.obradjujemo);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		MenuItem fullscreenItem = menu.findItem(R.id.action_fullscreen);
		MenuItem nextItem = menu.findItem(R.id.action_next);
		MenuItem previousItem = menu.findItem(R.id.action_previous);

		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

		switch (item.getItemId()) {
		case R.id.action_previous:
			desno();
			return true;
		case R.id.action_next:
			lijevo();
			return true;
		case R.id.action_fullscreen:
			if (!isImmersiveModeEnabled) {
				fullscreenItem.setIcon(R.drawable.ic_disable_fullscreen);
				fullscreenItem.setTitle("Show System Bars");
				toggleImmersive();
			} else if (isImmersiveModeEnabled) {
				fullscreenItem.setIcon(R.drawable.ic_fullscreen);
				fullscreenItem.setTitle("Hide System Bars");
				toggleImmersive();
			}
			return true;
		case R.id.action_switch:
			if (drawing) {
				drawing = false;
			} else {
				drawing = true;
				Stranica.paths.add(Stranica.putanja);
			}
			return true;
		case R.id.action_goTo:
			postaviDialog();
			return true;
		default:
			return false;

		}
	}
	
	

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		spremiStranicu(Glavna.strNaKojojSeNalazimo);
		super.onStop();
	}

	private void postaviDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Jump to page:");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Glavna.strNaKojojSeNalazimo = Integer.parseInt(input.getText()
						.toString());
				if (Glavna.strNaKojojSeNalazimo > 1
						&& Glavna.strNaKojojSeNalazimo <= Glavna.stranice.length) {
					Glavna.ucitajStranice();
					postaviStranicu();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();

	}

	public void toggleImmersive() {

		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
		if (isImmersiveModeEnabled) {
			Log.i("TAG", "Turning immersive mode mode off. ");
			getActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			getActionBar().show();
		} else {
			Log.i("TAG", "Turning immersive mode mode on.");
			getActionBar().show();
		}

		if (Build.VERSION.SDK_INT >= 14) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		if (Build.VERSION.SDK_INT >= 16) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		if (Build.VERSION.SDK_INT >= 18) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonNext:
			lijevo();
			break;
		case R.id.buttonPrevious:
			desno();
			break;
		default:
			break;

		}
	}
}
