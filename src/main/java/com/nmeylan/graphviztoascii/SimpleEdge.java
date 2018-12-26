package com.nmeylan.graphviztoascii;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * A simple edge representation as it does not handle labels, color and shape.
 */
public class SimpleEdge {
  protected SimpleNode tail;
  protected SimpleNode head;
  protected List<ControlPoint> controlPoints;

  public SimpleEdge(SimpleNode tail, SimpleNode head, List<ControlPoint> controlPoints) {
    if (tail == null || head == null) {
      throw new InvalidParameterException();
    }
    this.tail = tail;
    this.head = head;
    this.controlPoints = controlPoints;
  }

  public SimpleNode getTail() {
    return tail;
  }

  public SimpleNode getHead() {
    return head;
  }

  public List<ControlPoint> getControlPoints() {
    return controlPoints;
  }

  public void setControlPoints(List<ControlPoint> controlPoints) {
    this.controlPoints = controlPoints;
  }
}
