package thesis.core.sensors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;
import thesis.core.TestUtils;
import thesis.core.common.WorldCoordinate;
import thesis.core.targets.TargetMgr;
import thesis.core.world.World;

public class SensorTests
{

   @Test
   public void slewTests()
   {
      World world = new World();

      EntityTypeCfgs entTypeCfgs = TestUtils.genericEntityCfgs(1, 1, 1);
      TargetMgr tgtMgr = TestUtils.genericTgtMgr(world, entTypeCfgs, 1);

      SensorTypeConfigs snsrTypeCfgs = new SensorTypeConfigs();
      snsrTypeCfgs.reset(1);
      snsrTypeCfgs.setSensorData(0, 45, 0, 1000, 10);
      Sensor testMe = new Sensor(0, 0, snsrTypeCfgs, tgtMgr);

      WorldCoordinate lookAt = new WorldCoordinate(-100, -100);

      //Default to origin
      WorldCoordinate sensorPosition = new WorldCoordinate();

      //Compute number of frames needed to slew to look angle
      double bearingTo = sensorPosition.bearingTo(lookAt);
      int numFrames = (int)Math.ceil(bearingTo / snsrTypeCfgs.getMaxSlewFrameRate(0));
      if(numFrames < 0)
      {
         numFrames = -numFrames;
      }
      numFrames++;

      testMe.slewToLookAt(lookAt);

      for(int i=0; i<numFrames; i++)
      {
         testMe.stepSimulation(sensorPosition);
         //System.out.println(testMe.getAzimuth());
      }

      assertEquals("Did not correctly slew sensor to lookAt angle.", bearingTo, testMe.getAzimuth(), 0.00001);
   }
}
