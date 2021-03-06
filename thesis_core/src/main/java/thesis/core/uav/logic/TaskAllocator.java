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
import thesis.core.world.WorldGIS;

public class TaskAllocator
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private final int hostUavId;
   private final WorldGIS gis;
   
   private TaskType curTask;
   private TargetBelief curTgt;

   private TargetBelief bestMonitorTgt, bestAttackTgt;
   private int bestMonitorTgtBid, bestAttackTargetBid;

   public TaskAllocator(int hostUavId, WorldGIS gis)
   {
      this.hostUavId = hostUavId;
      this.gis = gis;
      curTask = TaskType.Search;
      curTgt = null;
      bestMonitorTgtBid = -1;
      bestAttackTargetBid = -1;
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
         if(!tb.getTaskStatus().isDestroyed())
         {
            monitorBids.put(tb, computeMonitorBid(tb, hostUAV));
            attackBids.put(tb, computeAttackBid(tb, hostUAV));
         }
      }

      findBestAvailableMonitor(monitorBids);
      findBestAvailableAttack(attackBids);

      if (bestAttackTgt != null)
      {
         int bid = attackBids.get(bestAttackTgt);
         // My attack bid is better than the stored data (guaranteed by
         // findBestAvailableAttack())
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
         // My monitor bid is better than the stored data (guaranteed by
         // findBestAvailableMonitor())
         bestMonitorTgt.getTaskStatus().setMonitorState(TaskState.Enroute);
         bestMonitorTgt.getTaskStatus().setMonitorUAV(hostUavId);
         bestMonitorTgt.getTaskStatus().setMonitorUAVScore(bid);
         bestMonitorTgt.getTaskStatus().setMonitorUpdateTimestamp(SimTime.getCurrentSimTimeMS());

         curTgt = bestMonitorTgt;
         curTask = TaskType.Monitor;
      }
      // else keep searching
   }

   private void findBestAvailableMonitor(Map<TargetBelief, Integer> bids)
   {
      int bestBid = -1;
      TargetBelief bestTgt = null;

      Iterator<TargetBelief> itr = bids.keySet().iterator();
      while (itr.hasNext())
      {
         TargetBelief tb = itr.next();

//         if (tb.getTaskStatus().getMonitorState() == TaskState.Complete
//               || tb.getTaskStatus().getMonitorState() == TaskState.Performing)
         if (tb.getTaskStatus().getMonitorState() == TaskState.Complete)
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
         int bidToBeat = tb.getTaskStatus().getMonitorUAVScore();

         /*
         if(tb.getTaskStatus().getMonitorState() == TaskState.Performing)
         {
            //Must be 20% better to take over
            bidToBeat = (int)(bidToBeat * 1.2);
            logger.warn("UAV {} had a sufficiently better monitor score to kick out UAV {} from monitoring target {}.",
                  hostUavId, tb.getTaskStatus().getMonitorUAV(), tb.getTrueTargetID());
         }*/
         
         // If myBid > currently monitoring uav's bid
         if (myBid > bidToBeat)
         {
            // If my current bid > than my best monitor bid then store this bid
            if (myBid > bestBid)
            {
               bestBid = myBid;
               bestTgt = tb;
            }
         }
      }

      bestMonitorTgtBid = bestBid;
      bestMonitorTgt = bestTgt;
   }

   private void findBestAvailableAttack(Map<TargetBelief, Integer> atkBids)
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

      bestAttackTargetBid = bestBid;
      bestAttackTgt = bestTgt;
   }

   private int computeMonitorBid(TargetBelief tgt, UAV hostUAV)
   {
      double distance = tgt.getCoordinate().distanceTo(hostUAV.getPathing().getCoordinate());
      int mostLikelyType = tgt.getHighestProbabilityTargetType();
      double bestProb = hostUAV.getSensors().getBestScanProb(mostLikelyType);
      
      double bid = -1;
      if(bestProb > -1)
      {
         //Distance is the basis for metric
         bid = gis.getMaxWorldDistance() - distance;

         //Convert prob(success) into "meters" for the metric
         bid += gis.getMaxWorldDistance() * bestProb;   
      }

      // FIXME For now just use distance as the cost function
      //double bid = gis.getMaxWorldDistance() - distance;
      return (int) bid;
   }

   private int computeAttackBid(TargetBelief tgt, UAV hostUAV)
   {
      double bid = -1;

      int likelyTgtType = tgt.getHighestProbabilityTargetType();
      if(likelyTgtType == -1)
      {
         logger.warn("Could not determine type of target for tgtBelief {} on UAV {}", tgt.getTrueTargetID(), hostUAV.getID());
      }
      else
      {
         Weapon wpn = hostUAV.getWeapons().getBestWeapon(likelyTgtType);
         if (wpn != null && wpn.getQuantity() > 0)
         {
            double distance = tgt.getCoordinate().distanceTo(hostUAV.getPathing().getCoordinate());
            double bestProb = hostUAV.getWeapons().getBestAttackProb(likelyTgtType);

            if(bestProb > -1)
            {
               //Distance is the basis for metric
               bid = gis.getMaxWorldDistance() - distance;

               //Convert prob(success) into "meters" for the metric
               bid += gis.getMaxWorldDistance() * bestProb;   
            }
            
         }
      }


      return (int)bid;
   }

   public TargetBelief getBestMonitorTarget()
   {
      return bestMonitorTgt;
   }

   public int getBestMonitorTargetBid()
   {
      return bestMonitorTgtBid;
   }

   public TargetBelief getBestAttackTarget()
   {
      return bestAttackTgt;
   }

   public int getBestAttackTargetBid()
   {
      return bestAttackTargetBid;
   }
}
