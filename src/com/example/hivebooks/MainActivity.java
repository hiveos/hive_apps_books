package com.example.hivebooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private List<String> items = null;
	private File whereToCopy = new File(
			Environment.getExternalStorageDirectory() + "/HIVE/Books/");
	public Glavna glavnaObjekt;

	public void kopirajFajl(File sourceLocation, File targetLocation)
			throws IOException {
		InputStream in = new FileInputStream(sourceLocation);
		OutputStream out = new FileOutputStream(targetLocation);

		// Copy the bits from instream to outstream
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);
		if (!whereToCopy.exists())
			whereToCopy.mkdirs();
		getFiles(new File(Environment.getExternalStorageDirectory() + "/")
				.listFiles());

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;

		if (selectedRow == 0) {
			getFiles(new File(Environment.getExternalStorageDirectory() + "/")
					.listFiles());
		}

		else {
			File file = new File(items.get(selectedRow));
			String imeFajla = file.getName().toString();
			String ekstenzijaFajla = imeFajla.substring(
					(imeFajla.lastIndexOf(".") + 1), imeFajla.length());

			if (file.isDirectory()) {
				getFiles(file.listFiles());
			} else if (file.isFile() && ekstenzijaFajla.equals("pdf")) {
				File bookRoot = new File(file.getPath());
				File targetLocation = new File(Glavna.sdCard + "/HIVE/Books/"
						+ imeFajla);

				if (!bookRoot.toString().equals(targetLocation.toString())) {

					if (!targetLocation.exists()) {
						try {
							targetLocation.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					try {
						kopirajFajl(bookRoot, targetLocation);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(targetLocation),
							"application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
				}

				if (bookRoot.toString().equals(targetLocation.toString())) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(targetLocation),
							"application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
				}

			} else {
				new AlertDialog.Builder(this)
						.setTitle(ekstenzijaFajla + " files are not supported ")
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int button) {
										// do nothing
									}
								}).show();
			}

		}
	}

	private void getFiles(File[] files) {
		items = new ArrayList<String>();
		items.add("Go back");
		for (File file : files) {
			items.add(file.getPath());
		}
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.file_list_row, items);
		setListAdapter(fileList);
	}
}