package thesis.core.weapons;

import thesis.core.common.Angle;
import thesis.core.common.WorldPose;
import thesis.core.targets.TargetMgr;

public class Weapon
{
   /**
    * The minimum range of the weapon in meters.
    */
   private final double MIN_RNG;

   /**
    * The max range of the weapon in meters.
    */
   private final double MAX_RNG;

   /**
    * The launch angle of the weapon in degrees.
    */
   private final double LAUNCH_ANGLE;

   private final int type;
   private final int id;

   private final WorldPose pose;
   private final TargetMgr tgtMgr;

   private int quantity;

   public Weapon(int type, int id, WeaponTypeConfigs cfgs, TargetMgr tgtMgr, int initQty)
   {
      if (cfgs == null)
      {
         throw new NullPointerException("Type configs cannot be null.");
      }

      if (tgtMgr == null)
      {
         throw new NullPointerException("Target manager cannot be null.");
      }

      this.type = type;
      this.id = id;
      this.tgtMgr = tgtMgr;

      pose = new WorldPose();

      MIN_RNG = cfgs.getMinRange(type);
      MAX_RNG = cfgs.getMaxRange(type);
      LAUNCH_ANGLE = cfgs.getFOV(type);
      quantity = initQty;
   }

   public int getType()
   {
      return type;
   }

   public int getID()
   {
      return id;
   }

   /**
    * Get the maximum range of the weapon in meters.
    *
    * @return Max range in meters.
    */
   public double getMaxRange()
   {
      return MAX_RNG;
   }

   /**
    * Get the maximum off boresight launch angle for the weapon.
    *
    * @return Degrees
    */
   public double getMaxLaunchErrorAngle()
   {
      return LAUNCH_ANGLE;
   }

   /**
    * Get the current azimuth of the weapon in relation to the world's zero
    * degree mark. This angle is in absolute world coordinates.
    *
    * @return The azimuth of the weapon in absolute world coordinates (degrees).
    */
   public double getAzimuth()
   {
      return pose.getHeading();
   }

   public WorldPose getPose()
   {
      return pose;
   }

   /**
    * Set the number of munitions available for this weapon on the host UAV.
    *
    * @param quantity
    *           The number of available munitions.
    */
   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   /**
    * Get the number of munitions available to launch from the host UAV.
    *
    * @return The number of available munitions.
    */
   public int getQuantity()
   {
      return quantity;
   }

   /**
    * Determine if the target is within the launch acceptability region of the
    * weapon.
    *
    * @param tgtPose
    *           Pose of the target.
    * @param maxRangePercent
    *           Restrict the LAR to this percentage of the max range.
    * @return True if the target is within the LAR.
    */
   public boolean isInLaunchAcceptabilityRegion(WorldPose tgtPose, double maxRangePercent)
   {
      boolean inLAR = false;

      double distToTar = pose.getCoordinate().distanceTo(tgtPose.getCoordinate());

      // Check the distance first since it's a cheap operation
      if (Math.abs(distToTar) < (MAX_RNG * maxRangePercent) && Math.abs(distToTar) > MIN_RNG)
      {
         double angToTar = pose.getCoordinate().bearingTo(tgtPose.getCoordinate());

         double leftBnd = Angle.normalizeNegPiToPi(pose.getHeading());
         double rightBnd = Angle.normalizeNegPiToPi(pose.getHeading());

         leftBnd -= LAUNCH_ANGLE / 2;
         rightBnd += LAUNCH_ANGLE / 2;

         if (Angle.isBetween(angToTar, leftBnd, rightBnd))
         {
            inLAR = true;
         }

         // FIXME Account for target's best attack angle
      }

      return inLAR;

   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder("Type: ");
      sb.append(type);
      sb.append(", Qty: ");
      sb.append(quantity);
      return sb.toString();
   }
}
