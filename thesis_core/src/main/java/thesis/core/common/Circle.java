package thesis.core.common;

public class Circle
{
   private WorldCoordinate center;
   /**
    * Meters.
    */
   private double radius;

   public Circle()
   {
      center = new WorldCoordinate();
      radius = 0;
   }

   public WorldCoordinate getCenter()
   {
      return center;
   }

   /**
    * @return Meters
    */
   public double getRadius()
   {
      return radius;
   }

   /**
    * @param radius Meters
    */
   public void setRadius(double radius)
   {
      this.radius = radius;
   }

   public WorldPose minTravelToTangent(WorldPose startingPose)
   {
      double offsetBearingToLoiterPt = startingPose.getCoordinate().bearingTo(center);
      final double distToLoiterPt = Math.abs(center.distanceTo(startingPose.getCoordinate()));
      final double distToInterceptPt = Math.sqrt(radius * radius + distToLoiterPt * distToLoiterPt);

      double angleUp = Math.toDegrees(Math.atan2(radius, distToLoiterPt)) + offsetBearingToLoiterPt;
      double angleDown = Math.toDegrees(Math.atan2(-radius, distToLoiterPt)) + offsetBearingToLoiterPt;

      WorldCoordinate upWC = new WorldCoordinate(startingPose.getCoordinate());
      WorldCoordinate downWC = new WorldCoordinate(startingPose.getCoordinate());
      upWC.translatePolar(angleUp, distToInterceptPt);
      downWC.translatePolar(angleDown, distToInterceptPt);

      double bearingUp = startingPose.getCoordinate().bearingTo(upWC);
      double bearingDown = startingPose.getCoordinate().bearingTo(downWC);

      double hdg = startingPose.getHeading() - 90;
      double relBearingUp = Angle.normalizeNegPiToPi(bearingUp - hdg);
      double relBearingDown =  Angle.normalizeNegPiToPi(bearingDown - hdg);

      WorldPose tangentPose = new WorldPose();
      if(Math.abs(relBearingDown) < Math.abs(relBearingUp))
      {
         tangentPose.getCoordinate().setCoordinate(downWC);
         tangentPose.setHeading(Angle.cartesianAngleToNorthUp(bearingDown));
      }
      else
      {
         tangentPose.getCoordinate().setCoordinate(upWC);
         tangentPose.setHeading(Angle.cartesianAngleToNorthUp(bearingUp));
      }

      return tangentPose;
   }

   @Override
   public String toString()
   {
      return String.format("%s, %.2fm", center.toString(), radius);
   }
}
