package thesis.core.weapons;

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

   //private final WorldPose pose;
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

      //pose = new WorldPose();

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
    * Get the minimum range of the weapon in meters.
    *
    * @return Min range in meters.
    */
   public double getMinRange()
   {
      return MIN_RNG;
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

   /*
    * Get the current azimuth of the weapon in relation to the world's zero
    * degree mark. This angle is in absolute world coordinates.
    *
    * @return The azimuth of the weapon in absolute world coordinates (degrees).
    */
/*   public double getAzimuth()
   {
      return pose.getHeading();
   }

   public WorldPose getPose()
   {
      return pose;
   }*/

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
