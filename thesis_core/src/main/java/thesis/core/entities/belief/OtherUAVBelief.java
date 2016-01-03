package thesis.core.entities.belief;

import thesis.core.common.WorldPose;

/**
 * A container for everything a UAV thinks it knows about another UAV teammate
 * out in the world.
 */
public class OtherUAVBelief
{
   private WorldPose pose;
   private float confidence;
   private int uavID;

   public OtherUAVBelief(int id)
   {
      this.uavID = id;
      confidence = 0;
      pose = new WorldPose();
   }

   public int getUavID()
   {
      return uavID;
   }

   public WorldPose getPose()
   {
      return pose;
   }

   /**
    * @return A value [0,1] indicating how confident that this teammate
    *         information is correct.
    */
   public float getConfidence()
   {
      return confidence;
   }

   /**
    * @param confidence
    *           A value [0,1] indicating how confident that this teammate
    *           information is correct.
    */
   public void setConfidence(float confidence)
   {
      this.confidence = confidence;
   }
}
