package com.nmeylan.graphviztoascii;

import com.nmeylan.graphviztoascii.exceptions.GraphParseException;

import java.io.*;
import java.security.InvalidParameterException;

public class PlainExtParser {

  public SimpleGraph parse(InputStream extFormatGraph) {
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(extFormatGraph))) {
      SimpleGraph graph = new SimpleGraph();;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.equalsIgnoreCase("stop")) {
          break;
        } else if (line.startsWith("graph")) {
          String[] graphElements = line.split(" ");
          graph = new SimpleGraph(Double.parseDouble(graphElements[1]), Double.parseDouble(graphElements[2]), Double.parseDouble(graphElements[3]));
        } else if (line.startsWith("node")) {
          String[] nodeElements = line.split(" ");
          graph.addNode(new SimpleNode(nodeElements[1], Double.parseDouble(nodeElements[2]), Double.parseDouble(nodeElements[3])));
        } else if (line.startsWith("edge")) {
          String[] edgeElements = line.split(" ");
          graph.addEdge(new SimpleEdge(graph.getNode(edgeElements[1]), graph.getNode(edgeElements[2])));
        }
      }
      return graph;
    } catch (IOException | InvalidParameterException e) {
      throw new GraphParseException(e);
    }
  }
}
