package hive.apps.books;

import java.util.ArrayList;
import java.util.List;

import hive.apps.books.R.id;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PageScroller extends Activity {

	ViewFlipper viewFlipper;
	public Boolean isEditable;
	CrtanjeView crtanjeView;
	Bitmap bip;
	int brr = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_scroller);

//		final View decorView = getWindow().getDecorView();
//		decorView
//				.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//					@Override
//					public void onSystemUiVisibilityChange(int i) {
//						int height = decorView.getHeight();
//						Log.i("TAG", "Current height: " + height);
//					}
//				});

		toggleHideyBar();
	}

	public void toggleHideyBar() {

		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
		if (isImmersiveModeEnabled) {
			Log.i("TAG", "Turning immersive mode mode off. ");
			getActionBar().show();
		} else {
			Log.i("TAG", "Turning immersive mode mode on.");
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isEditable = false;
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		crtanjeView = (CrtanjeView) findViewById(R.id.crtanje);
		postaviViewFlipper();
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasFocus) {
	// super.onWindowFocusChanged(hasFocus);
	// if (hasFocus) {
	// this.getWindow()
	// .getDecorView()
	// .setSystemUiVisibility(
	// View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	// | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	// | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	// | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	// | View.SYSTEM_UI_FLAG_FULLSCREEN
	// | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	// }
	// }

	public void postaviViewFlipper() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		brr = 0;
		for (int i = 0; i < Glavna.nizStranica.size(); i++) {
			View view = inflater.inflate(R.layout.flip_lay, viewFlipper, false);
			ImageView slika = (ImageView) view.findViewById(R.id.slikaStranice);
			bip = Glavna.nizStranica.get(i);
			slika.setImageBitmap(bip);
			viewFlipper.addView(view);
			slika.setOnTouchListener(new OnSwipeTouchListener() {
				public void onSwipeTop() {
				}

				public void onSwipeRight() {
					desno();
				}

				public void onSwipeLeft() {
					lijevo();
				}

				public void onSwipeBottom() {
				}
			});
		}
	}

	public void lijevo() {
		if (!isEditable) {
			brr++;
			crtanjeView.ocistiFunkcija();
			viewFlipper.setOutAnimation(this, R.anim.out_to_left);
			viewFlipper.setInAnimation(this, R.anim.in_from_right);
			viewFlipper.showNext();
		}
	}

	public void desno() {
		if (brr > 0)
			brr--;
		else
			brr = Glavna.nizStranica.size();
		if (!isEditable) {
			crtanjeView.ocistiFunkcija();
			viewFlipper.setOutAnimation(this, R.anim.out_to_right);
			viewFlipper.setInAnimation(this, R.anim.in_from_left);
			viewFlipper.showPrevious();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.page_scroller, menu);
		setTitle(Glavna.obradjujemo);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.previous:
			viewFlipper.setOutAnimation(this, R.anim.out_to_right);
			viewFlipper.setInAnimation(this, R.anim.in_from_left);
			viewFlipper.showPrevious();
			return true;
		case R.id.next:
			viewFlipper.setOutAnimation(this, R.anim.out_to_left);
			viewFlipper.setInAnimation(this, R.anim.in_from_right);
			viewFlipper.showNext();
			return true;
		case R.id.edit:
			if (isEditable == true) {
				isEditable = false;
				findViewById(R.id.next).setEnabled(true);
				findViewById(R.id.previous).setEnabled(true);
				crtanjeView.spreminamStranicu(brr + 1);
				postaviViewFlipper();
				return true;
			}
			if (!isEditable) {
				isEditable = true;
				findViewById(R.id.next).setEnabled(false);
				findViewById(R.id.previous).setEnabled(false);
				crtanjeView.bringToFront();
				crtanjeView.nacrtajnamCanvas(bip);
				return false;
			}
			return true;
		default:
			return false;

		}
	}

}
