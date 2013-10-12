package hive.apps.books;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Glavna extends Activity implements OnClickListener, OnLongClickListener{

	private Button openButton;
	private LinearLayout drzacPolica;
	private LinearLayout polica;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	private ArrayList<MyButton> knjige = new ArrayList<MyButton>();
	private int policaCounter;
	private LayoutParams parametri;
	private LayoutParams knjigaParams;
	public static String sdCard;
	private String imeFajla;
	private int knjigaCounter;
	private MyButton knjiga;
	private Boolean isNeededToLoad=false;
	private int ukupniKnjigaCounter;
	private int policaNaKojojSeNalazimo;
	private int brojKnjigaZaLoadati;
	private File[] brojKnjizica;
	private String foldernoIme;

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
        
        brojKnjigaZaLoadati = new File(
                Environment.getExternalStorageDirectory() + "/HIVE/Books/")
                .listFiles().length;
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
	
	private void dodajKnjigu(){
		if(knjigaCounter<3)
		{
			knjiga=new MyButton(this, foldernoIme);
			knjiga.setOnClickListener(this);
			knjiga.setOnLongClickListener(this);
			knjiga.setId(ukupniKnjigaCounter);
			String imeKnjigeBezEkstenzije = knjiga.imeKnjige.substring(0, knjiga.imeKnjige.lastIndexOf('.'));
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(sdCard + "/HIVE/Books/"
					+ imeKnjigeBezEkstenzije +".png", options);			
			
			knjiga.setImageBitmap(bitmap);
			
			knjige.add(knjiga);
			knjigaCounter++;
			ukupniKnjigaCounter++;
			knjigaParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
			knjigaParams.leftMargin = 30;
            knjigaParams.bottomMargin = 20;
            knjigaParams.topMargin = 15;
            police.get(policaNaKojojSeNalazimo).addView(
                    knjige.get(ukupniKnjigaCounter - 1), knjigaParams);
		}
		else if(knjigaCounter>=3)
		{
			if (ukupniKnjigaCounter >= 20)
                dodajPolicu();
				policaNaKojojSeNalazimo++;
				knjigaCounter=0;
				dodajKnjigu();
		}
	}
	
	private void loadajKnjige() {
		// TODO Auto-generated method stub
		brojKnjizica = new File(Environment.getExternalStorageDirectory()
                + "/HIVE/Books/").listFiles();
		
		for (File infile : brojKnjizica) {
			String ekstenzijaFajla = infile.getName().substring(
					(infile.getName().lastIndexOf(".") + 1), infile.getName().length());
			
			
            if (infile.isFile() && ekstenzijaFajla.equals("pdf")) {
            		foldernoIme = infile.getName();
                    isNeededToLoad = true;
                    dodajKnjigu();
            }
		}
		
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
		
		if (brojKnjigaZaLoadati != 0)
            loadajKnjige();
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		MyButton trenutno=(MyButton)arg0;
		String obradjujemo = "";
		for(int i=0; i<knjige.size(); i++)
		{
			if(knjige.get(i).imeKnjige.toString().equals(trenutno.imeKnjige.toString())){
				obradjujemo=knjige.get(i).imeKnjige.toString();
			}
		}
		File targetLocation = new File(Glavna.sdCard + "/HIVE/Books/"
				+ obradjujemo);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(targetLocation),
				"application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		
	}

}
