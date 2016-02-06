package thesis.core.statedump;

import thesis.core.common.WorldPose;
import thesis.core.targets.Target;

public class TargetDump
{
   private int type;
   private int id;
   private WorldPose pose;
   private boolean mobile;

   public TargetDump(Target tgt)
   {
      type = tgt.getType();
      id = tgt.getID();
      pose = new WorldPose();
      mobile = tgt.isMobile();
      dumpUpdate(tgt);
   }

   public void dumpUpdate(Target tgt)
   {
      pose.copy(tgt.getPose());
   }

   public int getType()
   {
      return type;
   }

   public int getId()
   {
      return id;
   }

   public WorldPose getPose()
   {
      return pose;
   }

   public boolean isMobile()
   {
      return mobile;
   }
}
