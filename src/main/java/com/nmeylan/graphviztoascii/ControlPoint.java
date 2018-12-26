package com.nmeylan.graphviztoascii;

public class ControlPoint {
  private final double x;
  private final double y;

  public ControlPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getX(double scale) {
    return scale * getX();
  }

  public double getY(double scale) {
    return scale * getY();
  }
}
