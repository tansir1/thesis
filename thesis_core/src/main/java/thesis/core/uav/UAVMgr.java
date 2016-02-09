package thesis.core.uav;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.EntityTypeCfgs;
import thesis.core.SimModel;
import thesis.core.common.Circle;
import thesis.core.sensors.Sensor;
import thesis.core.sensors.SensorGroup;
import thesis.core.serialization.world.UAVStartCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.uav.comms.CommsConfig;
import thesis.core.uav.comms.UAVComms;
import thesis.core.uav.logic.UAVLogicMgr;
import thesis.core.utilities.LoggerIDs;

/**
 * High level manager that maintains all UAVs in the simulation.
 */
public class UAVMgr
{
   private Logger logger;
   private UAV[] uavs;

   public UAVMgr()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      uavs = null;
   }

   /**
    * Initialize the UAV manager with a collection of UAVs derived from the
    * given configuration data.
    *
    * @param entTypes
    *           UAV types will be queried from this.
    * @param worldCfg
    *           UAVs will be generated based on configuration data from here and
    *           types will be cross referenced from entTypes.
    */
   public void reset(EntityTypeCfgs entTypes, List<UAVStartCfg> uavStartCfgs, TargetMgr tgtMgr, Random randGen,
         CommsConfig commsCfg)
   {
      logger.debug("Resetting UAV Manager.");

      final int NUM_UAVS = uavStartCfgs.size();
      uavs = new UAV[NUM_UAVS];

      UAVSensorCfgs uavSensorCfgs = entTypes.getUAVSensorCfgs();
      final int NUM_SNSR_TYPES = uavSensorCfgs.getNumSensorTypes();

      UAVStartCfg uavStartCfg = null;

      for (int i = 0; i < NUM_UAVS; ++i)
      {
         final SensorGroup sensors = new SensorGroup();
         uavStartCfg = uavStartCfgs.get(i);
         int type = uavStartCfg.getUAVType();
         int snsrIDCnt = 0;

         for (int j = 0; j < NUM_SNSR_TYPES; ++j)
         {
            if (uavSensorCfgs.uavHasSensor(type, j))
            {
               Sensor sensor = new Sensor(j, snsrIDCnt, entTypes.getSnsrTypeCfgs(), tgtMgr);
               snsrIDCnt++;
               // Align sensor to point straight ahead at startup
               sensor.setAzimuth(uavStartCfg.getOrientation());
               sensors.addSensor(sensor);
            }
         }

         final UAVComms comms = new UAVComms(i, this, randGen, commsCfg);

         final Pathing pathing = new Pathing(i, type, entTypes.getUAVTypeCfgs());
         pathing.getCoordinate().setCoordinate(uavStartCfg.getLocation());
         pathing.setHeading(uavStartCfg.getOrientation());

         final UAVLogicMgr logicMgr = new UAVLogicMgr(entTypes.getSnsrProbs(), randGen, i);

         uavs[i] = new UAV(type, i, sensors, comms, pathing, logicMgr);
      }
   }

   public UAV[] getAllUAVs()
   {
      return uavs;
   }

   /**
    * Retrieve a UAV with the given ID.
    *
    * @param id
    *           The ID of the UAV to retrieve.
    * @return The requested UAV or null if no such UAV exists.
    */
   public UAV getUAV(int id)
   {
      UAV requested = null;
      for (UAV uav : uavs)
      {
         if (uav.getID() == id)
         {
            requested = uav;
            break;
         }
      }
      return requested;
   }

   /**
    * Step the simulation forward by {@link SimModel#SIM_STEP_RATE_MS} amount of
    * time.
    */
   public void stepSimulation()
   {
      for (int i = 0; i < uavs.length; ++i)
      {
         uavs[i].stepSimulation();
      }
   }

   /**
    * Get all UAVs within the specified geographic region.
    *
    * @param region
    *           Get all UAVs within this region.
    * @return A list of UAVs in the region or an empty list if no UAVs are
    *         within the region.
    */
   public List<UAV> getAllUAVsInRegion(Circle region)
   {
      List<UAV> inRegion = new ArrayList<UAV>();

      for (int i = 0; i < uavs.length; ++i)
      {
         if (Math.abs(uavs[i].getPathing().getCoordinate().distanceTo(region.getCenter())) < region.getRadius())
         {
            inRegion.add(uavs[i]);
         }
      }

      return inRegion;
   }

   /**
    * Get all UAVs within the specified geographic region.
    *
    * @param region
    *           Get all UAVs within this region.
    * @param excludeUAV
    *           ID of a UAV to exclude from the results.
    * @return A list of UAVs in the region or an empty list if no UAVs are
    *         within the region.
    */
   public List<UAV> getAllUAVsInRegion(Circle region, int excludeUAV)
   {
      List<UAV> inRegion = new ArrayList<UAV>();

      for (int i = 0; i < uavs.length; ++i)
      {
         if (Math.abs(uavs[i].getPathing().getCoordinate().distanceTo(region.getCenter())) < region.getRadius()
               && uavs[i].getID() != excludeUAV)
         {
            inRegion.add(uavs[i]);
         }
      }

      return inRegion;
   }
}
