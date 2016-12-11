package thesis.core.sensors;

public class SensorProbs
{
   private double snsrDetectEmpty[];
   private double snsrDetectTgt[][];
   //private double snsrConfirm[][];
   private double snsrHdgCoef[][];

   private double snsrMisClass[][][];

   public SensorProbs()
   {

   }

   public void reset(int numSnsrTypes, int numTgtTypes)
   {
      snsrDetectEmpty = new double[numSnsrTypes];
      snsrDetectTgt = new double[numSnsrTypes][numTgtTypes];
     // snsrConfirm = new double[numSnsrTypes][numTgtTypes];
      snsrHdgCoef = new double[numSnsrTypes][numTgtTypes];
      snsrMisClass = new double[numSnsrTypes][numTgtTypes][numTgtTypes];

      for(int i=0; i<snsrDetectTgt.length; ++i)
      {
         snsrDetectEmpty[i] = -1d;
         for(int j=0; j<snsrDetectTgt[i].length; ++j)
         {
            snsrDetectTgt[i][j] = -1d;//-1 probability is used as an unset error flag
            //snsrConfirm[i][j] = -1d;
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
      return snsrDetectTgt.length;
   }

   public int getNumTargetTypes()
   {
      return snsrDetectTgt[0].length;
   }

   public double getSensorDetectTgtProb(int snsrType, int tgtType)
   {
      return snsrDetectTgt[snsrType][tgtType];
   }

//   public double getSensorConfirmProb(int snsrType, int tgtType)
//   {
//      return snsrConfirm[snsrType][tgtType];
//   }

   public double getSensorMisclassifyProb(int snsrType, int detectType, int misclassifyType)
   {
      return snsrMisClass[snsrType][detectType][misclassifyType];
   }

   public double getSensorHeadingCoeff(int snsrType, int tgtType)
   {
      return snsrHdgCoef[snsrType][tgtType];
   }

   public double getSensorDetectEmptyProb(int snsrType)
   {
      return snsrDetectEmpty[snsrType];
   }

   public void setSensorDetectTgtProb(int snsrType, int tgtType, double prob)
   {
      snsrDetectTgt[snsrType][tgtType] = prob;
   }

//   public void setSensorConfirmProb(int snsrType, int tgtType, double prob)
//   {
//      snsrConfirm[snsrType][tgtType] = prob;
//   }

   public void setSensorHeadingCoeff(int snsrType, int tgtType, double coeff)
   {
      snsrHdgCoef[snsrType][tgtType] = coeff;
   }

   public void setSensorMisclassifyProb(int snsrType, int detectTgtType, int misclassTgtType, double prob)
   {
      snsrMisClass[snsrType][detectTgtType][misclassTgtType] = prob;
   }

   public void setSensorDetectEmptyProb(int snsrType, double prob)
   {
      snsrDetectEmpty[snsrType] = prob;
   }
}
