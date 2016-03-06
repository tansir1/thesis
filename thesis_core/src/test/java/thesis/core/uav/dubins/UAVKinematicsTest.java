package thesis.core.uav.dubins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import thesis.core.EntityTypeCfgs;
import thesis.core.SimModel;
import thesis.core.common.WorldPose;
import thesis.core.serialization.world.UAVStartCfg;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.uav.UAV;
import thesis.core.world.RenderOptions;
import thesis.core.world.RenderOptions.RenderOption;
import thesis.core.world.RenderSimState;

public class UAVKinematicsTest
{
   public static void main(String[] args)
   {
      WorldConfig worldCfg = new WorldConfig();
      worldCfg.getWorld().getWorldGIS().reset(10000, 10000, 10, 10);

      EntityTypeCfgs entTypeCfgs = new EntityTypeCfgs();
      entTypeCfgs.getUAVTypeCfgs().setUAVData(0, 10, 250);

      UAVStartCfg startCfg = new UAVStartCfg();
      startCfg.getLocation().setCoordinate(2000, 3000);
      startCfg.setOrientation(180);
      startCfg.setUAVType(0);
      worldCfg.getUAVCfgs().add(startCfg);


      SimModel sim = new SimModel();
      sim.reset(42, worldCfg, entTypeCfgs, 0.0f, 0.0f);

      UAV uav = sim.getUAVManager().getUAV(0);

      WorldPose flyTo = new WorldPose();
      flyTo.getCoordinate().setCoordinate(7000, 6000);
      flyTo.setHeading(-135);
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
         double east = pose.getEast();
         double north = pose.getNorth();
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
