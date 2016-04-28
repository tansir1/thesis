package thesis.core.uav.logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.SimTime;
import thesis.core.uav.UAV;
import thesis.core.utilities.LoggerIDs;
import thesis.core.weapons.Weapon;

public class TaskAllocator
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private final int hostUavId;

   private TaskType curTask;
   private TargetBelief curTgt;

   public TaskAllocator(int hostUavId)
   {
      this.hostUavId = hostUavId;
      curTask = TaskType.Search;
      curTgt = null;
   }

   public TargetBelief getTarget()
   {
      return curTgt;
   }

   public TaskType getTaskType()
   {
      return curTask;
   }

   public void stepSimulation(WorldBelief curBelief, UAV hostUAV)
   {
      // See if this UAV still owns the current assigned task, update scores
      switch (curTask)
      {
      case Attack:
         checkForAttackUpdates(hostUAV);
         break;
      case Monitor:
         checkForMonitorUpdates(hostUAV);
         break;
      case Search:
         break;
      }

      if (curTask == TaskType.Search)
      {
         bidOnTasks(curBelief, hostUAV);
      }

   }

   private void checkForAttackUpdates(UAV hostUAV)
   {
      if (curTgt.getTaskStatus().getAttackUAV() == hostUavId)
      {
         int myBid = computeAttackBid(curTgt, hostUAV);
         curTgt.getTaskStatus().setAttackUAVScore(myBid);
         curTgt.setTimestamp(SimTime.getCurrentSimTimeMS());
         // if(curTgt.getTaskStatus().getInterestedAttackUAVScore() > myBid)
         // {
         // //Someone else can do better than me. Give them some time to get
         // //in range.
         // }
         // I'm still the best attacker
      }
      else if (curTgt.getTaskStatus().getAttackUAV() == UAV.NULL_UAV_ID)
      {
         // Target was attacked. Go do something else.
         logger.trace("UAV {} sees attack task of {} as complete.  Changing to Search.", hostUAV,
               curTgt.getTrueTargetID());
         curTgt = null;
         curTask = TaskType.Search;
      }
      else// Someone else has a better attack score and is within range
      {
         logger.debug("UAV {} forced from task Attack to Search.", hostUAV);
         curTgt = null;
         curTask = TaskType.Search;
      }
   }

   private void checkForMonitorUpdates(UAV hostUAV)
   {
      if (curTgt.getTaskStatus().getMonitorUAV() == hostUavId)
      {
         int myBid = computeMonitorBid(curTgt, hostUAV);
         curTgt.getTaskStatus().setMonitorUAVScore(myBid);
         curTgt.setTimestamp(SimTime.getCurrentSimTimeMS());
      }
      else// Someone else has a better monitor score and is within range
      {
         logger.debug("UAV {} forced from task Monitor to Search.", hostUAV);
         curTgt = null;
         curTask = TaskType.Search;
      }
   }

   private void bidOnTasks(WorldBelief curBelief, UAV hostUAV)
   {
      Map<TargetBelief, Integer> monitorBids = new HashMap<TargetBelief, Integer>();
      Map<TargetBelief, Integer> attackBids = new HashMap<TargetBelief, Integer>();

      // Compute bids for all target tasks. Go do the highest one. Don't edit
      // bids until all have been computed

      List<TargetBelief> tgts = curBelief.getTargetBeliefs();
      final int numTgts = tgts.size();
      for (int i = 0; i < numTgts; ++i)
      {
         TargetBelief tb = tgts.get(i);

         monitorBids.put(tb, computeMonitorBid(tb, hostUAV));
         attackBids.put(tb, computeAttackBid(tb, hostUAV));
      }

      TargetBelief bestAttackTgt = findBestAvailableAttack(attackBids);
      TargetBelief bestMonitorTgt = findBestAvailableMonitor(monitorBids);

      if (bestAttackTgt != null)
      {
         int bid = attackBids.get(bestAttackTgt);

         bestAttackTgt.getTaskStatus().setAttackState(TaskState.Enroute);
         bestAttackTgt.getTaskStatus().setAttackUAV(hostUavId);
         bestAttackTgt.getTaskStatus().setAttackUAVScore(bid);
         bestAttackTgt.getTaskStatus().setAttackUpdateTimestamp(SimTime.getCurrentSimTimeMS());

         curTgt = bestAttackTgt;
         curTask = TaskType.Attack;
      }
      else if (bestMonitorTgt != null)
      {
         int bid = monitorBids.get(bestMonitorTgt);

         bestMonitorTgt.getTaskStatus().setMonitorState(TaskState.Enroute);
         bestMonitorTgt.getTaskStatus().setMonitorUAV(hostUavId);
         bestMonitorTgt.getTaskStatus().setMonitorUAVScore(bid);
         bestMonitorTgt.getTaskStatus().setMonitorUpdateTimestamp(SimTime.getCurrentSimTimeMS());

         curTgt = bestMonitorTgt;
         curTask = TaskType.Monitor;
      }
      // else keep searching
   }

   private TargetBelief findBestAvailableMonitor(Map<TargetBelief, Integer> bids)
   {
      int bestBid = -1;
      TargetBelief bestTgt = null;

      Iterator<TargetBelief> itr = bids.keySet().iterator();
      while (itr.hasNext())
      {
         TargetBelief tb = itr.next();

         if (tb.getTaskStatus().getMonitorState() == TaskState.Complete
               || tb.getTaskStatus().getMonitorState() == TaskState.Performing)
         {
            // Target cannot be monitored
            continue;
         }

         if (tb.getTaskStatus().getMonitorState() == TaskState.NO_TASK)
         {
            tb.getTaskStatus().setMonitorState(TaskState.Open);
         }

         // Task is Open, EnRoute

         int myBid = bids.get(tb);

         // If myBid > currently monitoring uav's bid
         if (myBid > tb.getTaskStatus().getMonitorUAVScore())
         {
            // If my current bid > than my best monitor bid then store this bid
            if (myBid > bestBid)
            {
               bestBid = myBid;
               bestTgt = tb;
            }
         }
      }

      return bestTgt;
   }

   private TargetBelief findBestAvailableAttack(Map<TargetBelief, Integer> atkBids)
   {
      int bestBid = -1;
      TargetBelief bestTgt = null;

      Iterator<TargetBelief> itr = atkBids.keySet().iterator();
      while (itr.hasNext())
      {
         TargetBelief tb = itr.next();

         if (tb.getTaskStatus().getAttackState() == TaskState.NO_TASK
               || tb.getTaskStatus().getAttackState() == TaskState.Complete
               || tb.getTaskStatus().getAttackState() == TaskState.Performing)
         {
            // Target cannot be attacked
            continue;
         }

         // Task is Open or EnRoute

         int myBid = atkBids.get(tb);

         // If myBid > currently attacking uav's bid
         if (myBid > tb.getTaskStatus().getAttackUAVScore())
         {
            // If my current bid is better than my best attack bid then store
            // this bid
            if (myBid > bestBid)
            {
               bestBid = myBid;
               bestTgt = tb;
            }
         }
      }

      return bestTgt;
   }

   private int computeMonitorBid(TargetBelief tgt, UAV hostUAV)
   {
      double distance = tgt.getCoordinate().distanceTo(hostUAV.getPathing().getCoordinate());
      // int mostLikelyType = tgt.getHighestProbabilityTargetType();
      // double bestProb = hostUAV.getSensors().getBestScanProb(mostLikelyType);

      // FIXME For now just use distance as the cost function
      return (int) distance;
   }

   private int computeAttackBid(TargetBelief tgt, UAV hostUAV)
   {
      int bid = -1;

      Weapon wpn = hostUAV.getWeapons().getBestWeapon(tgt.getHighestProbabilityTargetType());
      if (wpn != null)
      {
         double distance = tgt.getCoordinate().distanceTo(hostUAV.getPathing().getCoordinate());
         // int mostLikelyType = tgt.getHighestProbabilityTargetType();

         // FIXME For now just use distance as the cost function
         bid = (int) distance;
      }

      return bid;
   }
}
