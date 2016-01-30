package thesis.core.uav;

public class UAVSensorCfgs
{
   private boolean snsrsOnUAV[][];

   public UAVSensorCfgs()
   {

   }

   public int getNumUAVTypes()
   {
      return snsrsOnUAV.length;
   }

   public int getNumSensorTypes()
   {
      return snsrsOnUAV[0].length;
   }

   public void reset(int numUAVTypes, int numSnsrTypes)
   {
      snsrsOnUAV = new boolean[numUAVTypes][];
      for (int i = 0; i < numUAVTypes; ++i)
      {
         snsrsOnUAV[i] = new boolean[numSnsrTypes];

         for (int j = 0; j < numSnsrTypes; ++j)
         {
            snsrsOnUAV[i][j] = false;
         }
      }
   }

   public void addSensorToUAV(int uavType, int snsrType)
   {
      snsrsOnUAV[uavType][snsrType] = true;
   }

   public boolean uavHasSensor(int uavType, int snsrType)
   {
      return snsrsOnUAV[uavType][snsrType];
   }
}
