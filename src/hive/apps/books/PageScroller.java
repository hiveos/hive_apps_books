package hive.apps.books;


import java.util.ArrayList;
import java.util.List;


import hive.apps.books.R.id;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_scroller);
		
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for (int i = 0; i < Glavna.nizStranica.size(); i++) {
			View view = inflater.inflate(R.layout.flip_lay, viewFlipper, false);
			ImageView slika = (ImageView)view.findViewById(R.id.slikaStranice);
			Bitmap bip = Glavna.nizStranica.get(i);
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
	
	public void lijevo(){
		viewFlipper.setOutAnimation(this, R.anim.out_to_left);
		viewFlipper.setInAnimation(this, R.anim.in_from_right);
		viewFlipper.showNext();
	}
	
	public void desno(){
		viewFlipper.setOutAnimation(this, R.anim.out_to_right);
		viewFlipper.setInAnimation(this, R.anim.in_from_left);
		viewFlipper.showPrevious();
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
		switch(item.getItemId()){
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
		default: return false;
		
		}
	}
	
	

}
