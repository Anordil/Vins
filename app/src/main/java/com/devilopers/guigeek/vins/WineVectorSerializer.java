package com.devilopers.guigeek.vins;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import Vins.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;

public class WineVectorSerializer implements Serializable {
	
	private ProgressDialog mProgress;
	private int totalItems = 0;

	private static final long serialVersionUID = 12L;
	private Vector<Vin> data;
	private boolean exportError = false;

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

	public String doExport(String iFileName, boolean bIncludePics, TheWinesApp theWinesApp) throws IOException {

		File rootFolder = Environment.getExternalStorageDirectory();
		final File filename = new File(rootFolder, iFileName).getAbsoluteFile();
		final File filenameCSV = new File(rootFolder, iFileName + ".csv").getAbsoluteFile();
		final boolean includePics = bIncludePics;
		exportError = false;
		
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
					FileOutputStream fos =  new FileOutputStream(filename);
					PrintWriter writerCSV =  new PrintWriter(filenameCSV);
					ObjectOutputStream out = new ObjectOutputStream(fos);

					writerCSV.println("name, appellation, type, price, stock");

					for (Vin vin: data) {
						
						try {
						  
						  if (includePics) {
						    vin.loadImage();
						  }
						  else {
						    vin.set_imageBytes(null);
						  }

						  if (vin.getStock() > 0) {
							  writerCSV.println(vin.getNom() + ',' + vin.getAppellation() + ',' + vin.getColour() + ',' + vin.getPrice() + ',' + vin.getStock());
						  }
							out.writeObject(vin);
							out.flush();
						} 
						catch (IOException e) {
							Log.e("Serialize", "Error exporting " + vin.getNom() + " " + vin.getMillesime(), e);
							
							if (filename.getFreeSpace() == 0) {
								Log.e("Serialize", "No more free space !");
							}
							
							exportError = true;
							handle.sendMessage(handle.obtainMessage());
							break;
						}
						finally {
							vin.freeImage();
						}
						handle.sendMessage(handle.obtainMessage());
					}
					writerCSV.close();
					out.close();
				}
				catch (Exception e) {
					Log.e("Serialize", "Unhandled exception", e);
				}
			}
		});
		
		// Serialize all the wines
		totalItems = data.size();
		mProgress = new ProgressDialog(theWinesApp);
		mProgress.setMax(totalItems);
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgress.setCancelable(false);

		exportThread.start();
		mProgress.show();
		
		return filename.getAbsolutePath();
	}

	private void doProgress() {
		mProgress.incrementProgressBy(1);
		mProgress.setMessage(mProgress.getProgress() + "/" + mProgress.getMax());
		if (exportError) {
			mProgress.dismiss();
			  Toast.makeText(TheWinesApp.getContext(), TheWinesApp.getContext().getResources().getString(R.string.export_error), Toast.LENGTH_LONG).show();
		}
		else if (mProgress.getProgress() == mProgress.getMax()) {
			mProgress.dismiss();
			  Toast.makeText(TheWinesApp.getContext(), TheWinesApp.getContext().getResources().getString(R.string.export_success), Toast.LENGTH_LONG).show();
		}
		
	}

}
