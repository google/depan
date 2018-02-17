/*
 * Copyright 2008 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.eclipse.visualization.ogl;

import java.awt.geom.Point2D;

/**
 * Simple class representing a 2 dimensional vector, with handy methods for
 * 2D vector operations.
 *
 * @author Yohann Coppel
 *
 */
public class Vec2 {
  public float x, y;

  public Vec2(Point2D p) {
    x = (float) p.getX();
    y = (float) p.getY();
  }

  public Point2D toPoint() {
    return new Point2D.Float(x, y);
  }

  public Vec2(float ax, float ay) {
    x = ax;
    y = ay;
  }

  public float length() {
    return (float) Math.sqrt(x * x + y * y);
  }

  public float normalize() {
    float l = length();
    x /= l;
    y /= l;
    return l;
  }

  /**
   * Convert vector into radians.
   * Must be normalized first
   * ({@link #normalize()} or {@link #getNormalized()}).
   */
  public float radians() {
    double result = Math.acos(x);
    if (y < 0) {
      result = (2.0 * Math.PI) - result;
    }
    return (float) result;
  }

  public Vec2 getNormalized() {
    float l = length();
    return new Vec2(x / l, y / l);
  }

  public float squareLength() {
    return x * x + y * y;
  }

  public Vec2 mult(float a) {
    return new Vec2(x * a, y * a);
  }

  public Vec2 plus(Vec2 that) {
    return new Vec2(this.x + that.x, this.y + that.y);
  }

  public Vec2 minus() {
    return new Vec2(-x, -y);
  }

  public Vec2 minus(Vec2 that) {
    return new Vec2(x - that.x, y - that.y);
  }

  public Vec2 div(float f) {
    return new Vec2(x / f, y / f);
  }

  public float mult(Vec2 that) {
    return x * that.x + y * that.y;
  }

  @Override
  public String toString() {
    return "(" + x + ":" + y + ")";
  }

  @Override
  public boolean equals(Object other) {
    if ( !(other instanceof Vec2)) {
      return false;
    }
    Vec2 otherVec = (Vec2) other;
    if (this.x != otherVec.x) {
      return false;
    }
    if (this.y != otherVec.y) {
      return false;
    }
    return true;
  }
}
