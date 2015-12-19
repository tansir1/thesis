package thesis.core.entities.uav.belief;

import thesis.core.common.WorldPose;

/**
 * A container for everything a UAV thinks it knows about a target out in the
 * world.
 */
public class TargetBelief
{
   private WorldPose pose;
   private int type;
   private float confidence;

   public TargetBelief(int type)
   {
      this.type = type;
      pose = new WorldPose();
      confidence = 0;
   }

   public WorldPose getPose()
   {
      return pose;
   }

   public int getType()
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

   public void merge(TargetBelief other)
   {
      //Weight the confidences in order to merge belief values using an alpha filter
      double totalConf = confidence + other.confidence;
      //double otherConfWeight = other.confidence / totalConf;
      double myConfWeight = confidence / totalConf;

      double deltaNorth = pose.getCoordinate().getNorth() - other.pose.getNorth();
      /*double move = deltaNorth *
      pose.getCoordinate()*/
   }
}
