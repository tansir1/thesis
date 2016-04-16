package thesis.core.uav;

public class UAVWeaponCfgs
{
   private int wpnOnUAV[][];

   public UAVWeaponCfgs()
   {

   }

   public int getNumUAVTypes()
   {
      return wpnOnUAV.length;
   }

   public int getNumWeaponTypes()
   {
      return wpnOnUAV[0].length;
   }

   public void reset(int numUAVTypes, int numWpnTypes)
   {
      wpnOnUAV = new int[numUAVTypes][];
      for (int i = 0; i < numUAVTypes; ++i)
      {
         wpnOnUAV[i] = new int[numWpnTypes];

         for (int j = 0; j < numWpnTypes; ++j)
         {
            wpnOnUAV[i][j] = 0;
         }
      }
   }

   public void addWeaponToUAV(int uavType, int wpnType, int initialQty)
   {
      wpnOnUAV[uavType][wpnType] = initialQty;
   }

   public boolean uavHasWeapon(int uavType, int wpnType)
   {
      return wpnOnUAV[uavType][wpnType] > 0;
   }

   public int getInitialQuantity(int uavType, int wpnType)
   {
      return wpnOnUAV[uavType][wpnType];
   }
}
