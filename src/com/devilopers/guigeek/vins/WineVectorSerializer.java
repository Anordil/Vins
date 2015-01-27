package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.guigeek.vins.R;

public class WineVectorSerializer implements Serializable {
	
	private ProgressDialog mProgress;
	private int totalItems = 0;

	private static final long serialVersionUID = 12L;
	private Vector<Vin> data;

	public WineVectorSerializer(Vector<Vin> inputData) {
		data = new Vector<Vin>();
		for (Vin vin : inputData) {
			data.add(vin);
		}
	}


	public WineVectorSerializer(boolean fillWithWholeDB) {
		data = new Vector<Vin>();
		if (fillWithWholeDB) {
			fillWithWholeDB();
		}
	}


	public Vector<Vin> getData() {
		return data;
	}


	public void fillWithWholeDB() {
		data.clear();
		Vector<Vin> allDB = DatabaseAdapter.instance().getAll();
		for (Vin vin : allDB) {
			data.add(vin);
		}
	}

	public String doExport(String iFileName, TheWinesApp theWinesApp) throws IOException {

		File rootFolder = Environment.getExternalStorageDirectory();
		final File filename = new File(rootFolder, iFileName);
		
		// Handler
		final Handler handle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				doProgress();
			}
		};
		
		// Export logic in a separate thread
		Thread exportThread = new Thread(new Runnable() {
			public void run() {

				try {
					FileOutputStream fos = null;
					fos = new FileOutputStream(filename);
					final ObjectOutputStream out = new ObjectOutputStream(fos);

					for (Vin vin: data) {
						handle.sendMessage(handle.obtainMessage());
						try {
							out.writeObject(vin);
						} 
						catch (IOException e) {
							Log.e("Serialize", "Error exporting a wine");
						}
					}
					out.close();
				}
				catch (Exception e) {
				}
			}
		});
		
		// Serialize all the wines
		totalItems = data.size();
		mProgress = new ProgressDialog(theWinesApp);
		mProgress.setMax(totalItems);
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		exportThread.start();
		mProgress.show();
		
		return filename.getAbsolutePath();
	}

	private void doProgress() {
		mProgress.incrementProgressBy(1);
		mProgress.setMessage(mProgress.getProgress() + "/" + mProgress.getMax());

		if (mProgress.getProgress() == mProgress.getMax()) {
			mProgress.dismiss();
		}
	}

}
