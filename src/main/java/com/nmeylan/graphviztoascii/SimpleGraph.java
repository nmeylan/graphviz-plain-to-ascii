package com.nmeylan.graphviztoascii;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple graph representation, as labels, style and color are not handled for nodes and edges.
 */
public class SimpleGraph {
  protected List<SimpleNode> nodes;
  protected List<SimpleEdge> edges;
  protected Double scale;
  protected Double width;
  protected Double height;

  public SimpleGraph() {
    this.nodes = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.scale = 1.0;
    this.width = 0.0;
    this.height = 0.0;
  }

  public SimpleGraph(Double scale, Double width, Double height) {
    this.nodes = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.scale = scale;
    this.width = width;
    this.height = height;
  }

  public Double getScale() {
    return scale;
  }

  public Double getWidth() {
    return width;
  }

  public Double getHeight() {
    return height;
  }

  public List<SimpleNode> getNodes() {
    return nodes;
  }

  public List<SimpleEdge> getEdges() {
    return edges;
  }

  public SimpleNode getNode(String name) {
    return getNodes().stream()
      .filter(node -> node.getName().equals(name))
      .findFirst()
      .orElse(null);
  }

  public boolean addNode(SimpleNode node) {
    return this.getNodes().add(node);
  }

  public boolean addEdge(SimpleEdge edge) {
    return this.getEdges().add(edge);
  }
}
