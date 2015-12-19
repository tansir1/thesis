package thesis.core.entities.uav;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.WorldPose;
import thesis.core.entities.Target;
import thesis.core.entities.sensors.SensorGroup;
import thesis.core.entities.uav.belief.BeliefState;
import thesis.core.entities.uav.belief.TargetBelief;
import thesis.core.entities.uav.comms.UAVComms;
import thesis.core.utilities.LoggerIDs;

public class UAV
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV);

   private int type;

   private int id;

   private UAVComms comms;

   private SensorGroup sensors;
   private BeliefState belief;
   private Pathing pathing;

   public UAV(int type, int id, SensorGroup sensors, UAVComms comms, Pathing pathing)
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

      this.id = id;
      this.type = type;
      this.sensors = sensors;
      this.comms = comms;
      this.pathing = pathing;
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
      List<Target> tgtsInFOV = sensors.stepSimulation(pathing.getCoordinate());
      scanForTargets(tgtsInFOV);
   }

   private List<TargetBelief> scanForTargets(List<Target> tgtsInFOV)
   {
      List<TargetBelief> detections = new ArrayList<TargetBelief>();
      for(Target tgt : tgtsInFOV)
      {
         //TODO Need to add probabilities of detection.
         //For now 100% detection to test sensor update logic and beliefs
         TargetBelief tb = new TargetBelief(tgt.getType());
         tb.getPose().copy(tgt.getPose());
         detections.add(tb);
      }
      return detections;
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
