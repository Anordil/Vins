package com.devilopers.guigeek.vins;

import java.io.Serializable;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class CompartmentScreen extends SectionScreen {
  
  private Section _section;
  private Vector<Section> _compartments;
  private Section[] _arrayComp;
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    Bundle bundle = this.getIntent().getExtras();
    Serializable data = bundle.getSerializable(Constants.DATA_SECTION);
    if (data == null || !(data instanceof Section)) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error),Toast.LENGTH_SHORT).show();
      this.finish();
    }
    _section = (Section)data;
    
    // Display the active section's name
    TextView tv = (TextView)findViewById(R.id.sectionName);
    tv.setText(_section.getName());
    
    TextView label = (TextView)findViewById(R.id.labelSecOrComp);
    label.setText(getResources().getString(R.string.addCompartmentLabel));
    
    TextView selectLabel = (TextView)findViewById(R.id.tvSelect);
    selectLabel.setText(getResources().getString(R.string.select_compartment));
    
    // Init the list
    initList();
    
    ListView lv = getListView();
    lv.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Return the id of the selected compartment
        Section selectedCompartment = _arrayComp[position];
        returnResult(selectedCompartment.getId());
      }
    });
    
  }
  
  private void returnResult(int id) {
    Intent result = new Intent();
    result.putExtra(Constants.DATA_COMP_ID, id);
    setResult(RESULT_OK, result);
    this.finish();
  }
  
  private void initList() {
    _compartments = DatabaseAdapter.instance().getCompartments(_section.getId());
    _arrayComp = new Section[_compartments.size()];
    int i = 0;
    for (Section s: _compartments) {
      _arrayComp[i++] = s;
    }
    
    setListAdapter(new SectionAdapter(this, R.layout.list_section, _arrayComp));
  }


  @Override
  public void onClick(View v) {
    if (v == _buttonOk) {
      EditText label = (EditText)findViewById(R.id.newSectionInput);
      if (label.getText().toString() != null && label.getText().toString().length() > 0) {
        long result = DatabaseAdapter.instance().createCompartment(label.getText().toString(), _section.getId());
        if (result >= 0) {
          label.setText("");
          initList();
        }
        else {
          Toast.makeText(getApplicationContext(), getResources().getString(result == -2 ? R.string.compartment_already_exists:R.string.unexpected_error ),Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  @Override
  public void onBackPressed() {
    Intent data = new Intent();
    setResult(Constants.RETURN_CANCEL, data);
    super.onBackPressed();
  }
  
  // Contextual menu
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    Section activeCompartment = _arrayComp[info.position];
    
    if (item.getItemId() == R.id.menu_delete) {
      if (DatabaseAdapter.instance().deleteCompartment(activeCompartment.getId())) {
        initList();
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error ),Toast.LENGTH_SHORT).show();
      }
    }
    return true;
  }
  // Contextual menu ends
}
