package thesis.core.statedump;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.WorldPose;
import thesis.core.sensors.Sensor;
import thesis.core.uav.UAV;

public class UAVDump
{
   private int type;
   private int id;
   private WorldPose pose;
   private List<SensorDump> snsrs;
   private List<WorldPose> pathTrail;

   public UAVDump(UAV uav)
   {
      type = uav.getType();
      id = uav.getID();
      pose = new WorldPose();
      snsrs = new ArrayList<SensorDump>();
      pathTrail = new ArrayList<WorldPose>();

      dumpUpdate(uav);
   }

   public UAVDump(int type, int id, WorldPose pose)
   {
      this.type = type;
      this.id = id;
      this.pose = new WorldPose(pose);
      snsrs = new ArrayList<SensorDump>();
      pathTrail = new ArrayList<WorldPose>();
   }

   public void dumpUpdate(UAV uav)
   {
      pose.copy(uav.getPathing().getPose());

      List<Sensor> simSensors = uav.getSensors().getSensors();
      int numSnsrs = simSensors.size();
      for (int i = 0; i < numSnsrs; ++i)
      {
         boolean dumpedData = false;
         int numDumps = snsrs.size();
         for (int j = 0; j < numDumps; ++j)
         {
            if (snsrs.get(j).getID() == simSensors.get(i).getID())
            {
               dumpedData = true;
               snsrs.get(j).dumpUpdate(simSensors.get(i));
               break;
            }
         }

         if (!dumpedData)
         {
            SensorDump dump = new SensorDump(simSensors.get(i));
            snsrs.add(dump);
         }
      }

      pathTrail.clear();
      uav.getPathing().getFlightHistoryTrail(pathTrail);
   }

   public List<WorldPose> getFlightPathHistory()
   {
      return pathTrail;
   }

   public List<SensorDump> getSensors()
   {
      return snsrs;
   }

   public int getType()
   {
      return type;
   }

   public int getID()
   {
      return id;
   }

   public WorldPose getPose()
   {
      return pose;
   }

}
