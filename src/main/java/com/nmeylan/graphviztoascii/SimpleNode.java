package com.nmeylan.graphviztoascii;

public class SimpleNode {
  protected final String name;
  protected final double x;
  protected final double y;
  protected final double width;
  protected final double height;

  public SimpleNode(String name, double x, double y, double width, double height) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public String getName() {
    return name;
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

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }
}
