package thesis.core.weapons;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.common.Angle;
import thesis.core.common.WorldPose;
import thesis.core.targets.Target;
import thesis.core.targets.TargetMgr;
import thesis.core.utilities.LoggerIDs;

public class WeaponAttackLogic
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private WeaponProbs wpnProbs;
   private TargetMgr tgtMgr;
   private Random randGen;

   public WeaponAttackLogic(WeaponProbs wpnProbs, TargetMgr tgtMgr, Random randGen)
   {
      this.wpnProbs = wpnProbs;
      this.tgtMgr = tgtMgr;
      this.randGen = randGen;
   }

   public WeaponProbs getWeaponProbs()
   {
      return wpnProbs;
   }

   /**
    * Determine if the target is within the launch acceptability region of the
    * weapon.
    *
    * @param wpnPose
    *           Pose of the weapon.
    * @param tgtPose
    *           Pose of the target.
    * @param maxRangePercent
    *           Restrict the LAR to this percentage of the max range.
    * @return True if the target is within the LAR.
    */
   public boolean isInLaunchAcceptabilityRegion(WorldPose tgtPose, double maxRangePercent, Weapon wpn, WorldPose uavPose)
   {
      boolean inLAR = false;

      final double minRng = wpn.getMinRange();
      final double maxRng = wpn.getMaxRange();
      final double wpnLaunchFOV = wpn.getMaxLaunchErrorAngle();

      double distToTar = uavPose.getCoordinate().distanceTo(tgtPose.getCoordinate());

      // Check the distance first since it's a cheap operation
      if (Math.abs(distToTar) < (maxRng * maxRangePercent) && Math.abs(distToTar) > minRng)
      {
         double angToTar = uavPose.getCoordinate().bearingTo(tgtPose.getCoordinate());

         double leftBnd = Angle.normalizeNegPiToPi(uavPose.getHeading());
         double rightBnd = Angle.normalizeNegPiToPi(uavPose.getHeading());

         leftBnd -= wpnLaunchFOV / 2;
         rightBnd += wpnLaunchFOV / 2;

         if (Angle.isBetween(angToTar, leftBnd, rightBnd))
         {
            inLAR = true;
         }

         // FIXME Account for target's best attack angle
      }

      return inLAR;
   }

   public void fireAtTarget(TargetBelief tgtBelief, Weapon wpn, int uavID)
   {
      if(wpn.getQuantity() <= 0)
      {
         throw new IllegalArgumentException("Weapon is out of munitions.");
      }

      wpn.setQuantity(wpn.getQuantity() - 1);

      Target trueTgt = tgtMgr.getTargetByID(tgtBelief.getTrueTargetID());
      double wpnDestroyProp = wpnProbs.getWeaponDestroyProb(wpn.getType(), trueTgt.getType());
      double DEBUG = randGen.nextDouble();
      //if(randGen.nextDouble() < wpnDestroyProp)
      if(DEBUG < wpnDestroyProp)
      {
         logger.debug("UAV {} destroyed target {}", uavID, tgtBelief.getTrueTargetID());
      // FIXME Account for target's best attack angle
         trueTgt.attacked();
      }
   }

}
