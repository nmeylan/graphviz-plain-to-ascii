package com.nmeylan.graphviztoascii;

public class EdgePoint {
  private Direction direction;
  private final int x;
  private final int y;

  public EdgePoint(Direction direction, int x, int y) {
    this.direction = direction;
    this.x = x;
    this.y = y;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
