package com.devilopers.guigeek.vins;

import java.io.Serializable;

public class Section implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 9175408893941893296L;
  protected int _id;
  protected String _name;
  
  public Section(String name, int id) {
    _id = id;
    _name = name;
  }
  
  
  public int getId() {
    return _id;
  }
  public void setId(int id) {
    this._id = id;
  }
  public String getName() {
    return _name;
  }
  public void setName(String name) {
    this._name = name;
  }

}
