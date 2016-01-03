package thesis.core.entities.sensor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.WorldCoordinate;
import thesis.core.entities.TargetMgr;
import thesis.core.entities.sensors.Sensor;
import thesis.core.entities.sensors.SensorType;

public class SensorTests
{

   @Test
   public void slewTests()
   {
      SensorType st = new SensorType(1);
      st.setMaxSlewRate(10);
      Sensor testMe = new Sensor(st, new TargetMgr());

      WorldCoordinate lookAt = new WorldCoordinate(-100, -100);

      //Default to origin
      WorldCoordinate sensorPosition = new WorldCoordinate();

      //Compute number of frames needed to slew to look angle
      double bearingTo = sensorPosition.bearingTo(lookAt);
      int numFrames = (int)Math.ceil(bearingTo / st.getMaxSlewFrameRate());
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
