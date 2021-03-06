package thesis.core;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.SimTime;
import thesis.core.targets.Target;
import thesis.core.targets.TargetMgr;
import thesis.core.uav.UAV;
import thesis.core.uav.UAVMgr;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.World;

public class StatResults
{
   //10 minutes
   private static final long MAX_SIM_TIME_MS = 1000 * 60 * 10;
   
   private Logger logger;
   
   private boolean simFinished;

   private World world;

   private TargetMgr tgtMgr;

   private UAVMgr uavMgr;
   
   private boolean allTgtsFound;
   private long timeAllTgtsFound;

   private boolean allTgtsDestroyed;
   private long timeAllTgtsDestroyed;

   private boolean allWorldKnown;
   private long timeAllWorldKnown;
   
   private boolean outOfAmmo;
   private long timeOutOfAmmo;
   
   public StatResults(Logger logger)
   {
      this.logger = logger;
   }

   public void reset(World world, TargetMgr tgtMgr, UAVMgr uavMgr)
   {
      simFinished = false;

      timeAllTgtsFound = -1;
      allTgtsFound = false;

      timeAllTgtsDestroyed = -1;
      allTgtsDestroyed = false;
      
      timeAllWorldKnown = -1;
      allWorldKnown = false;
      
      timeOutOfAmmo = -1;
      outOfAmmo = false;

      this.world = world;
      this.tgtMgr = tgtMgr;
      this.uavMgr = uavMgr;
   }

   public void stepSimulation()
   {
      // --Ending critera---
      // All targets found
      // All targets destroyed
      // World uncertainty threhsold

      if (!allTgtsDestroyed)
      {
         boolean allDestroyed = true;
         for (Target tgt : tgtMgr.getAllTargets())
         {
            if (tgt.isAlive())
            {
               allDestroyed = false;
            }
         }

         if (allDestroyed)
         {
            allTgtsDestroyed = true;
            timeAllTgtsDestroyed = SimTime.getCurrentSimTimeMS();
         }
      }

      if(!outOfAmmo)
      {
         boolean allOut = true;
         for(UAV uav : uavMgr.getAllUAVs())
         {
            if(!uav.getWeapons().isOutOfAmmo())
            {
               allOut = false;
               break;
            }
         }
         
         if(allOut)
         {
            outOfAmmo = true;
            timeOutOfAmmo = SimTime.getCurrentSimTimeMS();
         }
      }
      
      if (!allTgtsFound)
      {
         boolean allTgtsDetected = true;
         for (Target tgt : tgtMgr.getAllTargets())
         {
            if (!tgt.hasBeenDetected())
            {
               allTgtsDetected = false;
            }
         }

         if (allTgtsDetected)
         {
            allTgtsFound = true;
            timeAllTgtsFound = SimTime.getCurrentSimTimeMS();
         }
      }

      if(!allWorldKnown)
      {
         boolean allWorldKnownLocal = true;
         
         UAV uavs[] = uavMgr.getAllUAVs();
         int numUAVsBelieveWorldKnown = 0;
         for(int i=0; i<uavs.length; ++i)
         {
            if(uavs[i].getBelief().believesAllWorldKnown())
            {
               numUAVsBelieveWorldKnown++;
            }
         }
         
         if(numUAVsBelieveWorldKnown < (uavs.length * 0.5))
         {
            allWorldKnownLocal = false;
         }
         
//         for(UAV uav : uavMgr.getAllUAVs())
//         {
//            if(!uav.getBelief().believesAllWorldKnown())
//            {
//               allWorldKnownLocal = false;
//            }
//         }
         
         if(allWorldKnownLocal)
         {
            allWorldKnown = true;
            timeAllWorldKnown = SimTime.getCurrentSimTimeMS();
         }
      }
      
      if(SimTime.getCurrentSimTimeMS() > MAX_SIM_TIME_MS)
      {
         simFinished = true;
         logger.error("Exceeded allowable mission time.");
      }
      else
      {
         simFinished = (allTgtsDestroyed || outOfAmmo) && allTgtsFound && allWorldKnown;   
      }
   }

   public boolean endStateReached()
   {
      return simFinished;
   }

   public void printResults()
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      logger.info("--------SIM RESULTS------------");
      logger.info("Time all tgts detected: {}ms", timeAllTgtsFound);
      logger.info("Time all tgts destroyed: {}ms", timeAllTgtsDestroyed);
      logger.info("Time out of ammo: {}ms", timeOutOfAmmo);
      logger.info("Time most UAVs believe all world known: {}ms", timeAllWorldKnown);

      for (Target tgt : tgtMgr.getAllTargets())
      {
         logger.info("Tgt {}, found: {}ms, destroyed: {}ms, delta: {}ms", tgt.getID(), tgt.getTimeFirstDetection(),
               tgt.getTimeDestroyed(), tgt.getTimeDestroyed() - tgt.getTimeFirstDetection());
      }
   }
   
   public void saveTargetResults(PrintWriter writer)
   {
      writer.println("TargetID, Time first detected (ms), Time destroyed (ms)");

      for (Target tgt : tgtMgr.getAllTargets())
      {
         StringBuilder sb = new StringBuilder();
         sb.append(tgt.getID());
         sb.append(",");
         sb.append(tgt.getTimeFirstDetection());
         sb.append(",");
         sb.append(tgt.getTimeDestroyed());
         writer.println(sb.toString());
      }
   }
   
   public long getTimeAllTargetsFound()
   {
      return timeAllTgtsFound;
   }
   
   public long getTimeAllTargetsDestroyed()
   {
      return timeAllTgtsDestroyed;
   }
   
   public long getTimeAllWorldKnown()
   {
      return timeAllWorldKnown;
   }
}
