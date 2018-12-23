package com.nmeylan.graphviztoascii;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

public class AsciiRenderer {

  private final static int X_UNIT_CHARS = 2; // 1 unit = 2 chars (SPACE)
  private final static int Y_UNIT_CHARS = 3; // 1 unit = 5 chars (LF)
  private final static char SPACE = ' ';
  private final static char LF = '\n';
  private final static char[] OVERRIDABLE_CHARS = new char[]{SPACE, '-', '|', '↗', '↙', '↖','↗'};
  private RankAxis rankAxis;
  private SimpleGraph graph;
  private List<SimpleNode> remainingNodes;
  private int nodeNameMaxLength;
  private int graphWidth;
  private int graphHeight;
  private char[][] graphRenderer;
  private final int xUnitScale;
  private final int yUnitScale;

  public AsciiRenderer(InputStream extFormatGraph) {
    this(extFormatGraph, RankAxis.NONE);
  }

  public AsciiRenderer(InputStream extFormatGraph, RankAxis rankAxis) {
    this(extFormatGraph, rankAxis, X_UNIT_CHARS, Y_UNIT_CHARS);
  }

  public AsciiRenderer(InputStream extFormatGraph, RankAxis rankAxis, int xUnitScale, int yUnitScale) {
    this.rankAxis = rankAxis;
    PlainExtParser parser = new PlainExtParser();
    graph = parser.parse(extFormatGraph);
    remainingNodes = graph.getNodes().subList(0, graph.getNodes().size());
    nodeNameMaxLength = graph.getNodes().stream().max(Comparator.comparingInt(n -> n.getName().length())).get().getName().length();
    this.xUnitScale = xUnitScale;
    this.yUnitScale = yUnitScale;
    graphWidth = (int) Math.ceil(graph.getWidth() * xUnitScale) + nodeNameMaxLength;
    graphHeight = (int) Math.ceil(graph.getHeight() * yUnitScale);
    graphRenderer = new char[graphHeight + 1][graphWidth + 1];
  }

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
   * @return output stream of the graph in ascii format. Null is return in case of error.
   */
  public OutputStream render() {
    try (OutputStream graphOutput = new ByteArrayOutputStream();
         Writer writer = new OutputStreamWriter(graphOutput, Charset.forName("UTF-8"))) {

      renderNodes();
      renderEdges();

      for (int y = graphRenderer.length - 1; y > 0; y--) {
        for (int x = 0; x < graphRenderer[0].length; x++) {
          writer.write(graphRenderer[y][x]);
        }
      }
      return graphOutput;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void renderNodes() {
    for (int y = 0; y <= graphHeight; y++) {
      final int currentY = y;
      for (int x = 0; x <= graphWidth; x++) {
        final int currentX = x;
        SimpleNode node = remainingNodes.stream()
          .filter(n ->
            (n.getX(xUnitScale) > currentX - 1 && n.getX(xUnitScale) <= currentX)
              && (n.getY(yUnitScale) > currentY - 1 && n.getY(yUnitScale) <= currentY)
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

  public void renderEdges() {
    for (SimpleEdge edge : graph.getEdges()) {
      SimpleNode head = edge.getHead();
      SimpleNode tail = edge.getTail();
      int currentX = (int) Math.ceil(tail.getX(xUnitScale));
      int currentY = (int) Math.ceil(tail.getY(yUnitScale));
      int targetX = (int) Math.ceil(head.getX(xUnitScale));
      int targetY = (int) Math.ceil(head.getY(yUnitScale));
      int prevX = -1;
      int prevY = -1;
      int diffX = currentX - targetX;
      int diffY = currentY - targetY;
      int changeX = 0;
      int changeY = 0;
      boolean isSameX = diffX == 0;
      boolean isSameY = diffY == 0;

      while (!isSameY || !isSameX) {
        if (!isSameY) {
          if (RankAxis.X == rankAxis && diffX > 3) {

          } else if (diffY < 0) {
            currentY++;
            changeY = 1;
          } else {
            currentY--;
            changeY = -1;
          }
        } else {
          changeY = 0;
        }
        if (!isSameX) {
          if (RankAxis.Y == rankAxis && diffY > 3) {

          } else if (diffX < 0) {
            currentX++;
            changeX = 1;
          } else {
            currentX--;
            changeX = -1;
          }
        } else {
          changeX = 0;
        }
        diffX = currentX - targetX;
        diffY = currentY - targetY;
        isSameX = diffX == 0;
        isSameY = diffY == 0;
        if (canCharBeOverride(graphRenderer[currentY][currentX])) {
          prevY = currentY;
          prevX = currentX;
          char symbol = getSymbol(diffX, diffY, changeX, changeY);
          graphRenderer[currentY][currentX] = symbol;
        }
      }
      if (prevX > -1 && prevY > -1) {
        char symbol = '>';
        if (changeX > 0 && changeY > 0) {
          symbol = '↗';
        } else if (changeX > 0 && changeY < 0) {
          symbol = '↘';
        } else if (changeX < 0 && changeY > 0) {
          symbol = '↖';
        } else if (changeX < 0 && changeY < 0) {
          symbol = '↙';
        } else if (changeY == 0 && (changeX < 0 || changeX > 0)) {
          if (prevY < currentY) {
            symbol = '↑';
          } else if (prevY > currentY) {
            symbol = '↓';
          } else {
            symbol = changeX < 0 ? '←' : '→';
          }
        } else if (changeX == 0 && changeY > 0) {
          symbol = '↑';
        } else if (changeX == 0 && changeY < 0) {
          symbol = '↓';
        }
        graphRenderer[prevY][prevX] = symbol;
      }
    }
  }

  private boolean canCharBeOverride(char character) {
    for(char c : OVERRIDABLE_CHARS) {
      if (character == c) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get symbol to print.
   *
   * @param changeX: diff on X axis between 2 points
   * @param changeY: diff on Y axis between 2 points
   * @return which symbol should be printed to render edges.
   */
  private char getSymbol(int diffX, int diffY, int changeX, int changeY) {
    char symbol = '-';
    if (RankAxis.Y == rankAxis && diffY > 3) {
      symbol = '|';
    } else if (changeY > 0) {
      if (RankAxis.X == rankAxis && diffX > 3) {

      } else if (changeX > 0) {
        symbol = '↗';
      } else if (changeX < 0) {
        symbol = '↖';
      } else {
        symbol = '|';
      }
    } else if (changeY < 0) {
      if (RankAxis.X == rankAxis && diffX > 3) {

      } if (changeX > 0) {
        symbol = '↘';
      } else if (changeX < 0) {
        symbol = '↙';
      } else {
        symbol = '|';
      }
    }
    return symbol;
  }
}
