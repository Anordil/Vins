package com.devilopers.guigeek.vins;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

      // The file exists, let's go to the import screen to decode it
      Intent intent = new Intent(FilePicker.this, ImportScreen.class);
      intent.putExtra(TheWinesApp.WRAPPER, file.getAbsolutePath());
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
