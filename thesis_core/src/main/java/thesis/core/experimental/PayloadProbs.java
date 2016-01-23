package thesis.core.experimental;

public class PayloadProbs
{
   private float snsrDetect[][];
   private float snsrConfirm[][];
   private float wpnDestroy[][];

   public PayloadProbs(int numSnsrTypes, int numWpnTypes, int numTgtTypes)
   {
      snsrDetect = new float[numSnsrTypes][numTgtTypes];
      snsrConfirm = new float[numSnsrTypes][numTgtTypes];
      wpnDestroy = new float[numWpnTypes][numTgtTypes];

      reset();
   }

   public void reset()
   {
      for(int i=0; i<snsrDetect.length; ++i)
      {
         for(int j=0; j<snsrDetect[i].length; ++j)
         {
            snsrDetect[i][j] = -1.0f;//-1 probability is used as an unset error flag
            snsrConfirm[i][j] = -1.0f;
         }
      }

      for(int i=0; i<wpnDestroy.length; ++i)
      {
         for(int j=0; j<wpnDestroy[i].length; ++j)
         {
            wpnDestroy[i][j] = -1.0f;//-1 probability is used as an unset error flag
         }
      }
   }

   public float getSensorDetectProb(int snsrType, int tgtType)
   {
      return snsrDetect[snsrType][tgtType];
   }

   public float getSensorConfirmProb(int snsrType, int tgtType)
   {
      return snsrConfirm[snsrType][tgtType];
   }

   public float getWeaponDestroyProb(int wpnType, int tgtType)
   {
      return wpnDestroy[wpnType][tgtType];
   }

   public void setSensorDetectProb(int snsrType, int tgtType, float prob)
   {
      snsrDetect[snsrType][tgtType] = prob;
   }

   public void setSensorConfirmProb(int snsrType, int tgtType, float prob)
   {
      snsrConfirm[snsrType][tgtType] = prob;
   }

   public void setWeaponDestroyProb(int wpnType, int tgtType, float prob)
   {
      wpnDestroy[wpnType][tgtType] = prob;
   }
}
