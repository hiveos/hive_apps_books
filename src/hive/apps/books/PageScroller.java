package hive.apps.books;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class PageScroller extends Activity implements OnClickListener{

	private Menu menu;
	ImageView Stranica;
	Bitmap izgledStranice;
	Button nextButton, previousButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_scroller);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Stranica = (ImageView) findViewById(R.id.imageView1);
		nextButton=(Button)findViewById(R.id.buttonNext);
		previousButton=(Button)findViewById(R.id.buttonPrevious);
		nextButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);
		PhotoViewAttacher mAttacher = new PhotoViewAttacher(Stranica);
		postaviStranicu();
	}

	void postaviStranicu() {
		Stranica.setImageBitmap(Glavna.bmpStranica);
	}

	public void lijevo() {
		Glavna.strNaKojojSeNalazimo++;
		Log.d("NALAZIMO SE NA: ", Glavna.strNaKojojSeNalazimo+"");
		if (Glavna.strNaKojojSeNalazimo <= Glavna.stranice.length) {
			Glavna.ucitajStranice();
			postaviStranicu();
		}
		else Glavna.strNaKojojSeNalazimo=Glavna.stranice.length;
	}

	public void desno() {
		Glavna.strNaKojojSeNalazimo--;
		Log.d("NALAZIMO SE NA: ", Glavna.strNaKojojSeNalazimo+"");
		if(Glavna.strNaKojojSeNalazimo>=1){
			Glavna.ucitajStranice();
			postaviStranicu();
		}
		else Glavna.strNaKojojSeNalazimo=1;
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
		case R.id.action_goTo:
			postaviDialog();
			return true;
		default:
			return false;

		}
	}

	private void postaviDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Jump to page:");
		// Create TextView
		final EditText input = new EditText (this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Glavna.strNaKojojSeNalazimo = Integer.parseInt(input.getText().toString());
			if(Glavna.strNaKojojSeNalazimo>1 && Glavna.strNaKojojSeNalazimo<=Glavna.stranice.length){
				Glavna.ucitajStranice();
				postaviStranicu();
			}
		  }
		});

		  alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
		switch(v.getId()){
		case R.id.buttonNext:
			lijevo();
			break;
		case R.id.buttonPrevious:
			desno();
			break;
		default: break;
		
		}
	}
}
