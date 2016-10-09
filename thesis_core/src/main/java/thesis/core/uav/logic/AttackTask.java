package thesis.core.uav.logic;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.common.Angle;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.sensors.SensorGroup;
import thesis.core.targets.TargetTypeConfigs;
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

   /**
    * This is a work-around for low fidelity path planning limitations.
    * 
    * If a UAV is attacking a static target it's possible to get stuck in an orbit at the shortest path
    * route due to trying to get the target in launch angle.  If we exceed this timeout then recompute
    * the strike path using the longer route.
    */
   private static final long MAX_STATIC_TGT_ORBIT_TIME_MS = 1000 * 60 * 2;
   
   private int hostUavId;

   private TargetTypeConfigs tgtTypeCfgs;
   private TargetBelief tgtBlf;
   private WorldCoordinate strikeCoord;
   
   private long performingStartTime;
   private Random rand;
   
   public AttackTask(int hostUavId, TargetTypeConfigs tgtTypeCfgs, Random rand)
   {
      this.hostUavId = hostUavId;
      this.tgtTypeCfgs = tgtTypeCfgs;
      this.rand = rand;
      
      strikeCoord = new WorldCoordinate();
      performingStartTime = 0;
   }

   public void Reset(TargetBelief tgtBlf, Pathing pathing, SensorGroup snsrGrp)
   {
      logger.debug("UAV {} resetting to Attack target {}", hostUavId, tgtBlf.getTrueTargetID());
      this.tgtBlf = tgtBlf;
      strikeCoord.setCoordinate(tgtBlf.getCoordinate());

      WorldPose attackPose1 = computeStrikePose(pathing, true);
      WorldPose attackPose2 = computeStrikePose(pathing, false);
      
      pathing.computePathByDistance(attackPose1, attackPose2, true);
      snsrGrp.stareAtAll(strikeCoord);
   }
   
   public void staticLoiterBreakoutOverride(Pathing pathing)
   {
      WorldPose attackPose1 = computeStrikePose(pathing, true);
      WorldPose attackPose2 = computeStrikePose(pathing, false);
      
      pathing.computePathByDistance(attackPose1, attackPose2, false);
   }

   private WorldPose computeStrikePose(Pathing pathing, boolean addBestAngle)
   {
      double bestAngle = tgtTypeCfgs.getBestAngle(tgtBlf.getHighestProbabilityTargetType());
      //double distToTgt = pathing.getCoordinate().distanceTo(strikeCoord);
      
      WorldPose attackPose = new WorldPose();
      double attackAngle = Angle.normalize360(tgtBlf.getHeadingEstimate() + bestAngle);
      
      if(addBestAngle)
      {
         attackAngle = Angle.normalize360(tgtBlf.getHeadingEstimate() + bestAngle);
      }
      else
      {
         attackAngle = Angle.normalize360(tgtBlf.getHeadingEstimate() - bestAngle);
      }
      
      attackPose.getCoordinate().setCoordinate(strikeCoord);

      //Don't care about weapons range, just looking for a position to orient the platform
      //double strikeDist = (distToTgt * rand.nextFloat()) + distToTgt * 0.5;
      //attackPose.getCoordinate().translatePolar(attackAngle, strikeDist);
      attackPose.setHeading(Angle.normalize360(attackAngle + 180));//Reverse the heading to face the target
      
      return attackPose;
   }
   
   public void stepSimulation(TargetBelief tgtBelief, Pathing pathing, WeaponGroup wpnGrp, SensorGroup snsrGrp)
   {
      Weapon wpn = wpnGrp.getBestWeapon(tgtBelief.getHighestProbabilityTargetType());
      if (wpn == null)
      {
         logger.error("UAV {} attempted to attack but it has no weapon.", hostUavId);
         return;
      }

      final double distToTar = strikeCoord.distanceTo(tgtBelief.getCoordinate());

      if(tgtBelief.getTaskStatus().getAttackState() != TaskState.Performing && distToTar < wpn.getMaxRange())
      {
         logger.debug("UAV {} in range of Attack target {}.  Changing state from EnRoute to Performing.", hostUavId, tgtBelief.getTrueTargetID());
         tgtBelief.getTaskStatus().setAttackState(TaskState.Performing);
         performingStartTime = SimTime.getCurrentSimTimeMS();
      }

      // Recompute a path if the target has moved significantly
      if (distToTar > TGT_MOVE_DIST_TO_RECOMPUTE_PATH)
      {
         Reset(tgtBlf, pathing, snsrGrp);
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
         performingStartTime = 0;
      }
      
      if((SimTime.getCurrentSimTimeMS() - performingStartTime) > MAX_STATIC_TGT_ORBIT_TIME_MS)
      {
         logger.debug("UAV {} stuck in a no LAR shortest path/static target orbit.  Reseting to longer route.", hostUavId);
         performingStartTime = SimTime.getCurrentSimTimeMS();
         staticLoiterBreakoutOverride(pathing);
      }

   }
}
