package thesis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.SimTime;
import thesis.core.targets.Target;
import thesis.core.targets.TargetMgr;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.World;

public class StatResults
{
   private boolean simFinished;
   
   private World world;

   private TargetMgr tgtMgr;
   
   private boolean allTgtsFound;
   private long timeAllTgtsFound;
   
   private boolean allTgtsDestroyed;
   private long timeAllTgtsDestroyed;
   
   public StatResults()
   {
      
   }
   
   public void reset(World world, TargetMgr tgtMgr)
   {
      simFinished = false;
      
      timeAllTgtsFound = -1;
      allTgtsFound = false;
      
      timeAllTgtsDestroyed = -1;
      allTgtsDestroyed = false;
      
      this.world = world;
      this.tgtMgr = tgtMgr;
   }
   
   
   public void stepSimulation()
   {
      //All targets destroyed
      //World uncertainty threhsold
     
      if(!allTgtsDestroyed)
      {
         boolean allDestroyed = true;
         for(Target tgt : tgtMgr.getAllTargets())
         {            
            if(tgt.isAlive())
            {
               allDestroyed = false;
            }
         }
         
         if(allDestroyed)
         {
            allTgtsDestroyed = true;
            timeAllTgtsDestroyed = SimTime.getCurrentSimTimeMS();
         }
      }
      
      if(!allTgtsFound)
      {
         boolean allTgtsDetected = true;
         for(Target tgt : tgtMgr.getAllTargets())
         {            
            if(!tgt.hasBeenDetected())
            {
               allTgtsDetected = false;
            }
         }
         
         if(allTgtsDetected)
         {
            allTgtsFound = true;
            timeAllTgtsFound = SimTime.getCurrentSimTimeMS();
         }
      }
      
      simFinished = allTgtsDestroyed && allTgtsFound;
   }
   
   public boolean endStateReached()
   {
      return simFinished;
   }
   
   public void printResults()
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      logger.info("--------SIM RESULTS------------");
      logger.info("Time all tgts detected: {}", timeAllTgtsFound);
      logger.info("Time all tgts destroyed: {}", timeAllTgtsDestroyed);
      
   }
}
