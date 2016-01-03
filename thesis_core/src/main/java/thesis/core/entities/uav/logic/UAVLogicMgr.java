package thesis.core.entities.uav.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import thesis.core.entities.Target;
import thesis.core.entities.belief.BeliefState;
import thesis.core.entities.belief.TargetBelief;
import thesis.core.entities.sensors.SensorDetections;
import thesis.core.entities.sensors.SensorProbs;
import thesis.core.entities.uav.comms.Message;

public class UAVLogicMgr
{
   private final SensorProbs sensorProbs;
   private final Random randGen;

   public UAVLogicMgr(SensorProbs sensorProbs, Random randGen)
   {
      if (sensorProbs == null)
      {
         throw new NullPointerException("Sensor probs cannot be null.");
      }

      if (randGen == null)
      {
         throw new NullPointerException("Random cannot be null.");
      }

      this.sensorProbs = sensorProbs;
      this.randGen = randGen;
   }

   public void stepSimulation(List<SensorDetections> detections, BeliefState curBelief, List<Message> incomingMsgs)
   {
      for (TargetBelief tb : scanForTargets(detections))
      {
         curBelief.mergeTarget(tb);
      }
   }

   private List<TargetBelief> scanForTargets(List<SensorDetections> detections)
   {
      List<TargetBelief> tgtBeliefs = new ArrayList<TargetBelief>();
      for (SensorDetections sd : detections)
      {
         for (Target tgt : sd.getTgtsInFOV())
         {
            // TODO Need logic to check state of UAV to determine if it should
            // be
            // detecting or identifying

            float detectProb = sensorProbs.getDetectionProb(sd.getSensorType(), tgt.getType());
            float idProb = sensorProbs.getIdentificationProb(sd.getSensorType(), tgt.getType());
            // TODO Need to add probabilities of detection.
            // For now 100% detection to test sensor update logic and beliefs
            TargetBelief tb = new TargetBelief(tgt.getType());
            tb.getPose().copy(tgt.getPose());
            tgtBeliefs.add(tb);
         }
      }

      return tgtBeliefs;
   }
}
