package thesis.core.common;

public class Rectangle
{
   private WorldCoordinate topLeft;
   private WorldCoordinate topRight;
   private WorldCoordinate bottomLeft;
   private WorldCoordinate bottomRight;

   public Rectangle()
   {
      topLeft = new WorldCoordinate();
      topRight = new WorldCoordinate();
      bottomLeft = new WorldCoordinate();
      bottomRight = new WorldCoordinate();
   }

   public void copy(Rectangle copy)
   {
      topLeft.setCoordinate(copy.topLeft);
      topRight.setCoordinate(copy.topRight);
      bottomLeft.setCoordinate(copy.bottomLeft);
      bottomRight.setCoordinate(copy.bottomRight);
   }

   public WorldCoordinate getTopLeft()
   {
      return topLeft;
   }

   public WorldCoordinate getTopRight()
   {
      return topRight;
   }

   public WorldCoordinate getBottomLeft()
   {
      return bottomLeft;
   }

   public WorldCoordinate getBottomRight()
   {
      return bottomRight;
   }

   public boolean isCanonicalForm()
   {
      boolean valid = true;

      // Ensure left points are more west than right points
      if (topLeft.getEast() > topRight.getEast() || topLeft.getEast() > bottomRight.getEast()
            || bottomLeft.getEast() > topRight.getEast() || bottomLeft.getEast() > bottomRight.getEast())
      {
         valid = false;
      }
      // Ensure top points are more north than bottom points
      else if (topLeft.getNorth() < bottomLeft.getNorth() || topLeft.getNorth() < bottomRight.getNorth()
            || topRight.getNorth() < bottomLeft.getNorth() || topRight.getNorth() < bottomRight.getNorth())
      {
         valid = false;
      }
      else if (topLeft.getEast() > topRight.getEast() || bottomLeft.getEast() > bottomRight.getEast())
      {
         valid = false;
      }

      return valid;
   }

   /**
    * Swaps coordinates internally to make sure that all four corners are in
    * their canonical locations.
    */
   public void convertToCanonicalForm()
   {
      double temp = 0;

      // ---Check verticals---
      if (topLeft.getNorth() < bottomLeft.getNorth())
      {
         temp = topLeft.getNorth();
         topLeft.setNorth(bottomLeft.getNorth());
         bottomLeft.setNorth(temp);
      }

      if (topRight.getNorth() < bottomRight.getNorth())
      {
         temp = topRight.getNorth();
         topRight.setNorth(bottomRight.getNorth());
         bottomRight.setNorth(temp);
      }

      // ---Check horizontals---
      if (topLeft.getEast() > topRight.getEast())
      {
         temp = topLeft.getEast();
         topLeft.setEast(topRight.getEast());
         topRight.setEast(temp);
      }

      if (bottomLeft.getEast() > bottomRight.getEast())
      {
         temp = bottomRight.getEast();
         bottomRight.setEast(bottomLeft.getEast());
         bottomLeft.setEast(temp);
      }
   }

   public double getWidth()
   {
      return topLeft.distanceTo(topRight);
   }

   public double getHeight()
   {
      return bottomLeft.distanceTo(topLeft);
   }

   public boolean isCoordinateInRegion(final WorldCoordinate testPt)
   {
      boolean inRegion = true;

      // This algorithm requires substantial computation but can work with any
      // polygon. Given the test coordinate create triangles iteratively through
      // all points of the polygon. If the sum of the area of the triangles is
      // greater than the area of the rectangle then the point is outside of the
      // rectangle.

      double widthM = getWidth();
      double heightM = getHeight();
      double rectAreaM2 = widthM * heightM;

      double testArea = triangularAreaM2(topLeft, testPt, bottomLeft);
      testArea += triangularAreaM2(bottomLeft, testPt, bottomRight);
      testArea += triangularAreaM2(bottomRight, testPt, topRight);
      testArea += triangularAreaM2(testPt, topRight, topLeft);

      if (testArea > rectAreaM2)
      {
         inRegion = false;
      }

      return inRegion;
   }

   private double triangularAreaM2(final WorldCoordinate pt1, final WorldCoordinate pt2, final WorldCoordinate pt3)
   {
      // Heron's formula finds the area of a triangle given the distance of all
      // 3 sides
      double d1 = Math.abs(pt1.distanceTo(pt2));
      double d2 = Math.abs(pt2.distanceTo(pt3));
      double d3 = Math.abs(pt3.distanceTo(pt1));

      double halfPerim = (d1 + d2 + d3) / 2.0;
      return Math.sqrt(halfPerim * (halfPerim - d1) * (halfPerim - d2) * (halfPerim - d3));
   }
}
