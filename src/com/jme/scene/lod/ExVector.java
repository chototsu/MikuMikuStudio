package com.jme.scene.lod;

import java.util.Vector;

public class ExVector extends Vector {
  public ExVector() { }
  public ExVector(int i) {
    super(i);
  }
  public ExVector(int i, int j) {
    super(i,j);
  }
  public boolean add(Object obj) {
    if (indexOf(obj) >= 0) return false;

    return super.add(obj);
  }
  public Object get(Object obj) {
    int i = indexOf(obj);
    if (i < 0) return null;
    else return get(i);
  }
}
