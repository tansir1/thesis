package thesis.core.serialization;

import thesis.core.common.WorldPose;

public class TargetEntitiesCfg
{
   private WorldPose poses[];
   private int tgtTypes[];
   private int numTgts;

   public TargetEntitiesCfg()
   {
      numTgts = -1;// Default error condition
   }

   public void reset(int numTgts)
   {
      this.numTgts = numTgts;
      poses = new WorldPose[numTgts];
      tgtTypes = new int[numTgts];

      for(int i=0; i<numTgts; ++i)
      {
         poses[i] = new WorldPose();
      }
   }

   public int getNumTargets()
   {
      return numTgts;
   }

   public WorldPose getTargetPose(int tgtIdx)
   {
      return poses[tgtIdx];
   }

   public int getTargetType(int tgtIdx)
   {
      return tgtTypes[tgtIdx];
   }

   /**
    * @param tgtIdx
    * @param tgtType
    * @param pose Value is copied internally.
    */
   public void setTargetData(int tgtIdx, int tgtType, WorldPose pose)
   {
      poses[tgtIdx].copy(pose);
      tgtTypes[tgtIdx] = tgtType;
   }
}
