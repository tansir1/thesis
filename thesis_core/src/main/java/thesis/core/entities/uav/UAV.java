package thesis.core.entities.uav;

import thesis.core.common.WorldPose;
import thesis.core.entities.belief.BeliefState;
import thesis.core.entities.sensors.SensorGroup;
import thesis.core.entities.uav.comms.UAVComms;
import thesis.core.entities.uav.logic.UAVLogicMgr;

public class UAV
{
   //private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV);

   private int type;

   private int id;

   private UAVComms comms;

   private SensorGroup sensors;
   private BeliefState belief;
   private Pathing pathing;
   private UAVLogicMgr logicMgr;

   public UAV(int type, int id, SensorGroup sensors, UAVComms comms, Pathing pathing, UAVLogicMgr logicMgr)
   {
      if (sensors == null)
      {
         throw new NullPointerException("Sensors cannot be null.");
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

      this.id = id;
      this.type = type;
      this.sensors = sensors;
      this.comms = comms;
      this.pathing = pathing;
      this.logicMgr = logicMgr;

      belief = new BeliefState();
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

      comms.stepSimulation(pathing.getCoordinate());
      logicMgr.stepSimulation(sensors.stepSimulation(pathing.getCoordinate()));
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

   public BeliefState getBelief()
   {
      return belief;
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
