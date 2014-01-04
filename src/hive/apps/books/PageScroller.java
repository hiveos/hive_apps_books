package hive.apps.books;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class PageScroller extends Activity {

	private Menu menu;

	ViewFlipper viewFlipper;
	public Boolean isEditable;
	CrtanjeView crtanjeView;
	Bitmap bip;
	int brr = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_scroller);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isEditable = false;
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		crtanjeView = (CrtanjeView) findViewById(R.id.crtanje);
		postaviViewFlipper();
	}

	int sirina;
	int visina;

	public void postaviViewFlipper() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		brr = 0;
		for (int i = 0; i < Glavna.nizStranica.size(); i++) {
			View view = inflater.inflate(R.layout.flip_lay, viewFlipper, false);
			ImageView slika = (ImageView) view.findViewById(R.id.slikaStranice);
			bip = Glavna.nizStranica.get(i);
			slika.setImageBitmap(bip);
			viewFlipper.addView(view);
			PhotoViewAttacher mAttacher = new PhotoViewAttacher(slika);
			/*
			 * slika.setOnTouchListener(new OnSwipeTouchListener() { public void
			 * onSwipeTop() { } public void onSwipeRight() { desno(); } public
			 * void onSwipeLeft() { lijevo(); } public void onSwipeBottom() { }
			 * });
			 */
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
		this.menu = menu;
		getMenuInflater().inflate(R.menu.page_scroller, menu);
		setTitle(Glavna.obradjujemo);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		MenuItem fullscreenItem = menu.findItem(R.id.action_fullscreen);
		
		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

		switch (item.getItemId()) {
		case R.id.action_previous:
			viewFlipper.setOutAnimation(this, R.anim.out_to_right);
			viewFlipper.setInAnimation(this, R.anim.in_from_left);
			viewFlipper.showPrevious();
			return true;
		case R.id.action_next:
			viewFlipper.setOutAnimation(this, R.anim.out_to_left);
			viewFlipper.setInAnimation(this, R.anim.in_from_right);
			viewFlipper.showNext();
			return true;
		case R.id.action_edit:
			if (isEditable == true) {
				isEditable = false;
				findViewById(R.id.action_next).setEnabled(true);
				findViewById(R.id.action_previous).setEnabled(true);
				crtanjeView.spreminamStranicu(brr + 1);
				postaviViewFlipper();
				return true;
			}
			if (!isEditable) {
				isEditable = true;
				findViewById(R.id.action_next).setEnabled(false);
				findViewById(R.id.action_previous).setEnabled(false);
				crtanjeView.bringToFront();
				crtanjeView.nacrtajnamCanvas(bip);
				return false;
			}
		case R.id.action_fullscreen:
			if (!isImmersiveModeEnabled) {
				fullscreenItem.setIcon(R.drawable.ic_navigation_expand);
				fullscreenItem.setTitle("Show System Bars");
				toggleImmersive();
			} else if (isImmersiveModeEnabled) {
				fullscreenItem.setIcon(R.drawable.ic_navigation_collapse);
				fullscreenItem.setTitle("Hide System Bars");
				toggleImmersive();
			}

			return true;
		default:
			return false;

		}
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
}
