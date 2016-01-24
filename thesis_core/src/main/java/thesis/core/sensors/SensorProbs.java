package thesis.core.sensors;

public class SensorProbs
{
   private float snsrDetect[][];
   private float snsrConfirm[][];

   public SensorProbs()
   {

   }

   public void reset(int numSnsrTypes, int numTgtTypes)
   {
      snsrDetect = new float[numSnsrTypes][numTgtTypes];
      snsrConfirm = new float[numSnsrTypes][numTgtTypes];

      for(int i=0; i<snsrDetect.length; ++i)
      {
         for(int j=0; j<snsrDetect[i].length; ++j)
         {
            snsrDetect[i][j] = -1.0f;//-1 probability is used as an unset error flag
            snsrConfirm[i][j] = -1.0f;
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

   public void setSensorDetectProb(int snsrType, int tgtType, float prob)
   {
      snsrDetect[snsrType][tgtType] = prob;
   }

   public void setSensorConfirmProb(int snsrType, int tgtType, float prob)
   {
      snsrConfirm[snsrType][tgtType] = prob;
   }
}
