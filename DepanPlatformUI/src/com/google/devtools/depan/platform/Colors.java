package com.google.devtools.depan.platform;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import java.awt.Color;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class Colors {

  private Colors() {
    // Prevent instantiation.
  }

  /**
   * Converts the <code>String</code> representation of a color to an actual
   * <code>Color</code> object.
   *
   * @param value String representation of the color in "r,g,b" format (e.g.
   * "100,255,0")
   * @return <code>Color</code> object that matches the red-green-blue values
   * provided by the parameter. Returns <code>null</code> for empty string.
   */
  public static Color stringToColor(String value) {
    try {
      if (!value.equals("")) {
        String[] s = value.split(",");
        if (s.length == 3) {
          int red = Integer.parseInt(s[0]);
          int green = Integer.parseInt(s[1]);
          int blue = Integer.parseInt(s[2]);
          return new Color(red, green, blue);
        }
      }
    } catch (NumberFormatException ex) {
      // ignore it, don't change anything.
      return null;
    } catch (IllegalArgumentException ex) {
      // if a user entered 548 as the red value....
      // ignore it, don't change anything.
      return null;
    }
    return null;
  }

  public static String getRgb(Color color) {
    return Integer.toString(color.getRed())
        + "," + Integer.toString(color.getGreen())
        + "," + Integer.toString(color.getBlue());
  }

  /**
   * Convert the given string to a color. The string must have the form "R,G,B".
   * If the conversion fails, return a red color.
   *
   * @param key
   * @return the Color corresponding to the given string R,G,B
   */
  public static Color getRgb(String key) {
    RGB rgb = StringConverter.asRGB(key, new RGB(255, 0, 0));
    return colorFromRgb(rgb);
  }

  public static RGB rgbFromColor(Color color) {
    RGB result = new RGB(color.getRed(), color.getGreen(), color.getBlue());
    return result;
  }

  public static Color colorFromRgb(RGB rgb) {
    Color result = new Color(rgb.red, rgb.green, rgb.blue);
    return result;
  }
}
