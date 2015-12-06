package thesis.core.entities.uav.sensor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.Angle;
import thesis.core.common.WorldCoordinate;
import thesis.core.entities.uav.sensors.Sensor;
import thesis.core.entities.uav.sensors.SensorType;

public class SensorTests
{

   @Test
   public void slewTests()
   {
      SensorType st = new SensorType(1);
      st.setMaxSlewRate(10);
      Sensor testMe = new Sensor(st);

      WorldCoordinate lookAt = new WorldCoordinate(-100, -100);

      //Default to origin
      WorldCoordinate sensorPosition = new WorldCoordinate();

      //Compute number of frames needed to slew to look angle
      Angle bearingTo = sensorPosition.bearingTo(lookAt);
      int numFrames = (int)Math.ceil(bearingTo.asDegrees() / st.getMaxSlewFrameRate());
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

      bearingTo.normalize360();
      assertEquals("Did not correctly slew sensor to lookAt angle.", bearingTo, testMe.getAzimuth());
   }
}
