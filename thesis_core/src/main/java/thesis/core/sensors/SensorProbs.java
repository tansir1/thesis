package thesis.core.sensors;

public class SensorProbs
{
   private double snsrDetect[][];
   private double snsrConfirm[][];
   private double snsrHdgCoef[][];

   private double snsrMisClass[][][];

   public SensorProbs()
   {

   }

   public void reset(int numSnsrTypes, int numTgtTypes)
   {
      snsrDetect = new double[numSnsrTypes][numTgtTypes];
      snsrConfirm = new double[numSnsrTypes][numTgtTypes];
      snsrHdgCoef = new double[numSnsrTypes][numTgtTypes];
      snsrMisClass = new double[numSnsrTypes][numTgtTypes][numTgtTypes];

      for(int i=0; i<snsrDetect.length; ++i)
      {
         for(int j=0; j<snsrDetect[i].length; ++j)
         {
            snsrDetect[i][j] = -1d;//-1 probability is used as an unset error flag
            snsrConfirm[i][j] = -1d;
            snsrHdgCoef[i][j] = -1d;

            snsrMisClass[i][j] = new double[numTgtTypes];
            for(int k=0; k<numTgtTypes; ++k)
            {
               snsrMisClass[i][j][k] = -1d;
            }
         }
      }
   }

   public int getNumSensorTypes()
   {
      return snsrConfirm.length;
   }

   public int getNumTargetTypes()
   {
      return snsrConfirm[0].length;
   }

   public double getSensorDetectProb(int snsrType, int tgtType)
   {
      return snsrDetect[snsrType][tgtType];
   }

   public double getSensorConfirmProb(int snsrType, int tgtType)
   {
      return snsrConfirm[snsrType][tgtType];
   }

   public double getSensorMisclassifyProb(int snsrType, int detectType, int misclassifyType)
   {
      return snsrMisClass[snsrType][detectType][misclassifyType];
   }

   public double getSensorHeadingCoeff(int snsrType, int tgtType)
   {
      return snsrHdgCoef[snsrType][tgtType];
   }

   public void setSensorDetectProb(int snsrType, int tgtType, double prob)
   {
      snsrDetect[snsrType][tgtType] = prob;
   }

   public void setSensorConfirmProb(int snsrType, int tgtType, double prob)
   {
      snsrConfirm[snsrType][tgtType] = prob;
   }

   public void setSensorHeadingCoeff(int snsrType, int tgtType, double coeff)
   {
      snsrHdgCoef[snsrType][tgtType] = coeff;
   }

   public void setSensorMisclassifyProb(int snsrType, int detectTgtType, int misclassTgtType, double prob)
   {
      snsrMisClass[snsrType][detectTgtType][misclassTgtType] = prob;
   }
}
