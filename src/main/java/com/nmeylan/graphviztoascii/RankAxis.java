package com.nmeylan.graphviztoascii;

/**
 * This enum is used to determine on which axis, edges should "go first"
 * Example:
 * NONE:
 *     x            x
 *            ->     \
 *         y          -- y
 *
 * Y:
 *     x            x
 *            ->    |
 *         y        ---- y
 *
 * X:
 *     x            x----
 *            ->         |
 *         y             y
 */
public enum RankAxis {
  NONE,
  Y,
  X
}
