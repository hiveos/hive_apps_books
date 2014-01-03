package hive.apps.books;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Glavna extends Activity implements OnClickListener,
		OnLongClickListener {

	private Button openButton;
	private LinearLayout drzacPolica;
	private LinearLayout polica;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	private ArrayList<MyButton> knjige = new ArrayList<MyButton>();
	public static ArrayList<Bitmap> nizStranica = new ArrayList<Bitmap>();
	private int policaCounter;
	private LayoutParams params;
	private LayoutParams knjigaParams;
	public static String sdCard;
	private String imeFajla;
	private int knjigaCounter;
	private MyButton knjiga;
	private Boolean isNeededToLoad = false;
	private int ukupniKnjigaCounter;
	private int policaNaKojojSeNalazimo;
	private int brojKnjigaZaLoadati;
	private File[] brojKnjizica;
	private String foldernoIme;
	public static String obradjujemo = "";
	public static File path;
	public File[] stranice;
	private LinearLayout emptyspace;

	View selectedItem;

	private int ItemId;
	protected Object mActionMode;

	public static final String SHELF_STYLE = "shelfstyle";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glavna);

		File BooksDir = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books");
		if (!BooksDir.exists())
			BooksDir.mkdirs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.glavna, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent goToSettings = new Intent(Glavna.this,
					SettingsActivity.class);
			Glavna.this.startActivity(goToSettings);
			break;
		default:
			return false;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		inicijaliziraj();

		for (int i = 0; i < 5; i++)
			dodajPolicu();

		if (brojKnjigaZaLoadati != 0)
			loadajKnjige();
	}

	private void inicijaliziraj() {
		sdCard = Environment.getExternalStorageDirectory().toString();
		policaCounter = 0;
		drzacPolica = (LinearLayout) findViewById(R.id.ShelfHolder);
		police.clear();
		knjige.clear();
		drzacPolica.removeAllViews();
		policaCounter = 0;
		knjigaCounter = 0;
		policaNaKojojSeNalazimo = 0;
		ukupniKnjigaCounter = 0;
		nizStranica.clear();

		File booksRoot = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books/");
		if (!booksRoot.exists())
			booksRoot.mkdirs();

		brojKnjigaZaLoadati = (new File(
				Environment.getExternalStorageDirectory() + "/HIVE/Books/")
				.listFiles().length);
	}

	@SuppressLint("ResourceAsColor")
	private void dodajPolicu() {
		emptyspace = (LinearLayout) findViewById(R.id.space);
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
		police.add(polica);
		policaCounter++;
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		polica.setLayoutParams(params);
		polica.setGravity(Gravity.CENTER_HORIZONTAL);
		emptyspace.setBackgroundResource(R.color.background);

		if (getShelfStyle().equals("no")) {
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
			emptyspace.setBackgroundResource(R.color.background);
		}
		if (getShelfStyle().equals("simple")) {
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
			polica.setBackgroundResource(R.drawable.shelf_simple);
			emptyspace.setBackgroundResource(R.color.background);
		}
		if (getShelfStyle().equals("wooden")) {
			params.topMargin = 0;
			params.leftMargin = 0;
			params.rightMargin = 0;
			polica.setPadding(50, 20, 50, 0);
			polica.setBackgroundResource(R.drawable.shelf_wooden);
			emptyspace.setBackgroundResource(R.drawable.shelf_wooden_empty);
		}

		drzacPolica.addView(polica);
	}

	public String getShelfStyle() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		return settings.getString(SHELF_STYLE, "");
	}

	private void dodajKnjigu() {
		if (knjigaCounter < 3) {
			Log.d("Ime foldera:", foldernoIme);
			knjiga = new MyButton(this, foldernoIme);
			knjiga.setOnClickListener(this);
			knjiga.setOnLongClickListener(this);
			knjiga.setId(ukupniKnjigaCounter);

			String imeKnjigeBezEkstenzije = knjiga.imeKnjige;
			Log.d("IME KNJIGE BEZ EKSTENZIJE", imeKnjigeBezEkstenzije);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(sdCard + "/HIVE/Books/"
					+ foldernoIme + "/1.jpg", options);
			Bitmap smanjeni = Bitmap
					.createScaledBitmap(bitmap, 180, 272, false);

			knjiga.setImageBitmap(smanjeni);
			knjige.add(knjiga);
			knjigaCounter++;
			Log.d("Knjiga counter:", knjigaCounter + "");
			ukupniKnjigaCounter++;
			knjigaParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			knjigaParams.leftMargin = 15;
			knjigaParams.rightMargin = 15;
			knjigaParams.bottomMargin = 0;
			knjigaParams.topMargin = 0;
			police.get(policaNaKojojSeNalazimo).addView(
					knjige.get(ukupniKnjigaCounter - 1), knjigaParams);
		} else if (knjigaCounter >= 3) {
			if (ukupniKnjigaCounter >= 20)
				dodajPolicu();
			policaNaKojojSeNalazimo++;
			knjigaCounter = 0;
			dodajKnjigu();
		}
	}

	private void loadajKnjige() {
		brojKnjizica = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books/").listFiles();

		for (File infile : brojKnjizica) {
			if (infile.isDirectory()) {
				foldernoIme = infile.getName();
				isNeededToLoad = true;
				dodajKnjigu();
			}
		}

	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void ucitajStranice() {
		stranice = path.listFiles();
		System.out.println(stranice.length + "");

		for (File stranica : stranice) {
			if (stranica.isFile()) {
				Bitmap bmpStranica = BitmapFactory.decodeFile(stranica
						.getAbsolutePath());
				nizStranica.add(bmpStranica);
			}
		}
		System.out.println(nizStranica.size() + " ");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		MyButton trenutno = (MyButton) arg0;
		for (int i = 0; i < knjige.size(); i++) {
			if (knjige.get(i).imeKnjige.toString().equals(
					trenutno.imeKnjige.toString())) {
				obradjujemo = knjige.get(i).imeKnjige.toString();
			}
		}
		path = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books/" + obradjujemo + "/");
		Log.d("Obradjujemo:", path.getAbsolutePath().toString());
		ucitajStranice();
		Intent intent = new Intent(Glavna.this, PageScroller.class);
		startActivity(intent);

	}

}
