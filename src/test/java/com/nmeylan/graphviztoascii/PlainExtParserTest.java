package com.nmeylan.graphviztoascii;


import com.nmeylan.graphviztoascii.exceptions.GraphParseException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlainExtParserTest {

  @Test
  public void shouldParsePlainExtGraph() {
    // Given
    InputStream graphInputStream = this.getClass().getClassLoader().getResourceAsStream("fixtures/good_graph.txt");
    // When
    PlainExtParser plainExtParser = new PlainExtParser();
    SimpleGraph graph = plainExtParser.parse(graphInputStream);
    // Then
    assertThat(graph.getScale()).isEqualTo(1.0);
    assertThat(graph.getWidth()).isEqualTo(3.25);
    assertThat(graph.getHeight()).isEqualTo(1.7778);
    assertThat(graph.getEdges()).hasSize(4);
    assertThat(graph.getNodes()).hasSize(4);
  }

  @Test
  public void shouldRaiseAnExceptionWhenParsingAMalfordmedGraph() {
    // Given
    InputStream graphInputStream = this.getClass().getClassLoader().getResourceAsStream("fixtures/malformed_graph.txt");
    // When
    PlainExtParser plainExtParser = new PlainExtParser();
    assertThrows(GraphParseException.class, () -> plainExtParser.parse(graphInputStream));
  }
}
