package thesis.core.uav.logic;

import thesis.core.common.SimTime;
import thesis.core.uav.UAV;

public class AttackTask
{
   private static final long MAX_INTERESTED_WAIT_TIME_MS = 1000 * 60;//Wait one minute
   private boolean waitingForInterestedUAV;
   private long startWaitTimeMS;

   public AttackTask()
   {
      waitingForInterestedUAV = false;
      startWaitTimeMS = -1;
   }

   public void stepSimulation(UAV hostUAV, boolean waitOnInterested)
   {

      if(!waitingForInterestedUAV && waitOnInterested)
      {
         startWaitTimeMS = SimTime.getCurrentSimTimeMS();

      }
   }
}
