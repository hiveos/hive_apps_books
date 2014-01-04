package hive.apps.books;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CrtanjeView extends View {

	public static Paint boja;
	public Boolean mijenjan = false;
	public static mojaPutanja putanja;
	private Paint krugBoja;
	private Path krugPutanja;
	public static int LONG_PRESS_TIME = 500;
	public static ArrayList<mojaPutanja> paths = new ArrayList<mojaPutanja>();
	public static ArrayList<mojaPutanja> undonePaths = new ArrayList<mojaPutanja>();
	public static Bitmap MyBitmap;
	public Canvas mCanvas;
	Bitmap kojiKoristimo = null;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		MyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(MyBitmap);
		mCanvas.drawColor(Color.WHITE);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void inicijalizacija(Context k) {
		boja = new Paint();
		krugBoja = new Paint();
		krugPutanja = new Path();

	}

	public void osvjezi() {
		postInvalidate();
	}

	public void ocistiFunkcija() {
		Log.d("hepek", "pozvano");
		for (mojaPutanja p : paths) {
			p.reset();
		}
		mCanvas.drawColor(Color.WHITE);
		mijenjan = true;
		postInvalidate();
	}

	private void postaviKist() {

		boja.setAntiAlias(true);
		boja.setColor(Color.BLUE);
		boja.setStyle(Paint.Style.STROKE);
		boja.setStrokeJoin(Paint.Join.ROUND);
		boja.setStrokeWidth(5f);
		putanja = new mojaPutanja(new Paint(boja));
		paths.add(putanja);

		krugBoja.setAntiAlias(true);
		krugBoja.setColor(Color.CYAN);
		krugBoja.setStyle(Paint.Style.STROKE);
		krugBoja.setStrokeJoin(Paint.Join.MITER);
		krugBoja.setStrokeWidth(4f);
	}

	public CrtanjeView(Context k, AttributeSet set) {
		super(k, set);
		inicijalizacija(k);
		postaviKist();
	}

	public void spreminamStranicu(int brstr) {
		File gdjeSnimiti = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books/");
		if (!gdjeSnimiti.exists())
			gdjeSnimiti.mkdirs();
		File nesto = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Books/" + Glavna.obradjujemo + "/" + brstr + ".jpg");
		FileOutputStream ostream;
		try {
			nesto.createNewFile();
			ostream = new FileOutputStream(nesto);
			MyBitmap.compress(CompressFormat.JPEG, 100, ostream);
			ostream.flush();
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void nacrtajnamCanvas(Bitmap str) {
		kojiKoristimo = str;
		mCanvas.drawBitmap(str, 0, 0, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (mojaPutanja p : paths) {
			canvas.drawPath(p, p.bojaPutanje);
			mCanvas.drawPath(p, p.bojaPutanje);
			canvas.drawPath(krugPutanja, krugBoja);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		float tackaX = e.getX();
		float tackaY = e.getY();
		mijenjan = true;

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			putanja.moveTo(tackaX, tackaY);
			return true;

		case MotionEvent.ACTION_MOVE:

			putanja.lineTo(tackaX, tackaY);
			krugPutanja.reset();
			krugPutanja.addCircle(tackaX, tackaY, 25, Path.Direction.CW);
			break;

		case MotionEvent.ACTION_UP:
			CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			CrtanjeView.paths.add(CrtanjeView.putanja);
			krugPutanja.reset();
			break;

		default:
			return false;
		}

		postInvalidate();
		return true;
	}
}