package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;

public class SensorProbs
{
   // A list is inefficient but good enough for this simulation's small number
   // of permutations
   private List<SensorToTargetProb> detectProbs;
   private List<SensorToTargetProb> identProbs;

   public SensorProbs()
   {
      detectProbs = new ArrayList<SensorToTargetProb>();
      identProbs = new ArrayList<SensorToTargetProb>();
   }

   public void reset()
   {
      detectProbs.clear();
      identProbs.clear();
   }

   public void setDetectionProb(int sensorType, int targetType, float prob)
   {
      setProb(detectProbs, sensorType, targetType, prob);
   }

   public void setIdentificationProb(int sensorType, int targetType, float prob)
   {
      setProb(identProbs, sensorType, targetType, prob);
   }

   private void setProb(List<SensorToTargetProb> probs, int sensorType, int targetType, float prob)
   {
      boolean exists = false;
      for (SensorToTargetProb stp : probs)
      {
         if (stp.sensorType == sensorType && stp.targetType == targetType)
         {
            stp.prob = prob;
            exists = true;
            break;
         }
      }

      if (!exists)
      {
         SensorToTargetProb stp = new SensorToTargetProb();
         stp.prob = prob;
         stp.sensorType = sensorType;
         stp.targetType = targetType;
         probs.add(stp);
      }
   }

   public float getDetectionProb(int sensorType, int targetType)
   {
      return getProb(detectProbs, sensorType, targetType);
   }

   public float getIdentificationProb(int sensorType, int targetType)
   {
      return getProb(identProbs, sensorType, targetType);
   }

   private float getProb(List<SensorToTargetProb> probs, int sensorType, int targetType)
   {
      float prob = -1;
      for (SensorToTargetProb stp : probs)
      {
         if (stp.sensorType == sensorType && stp.targetType == targetType)
         {
            prob = stp.prob;
            break;
         }
      }
      return prob;
   }

   private static class SensorToTargetProb
   {
      public int sensorType;
      public int targetType;
      public float prob;
   }
}
