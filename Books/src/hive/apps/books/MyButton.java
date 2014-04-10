package hive.apps.books;

import android.content.Context;
import android.widget.ImageView;

public class MyButton extends ImageView {
	public String imeKnjige;

	public MyButton(Context context, String ime) {
		super(context);
		imeKnjige = ime;

	}
}
