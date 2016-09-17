package thesis.core.targets;

import java.util.ArrayList;
import java.util.List;

import thesis.core.SimModel;
import thesis.core.common.HavenRouting;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class Target
{
   public static final int INVALID_TGT_ID = -1;

   private final int id;
   private final int type;
   private final WorldPose pose;

   private WorldCoordinate destination;
   private HavenRouting havenRouting;
   private List<WorldCoordinate> havenPath;
   private boolean alive;

   /**
    * Meters/second
    */
   private final double maxSpd;

   public Target(int tgtType, int id, double tgtSpd, HavenRouting havenRouting)
   {
      this.id = id;
      this.type = tgtType;
      this.maxSpd = tgtSpd;
      this.havenRouting = havenRouting;
      
      havenPath = new ArrayList<WorldCoordinate>();
      pose = new WorldPose();
      destination = new WorldCoordinate();
      alive = true;
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
    * Check if this type of target is mobile or statically fixed. The check is
    * performed by verifying that the max speed of the target is greater than
    * zero.
    *
    * @return True if the target is capable of movement, false otherwise.
    * @see TargetType#getMaxSpeed()
    */
   public boolean isMobile()
   {
      return maxSpd > 0;
   }

   public WorldCoordinate getCoordinate()
   {
      return pose.getCoordinate();
   }

   public double getHeading()
   {
      return pose.getHeading();
   }

   public void setHeading(double hdg)
   {
      pose.setHeading(hdg);
   }

   public WorldPose getPose()
   {
      return pose;
   }

   /**
    * Step the simulation forward by {@link SimModel#SIM_STEP_RATE_MS} amount of
    * time.
    */
   public void stepSimulation()
   {
      // TODO Add a time delay for mobile targets to sit inside of a haven

      if (isMobile())
      {
         if (isAtDestination())
         {
            selectNewDestination();
            double newHdg = pose.getCoordinate().bearingTo(havenPath.get(0));
            pose.setHeading(newHdg);
         }

         double deltaSeconds = SimTime.SIM_STEP_RATE_S;

         // east distance = time * speed * east component
         double easting = deltaSeconds * maxSpd * Math.cos(Math.toRadians(pose.getHeading()));
         // north distance = time * speed * north component
         double northing = deltaSeconds * maxSpd * Math.sin(Math.toRadians(pose.getHeading()));

         pose.getCoordinate().translateCart(northing, easting);
      }

   }

   private boolean isAtDestination()
   {
      boolean arrived = false;

      if (havenPath.isEmpty())
      {
         arrived = true;
      }
      else if (pose.getCoordinate().distanceTo(havenPath.get(0)) < maxSpd)
      {
         // If we're within one frame of the destination
         arrived = true;
      }

      return arrived;
   }

   private void selectNewDestination()
   {
      if (havenPath.size() > 1)
      {
         havenPath.remove(0);
      }
      else if((havenPath.size() == 1 || havenPath.isEmpty()) && isAtDestination())
      {
         havenPath.clear();
         havenRouting.selectNewHavenDestination(pose.getCoordinate(), destination, havenPath);
      }
   }

   public boolean isAlive()
   {
      return alive;
   }

   public void attacked()
   {
      alive = false;
   }
}
