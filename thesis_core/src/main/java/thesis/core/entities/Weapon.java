package thesis.core.entities;

import thesis.core.common.Angle;
import thesis.core.common.WorldCoordinate;

public class Weapon
{
   private WeaponType type;
   int quantity;

   public Weapon(WeaponType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;
      quantity = 0;
   }

   public WeaponType getType()
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
    *           Current heading of the weapon.
    * @param tarCoord
    *           Location of the target.
    * @return True if the target can be hit by the weapon, false otherwise.
    */
   public boolean isInRange(WorldCoordinate wpnCoord, Angle wpnHdg, WorldCoordinate tarCoord)
   {
      boolean inRange = false;

      double distToTar = wpnCoord.distanceTo(tarCoord);

      //Check the distance first since it's a cheap operation
      if(Math.abs(distToTar) < type.getMaxRange() &&
            Math.abs(distToTar) > type.getMinRange())
      {
         //Computing angles is mathematically expensive, only do it if the range check passes first

         Angle angToTar = wpnCoord.bearingTo(tarCoord);

         Angle leftBnd = new Angle(wpnHdg);
         Angle rightBnd = new Angle(wpnHdg);

         leftBnd.normalizeNegPiToPi();
         rightBnd.normalizeNegPiToPi();

         leftBnd.subtract(type.getFov().halfAngle());
         rightBnd.add(type.getFov().halfAngle());

         if(angToTar.isBetween(leftBnd, rightBnd))
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
      sb.append(type.getTypeID());
      sb.append(", Qty: ");
      sb.append(quantity);
      return sb.toString();
   }
}
