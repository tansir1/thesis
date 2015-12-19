package thesis.core.entities.uav;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.common.Circle;
import thesis.core.entities.TargetMgr;
import thesis.core.entities.sensors.Sensor;
import thesis.core.entities.sensors.SensorGroup;
import thesis.core.entities.sensors.SensorType;
import thesis.core.entities.uav.comms.CommsConfig;
import thesis.core.entities.uav.comms.UAVComms;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.UAVEntityConfig;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;

/**
 * High level manager that maintains all UAVs in the simulation.
 */
public class UAVMgr
{
   private Logger logger;
   private List<UAV> uavs;

   public UAVMgr()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      uavs = new ArrayList<UAV>();
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
   public void reset(EntityTypes entTypes, WorldConfig worldCfg, Random randGen,
         CommsConfig commsCfg, TargetMgr tgtMgr)
   {
      logger.debug("Resetting UAV Manager.");

      uavs.clear();

      int uavID = 0;
      for (UAVEntityConfig uavEntCfg : worldCfg.uavCfgs)
      {
         UAVType type = entTypes.getUAVType(uavEntCfg.getUAVType());
         if (type != null)
         {
            final SensorGroup sensors = new SensorGroup(tgtMgr);
            for(SensorType st : type.getSensors())
            {
               Sensor sensor = sensors.addSensor(st);
               //Align sensor to point straight ahead at startup
               sensor.setAzimuth(uavEntCfg.getOrientation());
            }

            final UAVComms comms = new UAVComms(uavID, this, randGen, commsCfg);

            final Pathing pathing = new Pathing(uavID, type);
            pathing.getCoordinate().setCoordinate(uavEntCfg.getLocation());
            pathing.setHeading(uavEntCfg.getOrientation());

            final UAV uav = new UAV(type.getTypeID(), uavID, sensors, comms, pathing);
            uavs.add(uav);

            ++uavID;
         }
         else
         {
            logger.error("UAV configured with an unknown target type.  Ignoring UAV.");
         }
      }
   }

   /**
    * @return An unmodifiable view of all uavs in the simulation.
    */
   public Collection<UAV> getAllUAVs()
   {
      return Collections.unmodifiableCollection(uavs);
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
      for (UAV uav : uavs)
      {
         uav.stepSimulation();
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

      for (UAV uav : uavs)
      {
         if (Math.abs(uav.getPathing().getCoordinate().distanceTo(region.getCenter())) < region.getRadius())
         {
            inRegion.add(uav);
         }
      }

      return inRegion;
   }
}
