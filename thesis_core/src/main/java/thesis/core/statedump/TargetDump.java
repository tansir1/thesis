package thesis.core.statedump;

import thesis.core.common.WorldPose;
import thesis.core.targets.Target;

@Deprecated
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

   public TargetDump(int id, int type, boolean mobile, WorldPose pose)
   {
      this.id = id;
      this.type = type;
      this.mobile = mobile;
      this.pose = new WorldPose(pose);
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
