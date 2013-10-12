package hive.apps.books;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MyButton extends ImageView {
	public String imeKnjige;
	
	public MyButton(Context context, String ime) {
		super(context);
		// TODO Auto-generated constructor stub
		imeKnjige=ime;
		
	}	
}
