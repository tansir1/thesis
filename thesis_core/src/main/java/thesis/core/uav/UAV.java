package thesis.core.uav;

import java.util.List;

import thesis.core.belief.WorldBelief;
import thesis.core.common.SimTime;
import thesis.core.common.WorldPose;
import thesis.core.sensors.SensorGroup;
import thesis.core.uav.comms.Message;
import thesis.core.uav.comms.UAVComms;
import thesis.core.uav.logic.UAVLogicMgr;
import thesis.core.weapons.WeaponGroup;

public class UAV
{
   // private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV);

   public static int NULL_UAV_ID = -1;

   private int type;

   private int id;

   private UAVComms comms;

   private SensorGroup sensors;
   private WeaponGroup weapons;
   private WorldBelief belief;
   private Pathing pathing;
   private UAVLogicMgr logicMgr;

   public UAV(int type, int id, SensorGroup sensors, WeaponGroup weapons, UAVComms comms, Pathing pathing, UAVLogicMgr logicMgr, WorldBelief wb)
   {
      if (sensors == null)
      {
         throw new NullPointerException("Sensors cannot be null.");
      }

      if (weapons == null)
      {
         throw new NullPointerException("Weapons cannot be null.");
      }

      if (comms == null)
      {
         throw new NullPointerException("Comms cannot be null.");
      }

      if (pathing == null)
      {
         throw new NullPointerException("Pathing cannot be null.");
      }

      if (logicMgr == null)
      {
         throw new NullPointerException("UAV Logic mgr cannot be null.");
      }

      if (wb == null)
      {
         throw new NullPointerException("Worldbelief cannot be null.");
      }

      this.id = id;
      this.type = type;
      this.sensors = sensors;
      this.weapons = weapons;
      this.comms = comms;
      this.pathing = pathing;
      this.logicMgr = logicMgr;
      this.belief = wb;
   }

   public int getID()
   {
      return id;
   }

   public int getType()
   {
      return type;
   }

   /**
    * Step the simulation forward by one frame.
    */
   public void stepSimulation()
   {
      pathing.stepSimulation();

      // Must be invoked before the comms.stepSimulation() call so that
      // broadcast messages are received.
      List<Message> msgs = comms.getAllIncoming();

      comms.stepSimulation(pathing.getCoordinate());
      sensors.stepSimulation(pathing.getCoordinate(), belief, SimTime.getCurrentSimTimeMS());
      logicMgr.stepSimulation(belief, msgs, this, comms);
      belief.stepSimulation(comms);
   }

   public Pathing getPathing()
   {
      return pathing;
   }

   public UAVComms getComms()
   {
      return comms;
   }

   public SensorGroup getSensors()
   {
      return sensors;
   }

   public WeaponGroup getWeapons()
   {
      return weapons;
   }

   public WorldBelief getBelief()
   {
      return belief;
   }

   public UAVLogicMgr getLogic()
   {
      return logicMgr;
   }

   /**
    * This is a temporary method for development testing purposes. It will be
    * deleted once aircraft have a means of selecting their own destinations.
    *
    * @param flyTo
    */
   public void TEMP_setDestination(final WorldPose flyTo)
   {
      pathing.computePathTo(flyTo);
   }

   @Override
   public String toString()
   {
      return Integer.toString(id) + ": " + pathing.getPose().toString();
   }
}
