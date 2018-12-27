package com.nmeylan.graphviztoascii;

public enum Direction {
  NORTH('↑'),
  SOUTH('↓'),
  EAST('→'),
  WEST('←'),
  NORTH_WEST('↖'),
  NORTH_EAST('↗'),
  SOUTH_WEST('↙'),
  SOUTH_EAST('↘'),
  HORIZONTAL('-'),
  VERTICAL('|'),
  NULL(' ');

  private char symbol;

  Direction(char symbol) {
    this.symbol = symbol;
  }

  public static Direction get(int changeX, int changeY) {
    if (changeX > 0) {
      if (changeY > 0) {
        return Direction.NORTH_EAST;
      } else if (changeY < 0) {
        return Direction.SOUTH_EAST;
      } else {
        return Direction.EAST;
      }
    } else if (changeX < 0) {
      if (changeY > 0) {
        return Direction.NORTH_WEST;
      } else if (changeY < 0) {
        return Direction.SOUTH_WEST;
      } else {
        return Direction.WEST;
      }
    } else {
      if (changeY > 0) {
        return Direction.NORTH;
      } else if (changeY < 0) {
        return Direction.SOUTH;
      }
    }
    return Direction.HORIZONTAL;
  }

  public char getSymbol() {
    return symbol;
  }
}
