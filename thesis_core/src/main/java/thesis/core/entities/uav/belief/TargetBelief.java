package thesis.core.entities.uav.belief;

import thesis.core.common.WorldPose;
import thesis.core.entities.TargetType;

/**
 * A container for everything a UAV thinks it knows about a target out in the
 * world.
 */
public class TargetBelief
{
   private WorldPose pose;
   private TargetType type;
   private float confidence;

   public TargetBelief(TargetType type)
   {
      if (type == null)
      {
         throw new NullPointerException("Target type cannot be null.");
      }
      this.type = type;
      pose = new WorldPose();
      confidence = 0;
   }

   public WorldPose getPose()
   {
      return pose;
   }

   public TargetType getType()
   {
      return type;
   }

   /**
    * @return A value [0,1] indicating how confident that this target
    *         information is correct.
    */
   public float getConfidence()
   {
      return confidence;
   }

   /**
    * @param confidence
    *           A value [0,1] indicating how confident that this target
    *           information is correct.
    */
   public void setConfidence(float confidence)
   {
      this.confidence = confidence;
   }

}
