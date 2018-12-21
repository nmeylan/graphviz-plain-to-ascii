package com.nmeylan.graphviztoascii;

public class SimpleNode {
  protected String name;
  protected Double x;
  protected Double y;

  public SimpleNode(String name, Double x, Double y) {
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

  public Double getX() {
    return x;
  }
  public Double getX(double scale) {
    return scale * getX();
  }

  public void setX(Double x) {
    this.x = x;
  }

  public Double getY() {
    return y;
  }

  public Double getY(double scale) {
    return scale * getY();
  }

  public void setY(Double y) {
    this.y = y;
  }
}
