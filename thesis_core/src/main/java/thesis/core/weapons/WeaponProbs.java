package thesis.core.weapons;

public class WeaponProbs
{
   private float wpnDestroy[][];

   public WeaponProbs()
   {

   }

   public void reset(int numWpnTypes, int numTgtTypes)
   {
      wpnDestroy = new float[numWpnTypes][numTgtTypes];

      for(int i=0; i<wpnDestroy.length; ++i)
      {
         for(int j=0; j<wpnDestroy[i].length; ++j)
         {
            wpnDestroy[i][j] = -1.0f;//-1 probability is used as an unset error flag
         }
      }
   }

   public float getWeaponDestroyProb(int wpnType, int tgtType)
   {
      return wpnDestroy[wpnType][tgtType];
   }

   public void setWeaponDestroyProb(int wpnType, int tgtType, float prob)
   {
      wpnDestroy[wpnType][tgtType] = prob;
   }
}
