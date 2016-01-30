package thesis.core.entities.belief;

import thesis.core.common.Angle;
import thesis.core.common.WorldPose;

/**
 * A container for everything a UAV thinks it knows about a target out in the
 * world.
 */
public class TargetBelief
{
   private final WorldPose pose;
   private final int type;
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
      //Weight the confidences in order to merge belief values
      final double totalConf = confidence + other.confidence;
      final double mergePercent = other.confidence / totalConf;

      double deltaNorth = pose.getCoordinate().getNorth() - other.pose.getNorth();
      double deltaEast = pose.getCoordinate().getEast() - other.pose.getEast();

      deltaNorth *= mergePercent;
      deltaEast *= mergePercent;
      pose.getCoordinate().translateCart(-deltaNorth, -deltaEast);

      double deltaHdg = pose.getHeading() - other.getPose().getHeading();
      //deltaHdg = Angle.normalize360(deltaHdg);
      deltaHdg *= mergePercent;
      final double newHdg = pose.getHeading() - deltaHdg;
      pose.setHeading(Angle.normalize360((float)newHdg));
   }
}
