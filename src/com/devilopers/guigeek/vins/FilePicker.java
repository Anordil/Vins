package com.devilopers.guigeek.vins;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.guigeek.vins.R;
import com.ipaulpro.afilechooser.FileChooserActivity;

public class FilePicker extends FileChooserActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Intent.ACTION_MAIN.equals(getIntent().getAction())) {
      showFileChooser();
    }
  }


  @Override
  protected void onFileSelect(File file) {
    if (file != null) {
      // Get the path of the Selected File.
      final String path = file.getAbsolutePath();
      WineVectorSerializer wrapper = null;
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try {
        fis = new FileInputStream(path);
        in = new ObjectInputStream(fis);
        wrapper = new WineVectorSerializer(false);
        
        Vin aWine = (Vin) in.readObject();
        while (aWine != null) {
        	wrapper.getData().add(aWine);
        	aWine =  (Vin) in.readObject();
        }
        in.close();
      } 
      catch (EOFException ex) {
    	  
      }
      catch (Exception ex) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
        this.finish();
      }

      // We successfully loaded the file, let's launch the Import screen
      Intent intent = new Intent(FilePicker.this, ImportScreen.class);
      intent.putExtra(TheWinesApp.WRAPPER, wrapper);
      startActivity(intent);
      finish();
    } 
    else {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
      finish();
    }
  }


  @Override
  protected void onFileError(Exception e) {
    Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
    finish();
  }

  @Override
  protected void onFileSelectCancel() {
    finish();
  }

  @Override
  protected void onFileDisconnect() {
    Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
    finish();
  }

}
