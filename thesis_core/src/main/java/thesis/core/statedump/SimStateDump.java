package thesis.core.statedump;

import java.util.ArrayList;
import java.util.List;

import thesis.core.SimModel;
import thesis.core.common.WorldPose;
import thesis.core.targets.Target;
import thesis.core.uav.UAV;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;

public class SimStateDump
{
   private World world;
   private List<UAVDump> uavs;
   private List<TargetDump> tgts;

   public SimStateDump()
   {
      uavs = new ArrayList<UAVDump>();
      tgts = new ArrayList<TargetDump>();
      world = new World();
   }

   public void init(SimModel model)
   {
      world.copy(model.getWorld());
   }

   public void update(SimModel model)
   {
      dumpUAVs(model);
      dumpTargets(model);
   }

   public void update(SimStateUpdateDump dump)
   {
      int numUAVs = uavs.size();
      int numTgts = tgts.size();

      for (int i = 0; i < numTgts; ++i)
      {
         WorldPose pose = dump.getTargetPose(tgts.get(i).getId());
         tgts.get(i).getPose().copy(pose);
      }

      for (int i = 0; i < numUAVs; ++i)
      {
         WorldPose pose = dump.getUAVPose(uavs.get(i).getID());
         uavs.get(i).getPose().copy(pose);
         List<SensorDump> snsrs = uavs.get(i).getSensors();
         for(SensorDump sd : snsrs)
         {
            SensorDump snsrUpdate = dump.getSensorUpdate(uavs.get(i).getID(), sd.getID());
            sd.dumpUpdate(snsrUpdate);
         }
      }
   }

   public void fillUpdateDump(SimStateUpdateDump dump)
   {
      int numUAVs = uavs.size();
      int numTgts = tgts.size();

      for (int i = 0; i < numTgts; ++i)
      {
         dump.setTargetPose(tgts.get(i).getId(), tgts.get(i).getPose());
      }

      for (int i = 0; i < numUAVs; ++i)
      {
         dump.setUAVPose(uavs.get(i).getID(), uavs.get(i).getPose());

         List<SensorDump> snsrs = uavs.get(i).getSensors();
         int numSnsrs = snsrs.size();
         for (int j = 0; j < numSnsrs; ++j)
         {
            dump.setSensorUpdate(uavs.get(i).getID(), snsrs.get(j));
         }
      }
   }

   public WorldGIS getWorldGIS()
   {
      return world.getWorldGIS();
   }

   public World getWorld()
   {
      return world;
   }

   public List<UAVDump> getUAVs()
   {
      return uavs;
   }

   public List<TargetDump> getTargets()
   {
      return tgts;
   }

   private void dumpUAVs(SimModel model)
   {
      UAV simUAVs[] = model.getUAVManager().getAllUAVs();
      for (int i = 0; i < simUAVs.length; ++i)
      {
         boolean dumpedData = false;
         int numDumps = uavs.size();
         for (int j = 0; j < numDumps; ++j)
         {
            if (uavs.get(j).getID() == simUAVs[i].getID())
            {
               dumpedData = true;
               uavs.get(j).dumpUpdate(simUAVs[i]);
               break;
            }
         }

         if (!dumpedData)
         {
            UAVDump dump = new UAVDump(simUAVs[i]);
            uavs.add(dump);
         }
      }
   }

   private void dumpTargets(SimModel model)
   {
      Target simTgts[] = model.getTargetManager().getAllTargets();
      for (int i = 0; i < simTgts.length; ++i)
      {
         boolean dumpedData = false;
         int numDumps = tgts.size();
         for (int j = 0; j < numDumps; ++j)
         {
            if (tgts.get(j).getId() == simTgts[i].getID())
            {
               dumpedData = true;
               tgts.get(j).dumpUpdate(simTgts[i]);
               break;
            }
         }

         if (!dumpedData)
         {
            TargetDump dump = new TargetDump(simTgts[i]);
            tgts.add(dump);
         }
      }
   }
}
