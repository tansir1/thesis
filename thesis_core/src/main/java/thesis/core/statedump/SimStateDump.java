package thesis.core.statedump;

import java.util.ArrayList;
import java.util.List;

import thesis.core.SimModel;
import thesis.core.targets.Target;
import thesis.core.uav.UAV;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;
import thesis.network.messages.InfrastructureMsg;

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

   public void getUpdateMsgs(List<InfrastructureMsg> msgs)
   {

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
      for(int i=0; i<simUAVs.length; ++i)
      {
         boolean dumpedData = false;
         int numDumps = uavs.size();
         for(int j=0; j<numDumps; ++j)
         {
            if(uavs.get(j).getID() == simUAVs[i].getID())
            {
               dumpedData = true;
               uavs.get(j).dumpUpdate(simUAVs[i]);
               break;
            }
         }

         if(!dumpedData)
         {
            UAVDump dump = new UAVDump(simUAVs[i]);
            uavs.add(dump);
         }
      }
   }

   private void dumpTargets(SimModel model)
   {
      Target simTgts[] = model.getTargetManager().getAllTargets();
      for(int i=0; i<simTgts.length; ++i)
      {
         boolean dumpedData = false;
         int numDumps = tgts.size();
         for(int j=0; j<numDumps; ++j)
         {
            if(tgts.get(j).getId() == simTgts[i].getID())
            {
               dumpedData = true;
               tgts.get(j).dumpUpdate(simTgts[i]);
               break;
            }
         }

         if(!dumpedData)
         {
            TargetDump dump = new TargetDump(simTgts[i]);
            tgts.add(dump);
         }
      }
   }
}
