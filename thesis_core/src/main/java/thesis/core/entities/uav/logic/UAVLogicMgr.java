package thesis.core.entities.uav.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.entities.belief.BeliefState;
import thesis.core.entities.belief.TargetBelief;
import thesis.core.entities.uav.comms.BeliefStateMsg;
import thesis.core.entities.uav.comms.Message;
import thesis.core.sensors.SensorDetections;
import thesis.core.sensors.SensorProbs;
import thesis.core.targets.Target;
import thesis.core.utilities.LoggerIDs;

public class UAVLogicMgr
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private final SensorProbs sensorProbs;
   private final Random randGen;
   private final int hostUavId;

   public UAVLogicMgr(SensorProbs sensorProbs, Random randGen, int hostUavId)
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
      this.hostUavId = hostUavId;
   }

   public void stepSimulation(List<SensorDetections> detections, BeliefState curBelief, List<Message> incomingMsgs)
   {
      for (TargetBelief tb : scanForTargets(detections))
      {
         curBelief.mergeTarget(tb);
      }

      for(Message msg : incomingMsgs)
      {
         switch(msg.getType())
         {
         case AuctionAnnounce:
            break;
         case AuctionBid:
            break;
         case AuctionLose:
            break;
         case AuctionWin:
            break;
         case BeliefState:
            processBeliefStateMsg(curBelief, msg);
            break;
         default:
            //TODO Log error, this shouldn't be possible unless a new enum type is added
            break;
         }
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

   private void processBeliefStateMsg(BeliefState curBelief, Message rawMsg)
   {
      BeliefStateMsg msg = (BeliefStateMsg)rawMsg;
      curBelief.mergeBelief(msg.getBelief());
      //logger.debug("Merged belief from {} into {}", rawMsg.getOriginatingUAV(), hostUavId);
   }
}
