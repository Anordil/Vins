package com.devilopers.guigeek.vins;

import java.util.Vector;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class SectionScreen extends ListActivity implements OnClickListener {

  protected Button _buttonOk;
  protected Vector<Section> _sectionList;
  protected Section[] _array;
  protected Section _updatedItem = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.section);
    
    _buttonOk = (Button)findViewById(R.id.buttonOk);
    _buttonOk.setOnClickListener(this);
    
    // Init the list
    initList();
    
    ListView lv = getListView();
    lv.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // When clicked, open the compartment
        _updatedItem = _array[position];
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DATA_SECTION, _array[position]);
        Intent newIntent = new Intent(getApplicationContext(), CompartmentScreen.class);
        newIntent.putExtras(bundle);
        startActivityForResult(newIntent, 0);
      }
    });
    
    // To delete sections
    registerForContextMenu(getListView());
  }

  private void initList() {
    _sectionList = DatabaseAdapter.instance().getSections();
    _array = new Section[_sectionList.size()];
    int i = 0;
    for (Section s: _sectionList) {
      _array[i++] = s;
    }
    
    setListAdapter(new SectionAdapter(this, R.layout.list_section, _array));
  }


  @Override
  public void onClick(View v) {
    if (v == _buttonOk) {
      EditText label = (EditText)findViewById(R.id.newSectionInput);
      if (label.getText().toString() != null && label.getText().toString().length() > 0) {
        long result = DatabaseAdapter.instance().createSection(label.getText().toString());
        if (result >= 0) {
          label.setText("");
          initList();
        }
        else {
          Toast.makeText(getApplicationContext(), getResources().getString(result == -2 ? R.string.section_already_exists:R.string.unexpected_error ),Toast.LENGTH_SHORT).show();
        }
      }
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      // An id should be returned
      int compId = data.getExtras().getInt(Constants.DATA_COMP_ID);
      if (compId != 0) {
        Intent result = new Intent();
        result.putExtra(Constants.DATA_COMP_ID, compId);
        setResult(RESULT_OK, result);
        this.finish();
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error ),Toast.LENGTH_SHORT).show();
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
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.delete_menu, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    Section activeSection = _array[info.position];
    
    if (item.getItemId() == R.id.menu_delete) {
      if (DatabaseAdapter.instance().deleteSection(activeSection.getId())) {
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
