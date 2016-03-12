package thesis.core.uav.logic;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.WorldBelief;
import thesis.core.sensors.SensorGroup;
import thesis.core.targets.Target;
import thesis.core.uav.Pathing;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class ConfirmTask
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private WorldGIS gis;
   private int hostUavId;
   private Random rand;

   private int trueTgtID;

   public ConfirmTask(int hostUavId, WorldGIS gis, Random randGen)
   {
      this.hostUavId = hostUavId;
      this.gis = gis;
      this.rand = randGen;
      trueTgtID = Target.INVALID_TGT_ID;
   }

   /**
    * @param trueTgtID
    *           Assumes cross-track correlation amongst the swarm. This is the
    *           Id of the 'track' to confirm.
    */
   public void reset(int trueTgtID)
   {
      this.trueTgtID = trueTgtID;
   }

   public void stepSimulation(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {

   }
}
