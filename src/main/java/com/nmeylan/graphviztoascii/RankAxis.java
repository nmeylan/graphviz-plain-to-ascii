package com.nmeylan.graphviztoascii;

/**
 * This enum is used to determine on which axis, edges should be "drawn first"
 * Example:
 * NONE:
 *     a            a
 *            ->     \
 *         b          -- b
 *
 * Y:
 *     a            a
 *            ->    |
 *         b        ---- b
 *
 * X:
 *     a            a----
 *            ->         |
 *         b             b
 */
public enum RankAxis {
  NONE,
  Y,
  X
}
