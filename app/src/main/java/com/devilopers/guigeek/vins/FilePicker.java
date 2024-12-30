package com.devilopers.guigeek.vins;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import Vins.R;

import java.io.File;
import java.net.URI;

public class FilePicker extends Activity {

  final int PICK_FILE = 145;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Intent.ACTION_MAIN.equals(getIntent().getAction())) {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.setType("*/*");
      startActivityForResult(intent, PICK_FILE);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICK_FILE) {
      if (resultCode == Activity.RESULT_OK) {
        if (data != null) {
          Uri uri = data.getData();
          onFileSelect(uri);
        }
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

  protected void onFileSelect(Uri uri) {
    if (uri != null) {

      // The file exists, let's go to the import screen to decode it
      Intent intent = new Intent(FilePicker.this, ImportScreen.class);
      intent.putExtra(TheWinesApp.WRAPPER, uri);
      startActivity(intent);
      finish();
    } 
    else {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
      finish();
    }
  }
}
