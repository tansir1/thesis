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
      worldCfg.getWorldHeight().setAsKilometers(10);
      worldCfg.getWorldWidth().setAsKilometers(10);
      
      UAVType uavType = new UAVType(1);
      uavType.getMaxSpd().setAsMetersPerSecond(10);
      uavType.getMinTurnRadius().setAsMeters(250);
      uavType.init();
      
      EntityTypes entTypes = new EntityTypes();
      entTypes.addUAVType(uavType);      
      
      UAVEntityConfig uavEntCfg = new UAVEntityConfig();
      WorldCoordinate.setCoordinateAsMeters(uavEntCfg.getLocation(), 2000, 3000);
      uavEntCfg.getOrientation().setAsDegrees(180);
      uavEntCfg.setUAVType(uavType.getTypeID());
      worldCfg.uavCfgs.add(uavEntCfg);
      
      SimModel sim = new SimModel();
      sim.reset(42, worldCfg, entTypes);
      
      UAV uav = sim.getUAVManager().getUAV(0);
      
      WorldPose flyTo = new WorldPose();
      WorldCoordinate.setCoordinateAsMeters(flyTo.getCoordinate(), 7000, 6000);
      flyTo.getHeading().setAsDegrees(-135);
      uav.TEMP_setDestination(flyTo);

      final int FRAME_LIMIT = 45000;
      for(int frameCount = 0; frameCount < FRAME_LIMIT; ++frameCount)
      {
         sim.stepSimulation();   
      }
      
      /*
      List<WorldPose> history = new ArrayList<WorldPose>();
      uav.getFlightHistoryTrail(history);
      for(WorldPose pose : history)
      {
         double east = pose.getEast().asMeters();
         double north = pose.getNorth().asMeters();
         System.out.format("%.2f,%.2f,%.2f\n", east, north, pose.getHeading().asDegrees());   
      }
      */
      
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
      System.out.println("Test complete.");
   }
}
