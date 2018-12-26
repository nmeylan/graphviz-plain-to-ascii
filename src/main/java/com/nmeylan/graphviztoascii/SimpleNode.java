package com.nmeylan.graphviztoascii;

public class SimpleNode {
  protected String name;
  protected final double x;
  protected final double y;

  public SimpleNode(String name, double x, double y) {
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getX() {
    return x;
  }
  public double getX(double scale) {
    return scale * getX();
  }

  public double getY() {
    return y;
  }

  public double getY(double scale) {
    return scale * getY();
  }

}
