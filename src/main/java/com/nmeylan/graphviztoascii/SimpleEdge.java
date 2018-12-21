package com.nmeylan.graphviztoascii;

import java.security.InvalidParameterException;

public class SimpleEdge {
  protected SimpleNode tail;
  protected SimpleNode head;

  public SimpleEdge(SimpleNode tail, SimpleNode head) {
    if (tail == null || head == null) {
      throw new InvalidParameterException();
    }
    this.tail = tail;
    this.head = head;
  }

  public SimpleNode getTail() {
    return tail;
  }

  public void setTail(SimpleNode tail) {
    this.tail = tail;
  }

  public SimpleNode getHead() {
    return head;
  }

  public void setHead(SimpleNode head) {
    this.head = head;
  }
}
