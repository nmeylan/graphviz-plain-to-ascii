package com.nmeylan.graphviztoascii;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

public class AsciiRenderer {

  private final static int X_UNIT_CHARS = 4; // 1 unit = 2 chars (SPACE)
  private final static int Y_UNIT_CHARS = 5; // 1 unit = 5 chars (LF)
  private final static char SPACE = ' ';
  private final static char LF = '\n';

  /**
   * Graphviz PLAIN_EXT format represent a graph in the following carthesian 2D plan:
   * <p>
   * y
   * ^ 1,0
   * |
   * │
   * │
   * └───────> x
   * 0,0      0,1
   *
   * @param extFormatGraph: A graph in a Graphviz PLAIN_EXT format.
   * @return output stream of the graph in ascii format.
   */
  public static OutputStream render(InputStream extFormatGraph) throws IOException {
    OutputStream graphOutput = new ByteArrayOutputStream();
    PlainExtParser parser = new PlainExtParser();
    SimpleGraph graph = parser.parse(extFormatGraph);
    StringBuffer buffer = new StringBuffer();
    List<SimpleNode> remainingNodes = graph.getNodes().subList(0, graph.getNodes().size());
    int nodeNameMaxLength = graph.getNodes().stream().max(Comparator.comparingInt(n -> n.getName().length())).get().getName().length();
    final int xUnitScale = X_UNIT_CHARS * nodeNameMaxLength;
    int graphWidth = (int) Math.ceil(graph.getWidth() * xUnitScale);
    int graphHeight = (int) Math.ceil(graph.getHeight() * Y_UNIT_CHARS);
    char[][] graphRenderer = new char[graphHeight + 1][graphWidth + 1];
    renderNodes(remainingNodes, xUnitScale, graphWidth, graphHeight, graphRenderer);
    renderEdges(graphRenderer, graph, xUnitScale);

    for (int y = graphRenderer.length - 1; y > 0; y--) {
      for (int x = 0; x < graphRenderer[0].length; x++) {
        graphOutput.write(graphRenderer[y][x]);
      }
    }

    return graphOutput;
  }

  /**
   *
   * @param remainingNodes: Remaining nodes to render
   * @param xUnitScale: unit scale for X axis
   * @param graphWidth: Graph width
   * @param graphHeight: Graph height
   * @param graphRenderer: current graph renderer
   */
  private static void renderNodes(List<SimpleNode> remainingNodes, int xUnitScale, int graphWidth, int graphHeight, char[][] graphRenderer) {
    for (int y = 0; y <= graphHeight; y++) {
      final int currentY = y;
      for (int x = 0; x <= graphWidth; x++) {
        final int currentX = x;
        SimpleNode node = remainingNodes.stream()
          .filter(n ->
            (n.getX(xUnitScale) > currentX - 1 && n.getX(xUnitScale) <= currentX)
              && (n.getY(Y_UNIT_CHARS) > currentY - 1 && n.getY(Y_UNIT_CHARS) <= currentY)
          )
          .findFirst() // We suppose that nodes can't have same coordinates.
          .orElse(null);
        if (node == null) {
          graphRenderer[currentY][currentX] = SPACE;
        } else {
          remainingNodes.remove(node);
          int i;
          for (i = 0; i < node.getName().length(); i++) {
            graphRenderer[currentY][currentX + i] = node.getName().charAt(i);
          }
          x += i - 1;
        }
      }
      graphRenderer[currentY][graphWidth] = LF;
    }
  }

  /**
   * Render edges between nodes.
   *
   * @param graphRenderer: current graph renderer
   * @param graph: current ext graph
   * @param xUnitScale: unit scale for X axis
   */
  public static void renderEdges(char[][] graphRenderer, SimpleGraph graph, int xUnitScale) {
    for (SimpleEdge edge : graph.getEdges()) {
      SimpleNode head = edge.getHead();
      SimpleNode tail = edge.getTail();
      int currentX = (int) Math.ceil(tail.getX(xUnitScale));
      int currentY = (int) Math.ceil(tail.getY(Y_UNIT_CHARS));
      int targetX = (int) Math.ceil(head.getX(xUnitScale));
      int targetY = (int) Math.ceil(head.getY(Y_UNIT_CHARS));
      int prevX = -1;
      int prevY = -1;
      int diffX = currentX - targetX;
      int diffY = currentY - targetY;
      boolean isSameX = diffX == 0;
      boolean isSameY = diffY == 0;

      while (!isSameY || !isSameX) {
        if (!isSameY) {
          if (diffY < 0) {
            currentY++;
          } else {
            currentY--;
          }
        }
        if (!isSameX) {
          if (diffX < 0) {
            currentX++;
          } else {
            currentX--;
          }
        }
        diffX = currentX - targetX;
        diffY = currentY - targetY;
        isSameX = diffX == 0;
        isSameY = diffY == 0;
        if (graphRenderer[currentY][currentX] == SPACE
          || graphRenderer[currentY][currentX] == '-'
          || graphRenderer[currentY][currentX] == '|'
          || graphRenderer[currentY][currentX] == '>') {
          prevY = currentY;
          prevX = currentX;
          char symbol = getSymbol(diffX, diffY);
          graphRenderer[currentY][currentX] = symbol;
        }
      }
      if (prevX > -1 && prevY > -1) {
        graphRenderer[prevY][prevX] = '>';
      }
    }
  }

  /**
   * Get symbol to print.
   *
   * @param diffX: diff on X axis between 2 points
   * @param diffY: diff on Y axis between 2 points
   * @return which symbol should be printed to render edges.
   */
  private static char getSymbol(int diffX, int diffY) {
    char symbol = '-';
    if (diffY > 0) {
      if (diffX > 0) {
        symbol = '/';
      } else if(diffX < 0) {
        symbol = '\\';
      } else {
        symbol = '|';
      }
    } else if (diffY < 0) {
      if (diffX > 0) {
        symbol = '\\';
      } else if(diffX < 0) {
        symbol = '/';
      } else {
        symbol = '|';
      }
    }
    return symbol;
  }
}
