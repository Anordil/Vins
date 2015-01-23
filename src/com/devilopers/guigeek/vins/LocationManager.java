package com.devilopers.guigeek.vins;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.guigeek.vins.R;

public class LocationManager {

  private static Activity mParent;


  public LocationManager(Activity parent) {
    mParent = parent;
  }

  public File startCamera(File ioOldFile) {

    // Check there is a camera
    if (!mParent.getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
      Toast.makeText(mParent, mParent.getResources().getString(R.string.no_camera).toString(), Toast.LENGTH_LONG).show();
      return null;
    }

    // Check there is a camera app
    if (!isIntentAvailable(mParent.getBaseContext(), MediaStore.ACTION_IMAGE_CAPTURE)) {
      Toast.makeText(mParent, mParent.getResources().getString(R.string.no_camera_app).toString(), Toast.LENGTH_LONG).show();
      return null;
    }
    
    // If an image already existed for this bottle, we'll override it
    File f;
    if (ioOldFile != null && ioOldFile.exists()) {
      f = ioOldFile;
    }
    else {
      f = createImageFile();
    }

    // Start the camera app, let the parent handle the result and call us back
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
    mParent.startActivityForResult(takePictureIntent, Constants.REQUEST_PICTURE);
    return f;
  }

  private File createImageFile() {
    // Create the image folder if it does not exist
    File imagesFolder = new File(Environment.getExternalStorageDirectory(), mParent.getResources().getString(R.string.picture_folder));
    if (!imagesFolder.exists()) {
      imagesFolder.mkdirs();
    }

    // Create the next image file
    int imageNum = 0;
    String fileName = "image_" + String.valueOf(imageNum) + ".jpg";
    File output = new File(imagesFolder, fileName);
    while (output.exists()) {
      imageNum++;
      fileName = "image_" + String.valueOf(imageNum) + ".jpg";
      output = new File(imagesFolder, fileName);
    }
    return output;
  }
  
  public File browseToFile(File ioOldFile) {
    // If an image already existed for this bottle, we'll override it
    File f;
    if (ioOldFile != null && ioOldFile.exists()) {
      f = ioOldFile;
    }
    else {
      f = createImageFile();
    }

    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    photoPickerIntent.setType("image/*");
    mParent.startActivityForResult(photoPickerIntent, Constants.REQUEST_BROWSE_PHOTO);   
    return f;
  }


  protected boolean isIntentAvailable(Context context, String action) {
    final PackageManager packageManager = context.getPackageManager();
    final Intent intent = new Intent(action);
    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
  }


}
