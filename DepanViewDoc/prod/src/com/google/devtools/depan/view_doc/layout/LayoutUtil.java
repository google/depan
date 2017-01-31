package com.google.devtools.depan.view_doc.layout;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.model.Point2dUtils;

import com.google.common.collect.Maps;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class LayoutUtil {

  private LayoutUtil() {
    // Prevent instantiation.
  }

  /** How much room to consume for full viewport layout scaling. */
  public static final double FULLSCALE_MARGIN = 0.9;

  /**
   * Defines the OpenGL distance between two points that should be considered
   * equivalent to zero.
   * 
   * <p>Ideally, this should be obtained from the GLPanel/View, perhaps as the
   * half the OpenGL distance between two pixels.  But that will have to wait.
   */
  private static final double ZERO_THRESHOLD = 0.1;

  public static Map<GraphNode, Point2D> calcPositions(
      LayoutGenerator layout,
      LayoutContext context,
      Collection<GraphNode> layoutNodes) {
    LayoutRunner runner = layout.buildRunner(context);
    runLayout(runner);
    return runner.getPositions(layoutNodes);
  }

  private static void runLayout(LayoutRunner runner) {
    if (runner.layoutDone())
      return;

    while (!runner.layoutDone()) {
      runner.layoutStep();
    }
  }


  /**
   * Scale the nodes so that they would fit in the viewport, but don't force
   * them into the viewport.
   * 
   * @param layoutNodes
   * @param locations
   * @param viewport
   * @return updated node locations that are the same size as the viewport
   */
  public static Map<GraphNode, Point2D> computeFullViewScale(
          Collection<GraphNode> layoutNodes,
          Map<GraphNode, Point2D> locations,
          Rectangle2D viewport) {

    if (layoutNodes.size() <= 0) {
      return Collections.emptyMap();
    }

    // If there is only one node, don't change its location
    Map<GraphNode, Point2D> result = Maps.newHashMap();
    if (layoutNodes.size() == 1) {
      GraphNode singletonNode = layoutNodes.iterator().next();
      Point2D singletonLocation = locations.get(singletonNode);
      if (null != singletonLocation) {
        result.put(singletonNode, singletonLocation);
      }
      return result;
    }

    // Scale all the nodes to fit within the indicated region
    LayoutScaler scaler = new LayoutScaler(layoutNodes, locations);
    double scaleView = scaleWithMargin(scaler, viewport);
    Point2dUtils.Translater translater =
            Point2dUtils.newScaleTranslater(scaleView, scaleView);
    return Point2dUtils.translateNodes(layoutNodes, locations, translater);
  }

  private static double scaleWithMargin(
          LayoutScaler scaler, Rectangle2D viewport) {
    return FULLSCALE_MARGIN
            * scaler.getFullViewScale(viewport, ZERO_THRESHOLD);
  }
}
