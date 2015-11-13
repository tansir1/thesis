package thesis.core.entities.uav.dubins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import thesis.core.SimModel;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.entities.uav.UAV;
import thesis.core.entities.uav.UAVType;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.UAVEntityConfig;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.world.RenderOptions;
import thesis.core.world.RenderOptions.RenderOption;
import thesis.core.world.RenderSimState;

public class UAVKinematicsTest
{
   public static void main(String[] args)
   {
      WorldConfig worldCfg = new WorldConfig();
      worldCfg.setNumColumns(10);
      worldCfg.setNumRows(10);
      worldCfg.getWorldHeight().setAsKilometers(100);
      worldCfg.getWorldWidth().setAsKilometers(100);
      
      UAVType uavType = new UAVType(1);
      uavType.getMaxSpd().setAsMetersPerSecond(10);
      uavType.getMaxTurnRt().setAsDegreesPerSecond(18);
      uavType.init();
      
      EntityTypes entTypes = new EntityTypes();
      entTypes.addUAVType(uavType);      
      
      UAVEntityConfig uavEntCfg = new UAVEntityConfig();
      WorldCoordinate.setCoordinateAsMeters(uavEntCfg.getLocation(), 45000, 5000);
      uavEntCfg.getOrientation().setAsDegrees(-90);
      uavEntCfg.setUAVType(uavType.getTypeID());
      worldCfg.uavCfgs.add(uavEntCfg);
      
      SimModel sim = new SimModel();
      sim.reset(42, worldCfg, entTypes);
      
      UAV uav = sim.getUAVManager().getUAV(0);
      
      WorldPose flyTo = new WorldPose();
      WorldCoordinate.setCoordinateAsMeters(flyTo.getCoordinate(), 40000, 64000);
      flyTo.getHeading().setAsDegrees(180);
      uav.TEMP_setDestination(flyTo);

      final int runTimeMS = 10 * 1000;
      for(int simTime = 0; simTime < runTimeMS; simTime += SimModel.SIM_STEP_RATE_MS)
      {
         sim.stepSimulation(SimModel.SIM_STEP_RATE_MS);   
      }
      
      
      for(WorldPose pose : uav.getFlightHistoryTrail())
      {
         double east = pose.getEast().asMeters();
         double north = pose.getNorth().asMeters();
         System.out.format("%.2f,%.2f,%.2f\n", east, north, pose.getHeading().asDegrees());   
      }
      
      
      try
      {
         RenderOptions opts = new RenderOptions();
         opts.setOption(RenderOption.UavHistoryTrail);
         
         BufferedImage buf = RenderSimState.renderToImage(sim, 1280, 720, opts);
         ImageIO.write(buf, "png", new File("UAVKinematicsTest.png"));
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }   
   }
}
