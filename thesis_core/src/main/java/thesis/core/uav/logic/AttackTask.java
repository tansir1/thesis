package thesis.core.uav.logic;

import thesis.core.belief.TargetBelief;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.uav.Pathing;

public class AttackTask
{

   /**
    * UAV will not fire at 100% max range, instead it will wait until it reaches
    * this percentage of max range before firing.
    */
   private static final double MAX_FIRE_RANGE_PERCENT = 0.8;

   public AttackTask()
   {
   }

   public void stepSimulation(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      double hdg = pathing.getHeading();
      WorldCoordinate curPos = pathing.getCoordinate();
      double bearingToTgt = curPos.bearingTo(tgtBelief.getCoordinate());

      //if hdg within launch angle of bearingToTgt then fire....and within .8 of range

   }
}
