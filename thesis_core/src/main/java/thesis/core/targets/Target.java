package thesis.core.targets;

import java.util.List;

import thesis.core.SimModel;
import thesis.core.common.HavenRouting;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class Target
{
   private final int type;
   private final WorldPose pose;

   private WorldCoordinate destination;
   private HavenRouting havenRouting;
   private List<WorldCoordinate> havenPath;

   /**
    * Meters/second
    */
   private final float maxSpd;

   public Target(int tgtType, float tgtSpd, HavenRouting havenRouting)
   {
      this.type = tgtType;
      this.maxSpd = tgtSpd;
      this.havenRouting = havenRouting;

      pose = new WorldPose();
      destination = new WorldCoordinate();

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

   public float getHeading()
   {
      return pose.getHeading();
   }

   public void setHeading(float hdg)
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

            float newHdg = pose.getCoordinate().bearingTo(havenPath.get(0));
            pose.setHeading(newHdg);
         }

         double deltaSeconds = SimTime.SIM_STEP_RATE_MS / 1000.0;

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
      if (!havenPath.isEmpty())
      {
         havenPath.remove(0);
      }
      else
      {
         havenRouting.selectNewHavenDestination(pose.getCoordinate(), destination, havenPath);
      }
   }
}
