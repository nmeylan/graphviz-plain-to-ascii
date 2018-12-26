package com.nmeylan.graphviztoascii;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AsciiRendererTest {

  @Test
  public void shouldRenderGraphInAscii() throws IOException {
    // Given
    InputStream graphInputStream = this.getClass().getClassLoader().getResourceAsStream("fixtures/good_graph.txt");
    AsciiRenderer asciiRenderer = new AsciiRenderer(graphInputStream);
    // When
    OutputStream graph = asciiRenderer.render();
    // Then
    System.out.println(graph);
  }

  @Test
  public void shouldRenderComplexGraphInAscii() throws IOException {
    // Given
    InputStream graphInputStream = this.getClass().getClassLoader().getResourceAsStream("fixtures/complex_graph.txt");
    AsciiRenderer asciiRenderer = new AsciiRenderer(graphInputStream, RankAxis.NONE, 10, 5);
    // When
    OutputStream graph = asciiRenderer.render();
    // Then
    System.out.println(graph);
  }
}
