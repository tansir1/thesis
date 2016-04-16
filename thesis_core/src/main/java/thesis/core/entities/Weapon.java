package thesis.core.entities;

import thesis.core.common.Angle;
import thesis.core.common.WorldCoordinate;

@Deprecated
public class Weapon
{
   /**
    * The minimum range of the sensor in meters.
    */
   private final double MIN_RNG;
   /**
    * The max range of the sensor in meters.
    */
   private final double MAX_RNG;
   /**
    * The FOV of the sensor in degrees.
    */
   private final double FOV;

   private final int type;
   private int quantity;

   public Weapon(WeaponType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type.getTypeID();
      quantity = 0;

      MIN_RNG = type.getMinRange();
      MAX_RNG = type.getMaxRange();
      FOV = type.getFov();
   }

   public int getType()
   {
      return type;
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
    * Determine if the target is within range of the weapon.
    *
    * @param wpnCoord
    *           Current location of the weapon.
    * @param wpnHdg
    *           Current heading of the weapon in degrees.
    * @param tarCoord
    *           Location of the target.
    * @return True if the target can be hit by the weapon, false otherwise.
    */
   public boolean isInRange(WorldCoordinate wpnCoord, double wpnHdg, WorldCoordinate tarCoord)
   {
      boolean inRange = false;

      double distToTar = wpnCoord.distanceTo(tarCoord);

      //Check the distance first since it's a cheap operation
      if(Math.abs(distToTar) < MAX_RNG &&
            Math.abs(distToTar) > MIN_RNG)
      {
         //Computing angles is mathematically expensive, only do it if the range check passes first

         double angToTar = wpnCoord.bearingTo(tarCoord);

         double leftBnd = Angle.normalizeNegPiToPi(wpnHdg);
         double rightBnd = Angle.normalizeNegPiToPi(wpnHdg);

         leftBnd -= FOV / 2;
         rightBnd += FOV / 2;

         if(Angle.isBetween(angToTar, leftBnd, rightBnd))
         {
            inRange = true;
         }
      }

      return inRange;
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
