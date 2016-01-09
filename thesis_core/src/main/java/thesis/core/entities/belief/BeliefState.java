package thesis.core.entities.belief;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thesis.core.common.SimTime;
import thesis.core.uav.comms.BeliefStateMsg;
import thesis.core.uav.comms.Message;
import thesis.core.uav.comms.UAVComms;

/**
 * Overall container for everything that a UAV thinks it knows about the world.
 */
public class BeliefState
{
   private List<TargetBelief> targets;
   private Map<Integer, OtherUAVBelief> team;

   /**
    * If two {@link TargetBelief}s of the same type are this close together
    * (meters) then it is assumed that they are the same target.
    */
   private final double TGT_MERGE_THRESHOLD = 100;

   /**
    * The confidence of all belief states drop by this much per frame. TODO This
    * should be set externally somehow.
    */
   private final float CONF_DECAY_RATE = // 2% per second
   (float) (0.02 * SimTime.SIM_STEP_RATE_S);

   /**
    * When this amount of simulation time elapses the UAV will broadcast its
    * current belief state.
    */
   private static long BELIEF_BROADCAST_RATE_MS = 1000;// Broadcast at 1hz

   private long lastBeliefBroadcastTimeAccumulator;

   public BeliefState()
   {
      targets = new ArrayList<TargetBelief>();
      team = new HashMap<Integer, OtherUAVBelief>();

      lastBeliefBroadcastTimeAccumulator = 0;
   }

   /**
    * Decays confidence values.
    */
   public void stepSimulation(UAVComms comms)
   {
      for (OtherUAVBelief otherUAV : team.values())
      {
         otherUAV.setConfidence(otherUAV.getConfidence() - CONF_DECAY_RATE);
      }

      for (TargetBelief tb : targets)
      {
         tb.setConfidence(tb.getConfidence() - CONF_DECAY_RATE);
      }

      lastBeliefBroadcastTimeAccumulator += SimTime.SIM_STEP_RATE_MS;
      if (lastBeliefBroadcastTimeAccumulator > BELIEF_BROADCAST_RATE_MS)
      {
         lastBeliefBroadcastTimeAccumulator = 0;
         BeliefStateMsg msg = new BeliefStateMsg(this);
         comms.transmit(msg, Message.BROADCAST_ID);
      }
   }

   /**
    * Get all targets the UAV thinks it knows about.
    *
    * @return
    */
   public List<TargetBelief> getTargets()
   {
      return Collections.unmodifiableList(targets);
   }

   /**
    * Search all target beliefs to find one that matches the type and vicinity
    * of the given target belief.
    *
    * @param matchMe Compare all targets against this one.
    * @return A list of target beliefs that might match the given one.
    */
   public List<TargetBelief> getMatchingTargets(TargetBelief matchMe)
   {
      List<TargetBelief> possibleMatches = new ArrayList<TargetBelief>();
      for (TargetBelief existing : targets)
      {
         // Cannot merge targets of different types
         if (existing.getType() == matchMe.getType())
         {
            double dist = existing.getPose().getCoordinate().distanceTo(matchMe.getPose().getCoordinate());
            dist = Math.abs(dist);
            if (dist < TGT_MERGE_THRESHOLD)
            {
               possibleMatches.add(existing);
            }
         }
      }
      return possibleMatches;
   }

   /**
    * Add another UAV teammate to this UAV's belief.
    *
    * @param oub
    *           The teammate to add.
    */
   public void addUAV(OtherUAVBelief oub)
   {
      team.put(oub.getUavID(), oub);
   }

   /**
    * Get the belief data about a teammate UAV.
    *
    * @param id
    *           The ID of the teammate to retrieve.
    * @return The requested belief information or null if no such data exists.
    */
   public OtherUAVBelief getOtherUAV(int id)
   {
      return team.get(id);
   }

   /**
    * Get all teammate UAVs that this UAV knows about.
    *
    * @return
    */
   public Collection<OtherUAVBelief> getOtherUAVs()
   {
      return team.values();
   }

   /**
    * Merge the given belief state into the calling state.
    *
    * @param mergeIn
    *           Merge this belief into the calling belief.
    */
   public void mergeBelief(BeliefState mergeIn)
   {
      // TODO Merge other UAVs
      // mergeIn.getOtherUAVs();

      for (TargetBelief tb : mergeIn.targets)
      {
         mergeTarget(tb);
      }
   }

   public void mergeTarget(TargetBelief tb)
   {
      boolean merged = false;
      for (TargetBelief existing : targets)
      {
         // Cannot merge targets of different types
         if (existing.getType() == tb.getType())
         {
            double dist = existing.getPose().getCoordinate().distanceTo(tb.getPose().getCoordinate());
            dist = Math.abs(dist);
            if (dist < TGT_MERGE_THRESHOLD)
            {
               merged = true;
               existing.merge(tb);
               break;
            }
         }
      }

      if (!merged)
      {
         targets.add(tb);
      }
   }
}
