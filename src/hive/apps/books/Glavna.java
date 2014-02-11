package hive.apps.books;

import hive.apps.books.helpers.HiveHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Glavna extends Activity implements OnClickListener,
		OnLongClickListener, OnRefreshListener {

	private LinearLayout drzacPolica;
	private LinearLayout polica;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	private ArrayList<RelativeLayout> knjige = new ArrayList<RelativeLayout>();
	public static ArrayList<Bitmap> nizStranica = new ArrayList<Bitmap>();
	private int policaCounter;
	private LayoutParams params;
	private LayoutParams bookParams;
	private LayoutParams bookTitleParams;
	public static String sdCard;
	private String imeFajla;
	private int knjigaCounter;
	private RelativeLayout book;
	private ImageView bookCover;
	private TextView bookTitle;
	private Boolean isNeededToLoad = false;
	private int ukupniKnjigaCounter;
	private int policaNaKojojSeNalazimo;
	private int brojKnjigaZaLoadati;
	private File[] brojKnjizica;
	private String foldernoIme;
	public static String obradjujemo = "";
	public static File path;
	public static File[] stranice;
	static int strNaKojojSeNalazimo = 1;
	static Bitmap bmpStranica;

	View selectedItem;

	private int ItemId;
	protected Object mActionMode;

	String[] mBooks;
	ArrayList<String> mBookIds = new ArrayList<String>();
	ArrayList<String> mBookNames = new ArrayList<String>();

	public static final String SHELF_STYLE = "shelfstyle";

	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glavna);

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		new FetchTask().execute();

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isNetworkAvailable()) {
			Intent mNoNetworkIntent = new Intent();
			mNoNetworkIntent.setAction("hive.action.General");
			mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
			sendBroadcast(mNoNetworkIntent);
			finish();
		}
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
	public void onRefreshStarted(View view) {
		new FetchTask().execute();
	}

	private void inicijaliziraj() {
		bmpStranica = null;
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

		brojKnjigaZaLoadati = mBooks.length;
	}

	@SuppressLint("ResourceAsColor")
	private void dodajPolicu() {
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
		police.add(polica);
		policaCounter++;
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		polica.setLayoutParams(params);
		polica.setGravity(Gravity.CENTER_HORIZONTAL);

		if (getShelfStyle().equals("no")) {
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
		}
		if (getShelfStyle().equals("simple")) {
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
			polica.setBackgroundResource(R.drawable.shelf_simple);
		}
		if (getShelfStyle().equals("wooden")) {
			params.topMargin = 0;
			params.leftMargin = 0;
			params.rightMargin = 0;
			polica.setPadding(50, 20, 50, 21);
			polica.setBackgroundResource(R.drawable.shelf_wooden);
		}

		drzacPolica.addView(polica);
	}

	public String getShelfStyle() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		return settings.getString(SHELF_STYLE, "");
	}

	private void dodajKnjigu(int position) {
		int max;
		if (isTablet(this)) {
			max = 4;
		} else {
			max = 2;
		}

		if (knjigaCounter < max) {
			bookCover = new ImageView(this);
			bookTitle = new TextView(this);
			bookCover.setOnClickListener(this);
			bookCover.setOnLongClickListener(this);
			bookCover.setId(ukupniKnjigaCounter);
			bookCover.setPadding(1, 1, 1, 1);

			book = new RelativeLayout(this);
			book.setBackgroundResource(R.drawable.book_bg);
			book.setGravity(Gravity.TOP);

			bookParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			book.setOnClickListener(this);
			book.setOnLongClickListener(this);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(
					Environment.getExternalStorageDirectory() + "/HIVE/Books/"
							+ mBookIds.get(position) + "/page1.png", options);
			Bitmap smanjeni;
			if (isTablet(this)) {
				smanjeni = Bitmap.createScaledBitmap(bitmap, 340, 450, false);
			} else {
				smanjeni = Bitmap.createScaledBitmap(bitmap, 260, 370, false);
			}
			bookCover.setImageBitmap(smanjeni);

			bookTitle.setBackgroundResource(R.drawable.book_title_bg);
			bookTitle.setPadding(10, 5, 10, 20);
			bookTitle.setTextAppearance(this, R.style.RobotoTextViewStyleLight);
			bookTitleParams = new LayoutParams(smanjeni.getWidth(),
					LayoutParams.WRAP_CONTENT);
			bookTitle.setLayoutParams(bookTitleParams);
			bookTitle.setText(mBookNames.get(position));

			book.addView(bookCover);
			book.addView(bookTitle);

			knjige.add(book);
			knjigaCounter++;

			ukupniKnjigaCounter++;

			bookParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			bookParams.leftMargin = 15;
			bookParams.rightMargin = 15;
			bookParams.bottomMargin = 0;
			bookParams.topMargin = 0;
			police.get(policaNaKojojSeNalazimo).addView(
					knjige.get(ukupniKnjigaCounter - 1), bookParams);

		} else if (knjigaCounter >= max) {
			if (ukupniKnjigaCounter >= 20)
				dodajPolicu();
			policaNaKojojSeNalazimo++;
			knjigaCounter = 0;
			dodajKnjigu(position);
		}
	}

	private void loadajKnjige() {
		for (int i = 0; i < mBooks.length; i++) {
			isNeededToLoad = true;
			dodajKnjigu(i);
		}

	}

	@Override
	public boolean onLongClick(View arg0) {
		return false;
	}

	public int uporediMe(Object a, Object b) {
		return Float.valueOf(a.toString()).compareTo(
				Float.valueOf(b.toString()));
	}

	public static void ucitajStranice() {
		stranice = path.listFiles();
		Arrays.sort(stranice);

		for (int i = 0; i < stranice.length; i++) {
			for (int x = 1; x < stranice.length - i; x++) {
				if (Integer.parseInt(stranice[x - 1].getName().substring(0,
						stranice[x - 1].getName().lastIndexOf('.'))) > Integer
						.parseInt(stranice[x].getName().substring(0,
								stranice[x].getName().lastIndexOf('.')))) {
					File temp = stranice[x - 1];
					stranice[x - 1] = stranice[x];
					stranice[x] = temp;

				}
			}
		}

		System.out.println(stranice.length + "");
		int counter = 0;
		if (stranice[strNaKojojSeNalazimo - 1].isFile()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 0;
			bmpStranica = BitmapFactory.decodeFile(
					stranice[strNaKojojSeNalazimo - 1].getAbsolutePath(),
					options);
		}
		System.out.println(nizStranica.size() + " ");
	}

	@Override
	public void onClick(View arg0) {
		for (int i = 0; i < mBooks.length; i++) {
			if (mBookNames.get(i).equals(bookTitle.getText().toString())) {
				obradjujemo = mBookIds.get(i);
			}
		}
		path = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books/" + obradjujemo + "/");
		ucitajStranice();
		Intent intent = new Intent(Glavna.this, PageScroller.class);
		startActivity(intent);

	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private class FetchTask extends AsyncTask<String, Integer, String> {

		String response;
		boolean isEmpty = true;

		@Override
		protected String doInBackground(String... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String FetchUrl = "http://hive.bluedream.info/api/"
					+ mHiveHelper.getUniqueId() + "/books";

			if (isNetworkAvailable()) {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(FetchUrl);
					HttpResponse responseGet;
					responseGet = client.execute(get);
					HttpEntity resEntityGet = responseGet.getEntity();

					if (resEntityGet != null) {
						response = EntityUtils.toString(resEntityGet);
					} else {

					}

					extractData();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Intent mNoNetworkIntent = new Intent();
				mNoNetworkIntent.setAction("hive.action.General");
				mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
				sendBroadcast(mNoNetworkIntent);
				finish();
			}

			return null;
		}

		private void extractData() {
			clearUp();

			if (!response.equals("")) {
				mBooks = response.split(";");

				for (int i = 0; i < mBooks.length; i++) {
					mBookIds.add(mBooks[i].substring(
							mBooks[i].indexOf("id=") + 3,
							mBooks[i].indexOf(",name")));
					mBookNames.add(mBooks[i].substring(
							mBooks[i].indexOf("name=") + 5,
							mBooks[i].indexOf(",cover")));
					isEmpty = false;
				}
			}
		}

		private void clearUp() {
			Glavna.this.runOnUiThread(new Runnable() {
				public void run() {
					drzacPolica = (LinearLayout) findViewById(R.id.ShelfHolder);
					drzacPolica.removeAllViews();
					// LinearLayout NoNotebook = (LinearLayout)
					// findViewById(R.id.no_notebook);
					// NoNotebook.setVisibility(View.GONE);
				}
			});
			clearArrays();
		}

		private void clearArrays() {
			mBookIds.clear();
			mBookNames.clear();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (isNetworkAvailable()) {
				if (!isEmpty) {
					addNotebooks();
				} else {
					displayNoNotebooks();
				}
			}
			mPullToRefreshLayout.setRefreshComplete();
		}

		private void addNotebooks() {
			inicijaliziraj();

			for (int i = 0; i < 5; i++)
				dodajPolicu();

			if (brojKnjigaZaLoadati != 0)
				loadajKnjige();
		}

		private void displayNoNotebooks() {
			Glavna.this.runOnUiThread(new Runnable() {
				public void run() {
					// LinearLayout NoNotebook = (LinearLayout)
					// findViewById(R.id.no_notebook);
					// NoNotebook.setVisibility(View.VISIBLE);
				}
			});
		}

	}
}
