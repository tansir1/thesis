package thesis.core.weapons;

import java.util.ArrayList;
import java.util.List;

public class WeaponGroup
{
   private List<Weapon> weapons;
   private double maxLaunchDistance;
   private WeaponAttackLogic atkLogic;

   public WeaponGroup(WeaponAttackLogic wpnAtkLogic)
   {
      this.atkLogic = wpnAtkLogic;
      weapons = new ArrayList<Weapon>();
      maxLaunchDistance = -1;
   }

   public void addWeapon(Weapon weapon)
   {
      weapons.add(weapon);
      maxLaunchDistance = Math.max(maxLaunchDistance, weapon.getMaxRange());
   }

   public List<Weapon> getWeapons()
   {
      return weapons;
   }

   /*
    * public void stepSimulation(WorldCoordinate hostUAVLocation, WorldBelief
    * belief, long simTime) { for (Weapon w : weapons) { //FIXME implement me }
    * }
    */

   /**
    * Get the maximum launch distance.
    *
    * @return Max sensing launch in meters.
    */
   public double getMaxLaunchRange()
   {
      return maxLaunchDistance;
   }

   public double getBestAttackProb(int tgtType)
   {
      double bestProb = -1;

      for (Weapon w : weapons)
      {
         if (w.getQuantity() > 0)
         {
            bestProb = Math.max(bestProb, atkLogic.getWeaponProbs().getWeaponDestroyProb(w.getType(), tgtType));
         }
      }
      return bestProb;
   }

   public Weapon getBestWeapon(int tgtType)
   {
      double bestProb = -1;
      Weapon bestWpn = null;
      
      if(tgtType != -1)
      {
         for (Weapon w : weapons)
         {
            if (w.getQuantity() > 0)
            {
               bestProb = Math.max(bestProb, atkLogic.getWeaponProbs().getWeaponDestroyProb(w.getType(), tgtType));
               bestWpn = w;
            }
         }
      }


      return bestWpn;
   }

   public WeaponAttackLogic getAttackLogic()
   {
      return atkLogic;
   }
   
   public boolean isOutOfAmmo()
   {
      boolean outOfAmmo = true;
      for(Weapon wpn : weapons)
      {
         if(wpn.getQuantity() > 0)
         {
            outOfAmmo = false;
            break;
         }
      }
      return outOfAmmo;
   }
}
