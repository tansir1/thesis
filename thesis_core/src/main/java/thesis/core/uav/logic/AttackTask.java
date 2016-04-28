package thesis.core.uav.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.common.SimTime;
import thesis.core.common.WorldPose;
import thesis.core.sensors.SensorGroup;
import thesis.core.uav.Pathing;
import thesis.core.uav.UAV;
import thesis.core.utilities.LoggerIDs;
import thesis.core.weapons.Weapon;
import thesis.core.weapons.WeaponGroup;

public class AttackTask
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   /**
    * If the target moves from the original confirmation coordinate by this much
    * then then re-compute a route to the target.
    */
   private static final double TGT_MOVE_DIST_TO_RECOMPUTE_PATH = 100;

   /**
    * UAV will not fire at 100% max range, instead it will wait until it reaches
    * this percentage of max range before firing.
    */
   private static final double MAX_FIRE_RANGE_PERCENT = 0.8;

   private int hostUavId;

   private WorldPose tgtPose;

   public AttackTask(int hostUavId)
   {
      this.hostUavId = hostUavId;
      tgtPose = new WorldPose();
   }

   public void Reset(WorldPose tgtPose, Pathing pathing, SensorGroup snsrGrp, int tgtID)
   {
      logger.debug("UAV {} resetting to Attack target {}", hostUavId, tgtID);
      this.tgtPose.copy(tgtPose);
      pathing.computePathTo(tgtPose);

      snsrGrp.stareAtAll(tgtPose.getCoordinate());
   }

   public void stepSimulation(TargetBelief tgtBelief, Pathing pathing, WeaponGroup wpnGrp, SensorGroup snsrGrp)
   {
      Weapon wpn = wpnGrp.getBestWeapon(tgtBelief.getHighestProbabilityTargetType());
      if (wpn == null)
      {
         logger.error("UAV {} attempted to attack but it has no weapon.", hostUavId);
         return;
      }

      final double distToTar = tgtPose.getCoordinate().distanceTo(tgtBelief.getCoordinate());

      if(tgtBelief.getTaskStatus().getAttackState() != TaskState.Performing && distToTar < wpn.getMaxRange())
      {
         logger.debug("UAV {} in range of Attack target {}.  Changing state from EnRoute to Performing.", hostUavId, tgtBelief.getTrueTargetID());
         tgtBelief.getTaskStatus().setAttackState(TaskState.Performing);
      }

      // Recompute a path if the target has moved significantly
      if (distToTar > TGT_MOVE_DIST_TO_RECOMPUTE_PATH)
      {
         Reset(tgtBelief.getPose(), pathing, snsrGrp, tgtBelief.getTrueTargetID());
      }

      // If the target is in the LAR then fire
      if (wpnGrp.getAttackLogic().isInLaunchAcceptabilityRegion(tgtBelief.getPose(), MAX_FIRE_RANGE_PERCENT, wpn, pathing.getPose()))
      {
         logger.debug("UAV {} fired at target {}", hostUavId, tgtBelief.getTrueTargetID());
         wpnGrp.getAttackLogic().fireAtTarget(tgtBelief, wpn, hostUavId);
         tgtBelief.getTaskStatus().setAttackState(TaskState.Complete);
         tgtBelief.getTaskStatus().setAttackUAV(UAV.NULL_UAV_ID);
         tgtBelief.getTaskStatus().setAttackUAVScore(-1);
         tgtBelief.getTaskStatus().setAttackUpdateTimestamp(SimTime.getCurrentSimTimeMS());
      }

   }
}
