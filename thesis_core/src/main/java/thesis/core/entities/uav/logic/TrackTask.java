package thesis.core.entities.uav.logic;

import java.util.List;

import thesis.core.common.SimTime;
import thesis.core.entities.belief.TargetBelief;
import thesis.core.entities.uav.UAV;

public class TrackTask extends UAVTask
{
   /**
    * When this amount of simulation time (milliseconds) elapses the UAV will
    * recompute a new path to the target if it is mobile.
    */
   private static long RECOMPUTE_PATH_TGT_INTERVAL_MS = 250;

   private long lastPathComputeTimeAccumulator;

   private TargetBelief target;
   private UAV uav;

   public TrackTask(UAV uav, TargetBelief tgt)
   {
      super(TaskType.Track);
      this.uav = uav;
      this.target = tgt;

      lastPathComputeTimeAccumulator = 0;
      uav.getPathing().computePathTo(target.getPose());
   }

   public TargetBelief getTarget()
   {
      return target;
   }

   @Override
   public void stepSimulation()
   {
      List<TargetBelief> matches = uav.getBelief().getMatchingTargets(target);

      for(TargetBelief match : matches)
      {
         target.merge(match);
      }

      uav.getSensors().stareAtAll(target.getPose().getCoordinate());
      if (target.isMobile())
      {
         lastPathComputeTimeAccumulator += SimTime.SIM_STEP_RATE_MS;
         if (lastPathComputeTimeAccumulator > RECOMPUTE_PATH_TGT_INTERVAL_MS)
         {
            lastPathComputeTimeAccumulator = 0;
            uav.getPathing().computePathTo(target.getPose());
            // TODO Compute a loiter pattern around target?
         }
      }
   }


   public boolean isTargetInRange()
   {
      return uav.getSensors().isInSensorRange(target.getPose().getCoordinate());
   }

   public boolean isTargetInView()
   {
      return uav.getSensors().isInView(target.getPose().getCoordinate());
   }
}
