package com.devilopers.guigeek.vins;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.guigeek.vins.R;

public class PreferencesManager extends Activity {
  
  SharedPreferences prefs;
  
  ToggleButton tPicture, tAging, tStock, tPrice, tVariety, tAssessment, tBestWith;
  
  TextView tvPic, tvAging, tvStock, tvPrice, tvVar, tvAss, tvBest;

  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.preferences);
    
    prefs = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
    
    tPicture = ( (ToggleButton)findViewById(R.id.toggle_picture) );
    tAging = ( (ToggleButton)findViewById(R.id.toggle_aging) );
    tStock = ( (ToggleButton)findViewById(R.id.toggle_stock) );
    tPrice = ( (ToggleButton)findViewById(R.id.toggle_price) );
    tVariety = ( (ToggleButton)findViewById(R.id.toggle_variety) );
    tAssessment = ( (ToggleButton)findViewById(R.id.toggle_assessment) );
    tBestWith = ( (ToggleButton)findViewById(R.id.toggle_bestwith) );
    
    tvPic = ( (TextView)findViewById(R.id.preftv_picture) );
    tvAging = ( (TextView)findViewById(R.id.preftv_aging) );
    tvStock = ( (TextView)findViewById(R.id.preftv_stock) );
    tvPrice = ( (TextView)findViewById(R.id.preftv_price) );
    tvVar = ( (TextView)findViewById(R.id.preftv_variety) );
    tvAss = ( (TextView)findViewById(R.id.preftv_assessment) );
    tvBest = ( (TextView)findViewById(R.id.preftv_bestwith) );
    createClickListener();
    
    tPicture.setChecked(prefs.getBoolean(Constants.PREF_PICTURE, true));
    tAging.setChecked(prefs.getBoolean(Constants.PREF_AGING, true));
    tStock.setChecked(prefs.getBoolean(Constants.PREF_STOCK, true));
    tPrice.setChecked(prefs.getBoolean(Constants.PREF_POS_AND_PRICE, true));
    tVariety.setChecked(prefs.getBoolean(Constants.PREF_VARIETY, true));
    tAssessment.setChecked(prefs.getBoolean(Constants.PREF_ASSESSMENTS, true));
    tBestWith.setChecked(prefs.getBoolean(Constants.PREF_BEST_WITH, true));
    
    ToggleListener listener = new ToggleListener();
    tPicture.setOnCheckedChangeListener(listener);
    tAging.setOnCheckedChangeListener(listener);
    tStock.setOnCheckedChangeListener(listener);
    tPrice.setOnCheckedChangeListener(listener);
    tVariety.setOnCheckedChangeListener(listener);
    tAssessment.setOnCheckedChangeListener(listener);
    tBestWith.setOnCheckedChangeListener(listener);
  }
  
  
  private class ToggleListener implements OnCheckedChangeListener {

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

      
      Editor editor = prefs.edit();
      if (editor == null) return;
      
      if (buttonView == tPicture) {
        editor.putBoolean(Constants.PREF_PICTURE, isChecked);
      }
      else if (buttonView == tAging) {
        editor.putBoolean(Constants.PREF_AGING, isChecked);
      }
      else if (buttonView == tStock) {
        editor.putBoolean(Constants.PREF_STOCK, isChecked);
      }
      else if (buttonView == tPrice) {
        editor.putBoolean(Constants.PREF_POS_AND_PRICE, isChecked);
      }
      else if (buttonView == tVariety) {
        editor.putBoolean(Constants.PREF_VARIETY, isChecked);
      }
      else if (buttonView == tAssessment) {
        editor.putBoolean(Constants.PREF_ASSESSMENTS, isChecked);
      }
      else if (buttonView == tBestWith) {
        editor.putBoolean(Constants.PREF_BEST_WITH, isChecked);
      }
      
      editor.commit();
    }

  }
  
  private void createClickListener() {
    tvPic.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tPicture.setChecked(!tPicture.isChecked());
      }
    });
    tvAging.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tAging.setChecked(!tAging.isChecked());
      }
    });
    tvStock.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tStock.setChecked(!tStock.isChecked());
      }
    });
    tvPrice.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tPrice.setChecked(!tPrice.isChecked());
      }
    });
    tvVar.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tVariety.setChecked(!tVariety.isChecked());
      }
    });
    tvAss.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tAssessment.setChecked(!tAssessment.isChecked());
      }
    });
    tvBest.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        tBestWith.setChecked(!tBestWith.isChecked());
      }
    });  
  }
}
