package thesis.core.uav;

import java.util.BitSet;

public class UAVSensorCfgs
{
   private BitSet snsrsOnUAV[];

   public UAVSensorCfgs()
   {

   }

   public void reset(int numUAVTypes, int numSnsrTypes)
   {
      snsrsOnUAV = new BitSet[numUAVTypes];
      for(int i=0; i<numUAVTypes; ++i)
      {
         snsrsOnUAV[i] = new BitSet(numSnsrTypes);
      }
   }

   public void addSensorToUAV(int uavType, int snsrType)
   {
      snsrsOnUAV[uavType].set(snsrType);
   }

   public boolean uavHasSensor(int uavType, int snsrType)
   {
      return snsrsOnUAV[uavType].get(snsrType);
   }
}
