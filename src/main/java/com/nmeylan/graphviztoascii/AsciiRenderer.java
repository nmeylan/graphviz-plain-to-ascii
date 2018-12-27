package com.nmeylan.graphviztoascii;

import org.omg.PortableInterceptor.DISCARDING;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AsciiRenderer {

  private final static int X_UNIT_CHARS = 6; // 1 unit = 6 chars (SPACE)
  private final static int Y_UNIT_CHARS = 3; // 1 unit = 3 chars (LF)
  private final static char SPACE = ' ';
  private final static char LF = '\n';
  private final static char[] OVERRIDABLE_CHARS = new char[]{SPACE, '-', '|', '↗', '↙', '↖', '↗'};
  private SimpleGraph graph;
  private List<SimpleNode> remainingNodes;
  private int nodeNameMaxLength;
  private int graphWidth;
  private int graphHeight;
  private char[][] graphRenderer;
  private final int xUnitScale;
  private final int yUnitScale;

  /**
   * @param extFormatGraph: Input stream of the graph in ext format
   */
  public AsciiRenderer(InputStream extFormatGraph) {
    this(extFormatGraph, null, null);
  }

  /**
   * @param extFormatGraph: Input stream of the graph in ext format
   * @param xUnitScale:     This value can be used to defined how to scale position units in X axis.
   *                        When node label are large, it is better to increase xUnitScale value.
   *                        Example: If nodes labels length is 10 chars long in average, then use 10 as xUnitScale,
   *                        gives a better result than 4.
   * @param yUnitScale:     This value can be used to defined how to scale position units in Y axis.
   */
  public AsciiRenderer(InputStream extFormatGraph, Integer xUnitScale, Integer yUnitScale) {
    PlainExtParser parser = new PlainExtParser();
    graph = parser.parse(extFormatGraph);
    remainingNodes = graph.getNodes().subList(0, graph.getNodes().size());
    nodeNameMaxLength = graph.getNodes().stream().max(Comparator.comparingInt(n -> n.getName().length())).get().getName().length();
    this.xUnitScale = xUnitScale == null ? getDefaultXUnitScale(nodeNameMaxLength) : xUnitScale;
    this.yUnitScale = yUnitScale == null ? Y_UNIT_CHARS : yUnitScale;
    graphWidth = (int) Math.ceil(graph.getWidth() * this.xUnitScale) + nodeNameMaxLength;
    graphHeight = (int) Math.ceil(graph.getHeight() * this.yUnitScale);
    graphRenderer = new char[graphHeight + 1][graphWidth + 1];
  }

  private int getDefaultXUnitScale(int nodeNameMaxLength) {
    if (nodeNameMaxLength > X_UNIT_CHARS) {
      return nodeNameMaxLength;
    } else {
      return X_UNIT_CHARS;
    }
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
      for (int i = 0; i < graphRenderer.length; i++) {
        int j = 0;
        for (j = 0; j < graphRenderer[0].length - 1; j++) {
          graphRenderer[i][j] = SPACE;
        }
        graphRenderer[i][j] = LF;
      }

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
        if (node != null) {
          remainingNodes.remove(node);
          int i;
          // recenter node name
          int newNodeCenter = 0;
          if (node.getName().length() > 6) {
            newNodeCenter = (int) Math.floor((node.getWidth() / 3) * xUnitScale);
          }
          for (i = 0; i < node.getName().length(); i++) {
            graphRenderer[currentY][currentX - newNodeCenter + i] = node.getName().charAt(i);
          }
          x += i - 1;
        }
      }
      graphRenderer[currentY][graphWidth] = LF;
    }
  }

  private void renderEdges() {
    for (SimpleEdge edge : graph.getEdges()) {
      List<ControlPoint> points = new LinkedList<>();
      points.add(new ControlPoint(edge.getTail().getX(), edge.getTail().getY()));
      points.addAll(edge.getControlPoints());
      points.add(new ControlPoint(edge.getHead().getX(), edge.getHead().getY()));
      List<EdgePoint> drawPoints = new LinkedList<>();
      for (int i = 0; i < points.size() - 1; i++) {
        ControlPoint from = points.get(i);
        ControlPoint to = points.get(i + 1);
        int currentX = (int) Math.ceil(from.getX(xUnitScale));
        int currentY = (int) Math.ceil(from.getY(yUnitScale));
        int targetX = (int) Math.ceil(to.getX(xUnitScale));
        int targetY = (int) Math.ceil(to.getY(yUnitScale));
        int diffX = currentX - targetX;
        int diffY = currentY - targetY;
        int changeX = 0;
        int changeY = 0;
        boolean isSameX = diffX == 0;
        boolean isSameY = diffY == 0;

        while (!isSameY || !isSameX) {
          if (!isSameY) {
            if (diffY < 0) {
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
            if (diffX < 0) {
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
          drawPoints.add(new EdgePoint(Direction.get(changeX, changeY), currentX, currentY));
        }

      }
      alterEdgePointDirection(drawPoints);
      for (EdgePoint point : drawPoints) {
        if (canCharBeOverride(graphRenderer[point.getY()][point.getX()])) {
          graphRenderer[point.getY()][point.getX()] = point.getDirection().getSymbol();
        }
      }

    }
  }

  /**
   * This method alter edge points direction
   *
   * @param drawPoints: list a point to draw to represent an edge.
   */
  private void alterEdgePointDirection(List<EdgePoint> drawPoints) {
    EdgePoint previousPoint = null;
    for (int i = 0; i < drawPoints.size() - 2; i++) {
      EdgePoint point = drawPoints.get(i);
      EdgePoint nextPoint = drawPoints.get(i + 1);
      if (previousPoint != null) {
        /*
          When there are successive →→ or ←←. replace them by -→ or ←-
         */
        if ((point.getDirection() == Direction.SOUTH && (previousPoint.getDirection() == Direction.SOUTH || previousPoint.getDirection() == Direction.VERTICAL))
          || (point.getDirection() == Direction.NORTH && (previousPoint.getDirection() == Direction.NORTH || previousPoint.getDirection() == Direction.VERTICAL))) {
          point.setDirection(Direction.VERTICAL);
        }
        /*
          When there are successive
          ↓ replace them by |       or ↑     replace them by ↑
          ↓                 ↓          ↑                     |
         */
        else if ((point.getDirection() == Direction.EAST && (previousPoint.getDirection() == Direction.EAST) || previousPoint.getDirection() == Direction.HORIZONTAL)
          || (point.getDirection() == Direction.WEST && (previousPoint.getDirection() == Direction.WEST || previousPoint.getDirection() == Direction.HORIZONTAL))) {
          point.setDirection(Direction.HORIZONTAL);
        }
        /*
          When there are
          -           replace them by ↘
           ↘                           ↘
         */
        if (point.getDirection() == Direction.HORIZONTAL) {
          if (nextPoint.getDirection() == Direction.SOUTH_WEST) {
            point.setDirection(Direction.SOUTH_WEST);
          } else if (nextPoint.getDirection() == Direction.SOUTH_EAST) {
            point.setDirection(Direction.SOUTH_EAST);
          } else if (nextPoint.getDirection() == Direction.NORTH_EAST) {
            point.setDirection(Direction.NORTH_EAST);
          } else if (nextPoint.getDirection() == Direction.NORTH_WEST) {
            point.setDirection(Direction.NORTH_WEST);
          }
        }
      }
      previousPoint = point;
    }
  }

  private boolean canCharBeOverride(char character) {
    for (char c : OVERRIDABLE_CHARS) {
      if (character == c) {
        return true;
      }
    }
    return false;
  }
}
