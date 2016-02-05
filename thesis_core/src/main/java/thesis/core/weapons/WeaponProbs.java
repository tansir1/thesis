package thesis.core.weapons;

public class WeaponProbs
{
   private double wpnDestroy[][];

   public WeaponProbs()
   {

   }

   public int getNumWeaponTypes()
   {
      return wpnDestroy.length;
   }

   public int getNumTargetTypes()
   {
      return wpnDestroy[0].length;
   }

   public void reset(int numWpnTypes, int numTgtTypes)
   {
      wpnDestroy = new double[numWpnTypes][numTgtTypes];

      for(int i=0; i<wpnDestroy.length; ++i)
      {
         for(int j=0; j<wpnDestroy[i].length; ++j)
         {
            wpnDestroy[i][j] = -1.0f;//-1 probability is used as an unset error flag
         }
      }
   }

   public double getWeaponDestroyProb(int wpnType, int tgtType)
   {
      return wpnDestroy[wpnType][tgtType];
   }

   public void setWeaponDestroyProb(int wpnType, int tgtType, double prob)
   {
      wpnDestroy[wpnType][tgtType] = prob;
   }
}
