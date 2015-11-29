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

   public boolean isValidForm()
   {
      boolean valid = true;

      // Ensure left points are more west than right points
      if (topLeft.getEast().asMeters() < topRight.getEast().asMeters()
            || topLeft.getEast().asMeters() < bottomRight.getEast().asMeters()
            || bottomLeft.getEast().asMeters() < topRight.getEast().asMeters()
            || bottomLeft.getEast().asMeters() < bottomRight.getEast().asMeters())
      {
         valid = false;
      }
      // Ensure top points are more north than bottom points
      else if (topLeft.getNorth().asMeters() < bottomLeft.getNorth().asMeters()
            || topLeft.getNorth().asMeters() < bottomRight.getNorth().asMeters()
            || topRight.getNorth().asMeters() < bottomLeft.getNorth().asMeters()
            || topRight.getNorth().asMeters() < bottomRight.getNorth().asMeters())
      {
         valid = false;
      }
      else if (topLeft.getEast().asMeters() < topRight.getEast().asMeters()
            || bottomLeft.getEast().asMeters() < bottomRight.getEast().asMeters())
      {
         valid = false;
      }

      return valid;
   }

   public Distance getWidth()
   {
      return topLeft.distanceTo(topRight);
   }

   public Distance getHeight()
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

      double widthM = getWidth().asMeters();
      double heightM = getHeight().asMeters();
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
      double d1 = Math.abs(pt1.distanceTo(pt2).asMeters());
      double d2 = Math.abs(pt2.distanceTo(pt3).asMeters());
      double d3 = Math.abs(pt3.distanceTo(pt1).asMeters());

      double halfPerim = (d1 + d2 + d3) / 2.0;
      return Math.sqrt(halfPerim * (halfPerim - d1) * (halfPerim - d2) * (halfPerim - d3));
   }
}
