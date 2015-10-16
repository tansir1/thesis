package thesis.core.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import thesis.core.world.RoadSegment;
import thesis.core.world.WorldCoordinate;

public class WorldConfigFileTests
{

   @Test
   public void serializeTest()
   {
      //Generate and write the test data
      WorldConfig testMe = new WorldConfig();
      testMe.height.setAsKilometers(1234.01234);
      testMe.width.setAsKilometers(654.432);
      testMe.numColums = 4;
      testMe.numRows = 34;
      
      testMe.randSeed = 546742;
      
      RoadSegment rs = new RoadSegment();
      rs.getStart().setCoordinate(34.123, 765.345);
      rs.getEnd().setCoordinate(5673.21, 823945.123);
      testMe.roadSegments.add(rs);
      rs = new RoadSegment();
      rs.getStart().setCoordinate(6785.456, 940.2134345);
      rs.getEnd().setCoordinate(8963.21, 9426.5643);
      testMe.roadSegments.add(rs);
      
      WorldCoordinate wc = new WorldCoordinate();
      wc.setCoordinate(78.652, 618.13);
      testMe.havens.add(wc);
      wc = new WorldCoordinate();
      wc.setCoordinate(7128.652, 2034.13);
      testMe.havens.add(wc);
      
      TargetConfig tarCfg = new TargetConfig();
      tarCfg.getLocation().setCoordinate(8310.15, 4185.325);
      tarCfg.getOrientation().setAsDegrees(34.547);
      tarCfg.setTargetType(76);
      testMe.targetCfgs.add(tarCfg);
      tarCfg = new TargetConfig();
      tarCfg.getLocation().setCoordinate(3059.153, 1035.325);
      tarCfg.getOrientation().setAsDegrees(69.547);
      tarCfg.setTargetType(2);
      testMe.targetCfgs.add(tarCfg);
      
      
      UAVConfig uavCfg = new UAVConfig();
      uavCfg.getLocation().setCoordinate(4308.15, 96703.325);
      uavCfg.getOrientation().setAsDegrees(63.1547);
      uavCfg.setUAVType(3);
      testMe.uavCfgs.add(uavCfg);
      uavCfg = new UAVConfig();
      uavCfg.getLocation().setCoordinate(10354.153, 4358.3525);
      uavCfg.getOrientation().setAsDegrees(206.852);
      uavCfg.setUAVType(22);
      testMe.uavCfgs.add(uavCfg);
      
      //Write the data to a byte buffer
      ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
      assertTrue("Failed to write to output stream.", WorldConfigFile.saveConfig(outBuff, testMe));
      
      //Read in the data buffer and parse it
      ByteArrayInputStream inBuff = new ByteArrayInputStream(outBuff.toByteArray());
      //inBuff.reset();
      WorldConfig results = WorldConfigFile.loadConfig(inBuff);
      
      final double distance_tolerance = 0.00001;
      
      assertEquals("Failed to read world height.", results.height.asMeters(), testMe.height.asMeters(), distance_tolerance);
      assertEquals("Failed to read world width.", results.width.asMeters(), testMe.width.asMeters(), distance_tolerance);
      assertEquals("Failed to read num columns.", results.numColums, testMe.numColums);
      assertEquals("Failed to read num rows.", results.numRows, testMe.numRows);
      assertEquals("Failed to read random seed.", results.randSeed, testMe.randSeed);
      
      RoadSegment rsT = testMe.roadSegments.get(0);
      RoadSegment rsR = results.roadSegments.get(0);
      assertEquals("Failed to read first road segment north1.", rsR.getStart().getNorth(), rsT.getStart().getNorth(), distance_tolerance);
      assertEquals("Failed to read first road segment north2.", rsR.getEnd().getNorth(), rsT.getEnd().getNorth(), distance_tolerance);
      assertEquals("Failed to read first road segment east1.", rsR.getStart().getEast(), rsT.getStart().getEast(), distance_tolerance);
      assertEquals("Failed to read first road segment east2.", rsR.getEnd().getEast(), rsT.getEnd().getEast(), distance_tolerance);
      
      rsT = testMe.roadSegments.get(1);
      rsR = results.roadSegments.get(1);
      assertEquals("Failed to read second road segment north1.", rsR.getStart().getNorth(), rsT.getStart().getNorth(), distance_tolerance);
      assertEquals("Failed to read second road segment north2.", rsR.getEnd().getNorth(), rsT.getEnd().getNorth(), distance_tolerance);
      assertEquals("Failed to read second road segment east1.", rsR.getStart().getEast(), rsT.getStart().getEast(), distance_tolerance);
      assertEquals("Failed to read second road segment east2.", rsR.getEnd().getEast(), rsT.getEnd().getEast(), distance_tolerance);
      
      WorldCoordinate havenWCT = testMe.havens.get(0);
      WorldCoordinate havenWCR = results.havens.get(0);
      assertEquals("Failed to read first haven north.", havenWCR.getNorth(), havenWCT.getNorth(), distance_tolerance);
      assertEquals("Failed to read first haven east.", havenWCR.getEast(), havenWCT.getEast(), distance_tolerance);

      havenWCT = testMe.havens.get(1);
      havenWCR = results.havens.get(1);
      assertEquals("Failed to read second haven north.", havenWCR.getNorth(), havenWCT.getNorth(), distance_tolerance);
      assertEquals("Failed to read second haven east.", havenWCR.getEast(), havenWCT.getEast(), distance_tolerance);
      
      TargetConfig tarCfgT = testMe.targetCfgs.get(0);
      TargetConfig tarCfgR = results.targetCfgs.get(0);
      assertEquals("Failed to read first target's north.", tarCfgR.getLocation().getNorth(), tarCfgT.getLocation().getNorth(), distance_tolerance);
      assertEquals("Failed to read first target's east.", tarCfgR.getLocation().getEast(), tarCfgT.getLocation().getEast(), distance_tolerance);
      assertEquals("Failed to read first target's type.", tarCfgR.getTargetType(), tarCfgT.getTargetType());
      assertEquals("Failed to read first target's orientation.", tarCfgR.getOrientation().asDegrees(), tarCfgT.getOrientation().asDegrees(), distance_tolerance);
      
      tarCfgT = testMe.targetCfgs.get(1);
      tarCfgR = results.targetCfgs.get(1);
      assertEquals("Failed to read second target's north.", tarCfgR.getLocation().getNorth(), tarCfgT.getLocation().getNorth(), distance_tolerance);
      assertEquals("Failed to read second target's east.", tarCfgR.getLocation().getEast(), tarCfgT.getLocation().getEast(), distance_tolerance);
      assertEquals("Failed to read second target's type.", tarCfgR.getTargetType(), tarCfgT.getTargetType());
      assertEquals("Failed to read second target's orientation.", tarCfgR.getOrientation().asDegrees(), tarCfgT.getOrientation().asDegrees(), distance_tolerance);
    
      
      UAVConfig uavCfgT = testMe.uavCfgs.get(0);
      UAVConfig uavCfgR = results.uavCfgs.get(0);
      assertEquals("Failed to read first uav's north.", uavCfgR.getLocation().getNorth(), uavCfgT.getLocation().getNorth(), distance_tolerance);
      assertEquals("Failed to read first uav's east.", uavCfgR.getLocation().getEast(), uavCfgT.getLocation().getEast(), distance_tolerance);
      assertEquals("Failed to read first uav's type.", uavCfgR.getUAVType(), uavCfgT.getUAVType());
      assertEquals("Failed to read first uav's orientation.", uavCfgR.getOrientation().asDegrees(), uavCfgT.getOrientation().asDegrees(), distance_tolerance);
      
      uavCfgT = testMe.uavCfgs.get(1);
      uavCfgR = results.uavCfgs.get(1);
      assertEquals("Failed to read second uav's north.", uavCfgR.getLocation().getNorth(), uavCfgT.getLocation().getNorth(), distance_tolerance);
      assertEquals("Failed to read second uav's east.", uavCfgR.getLocation().getEast(), uavCfgT.getLocation().getEast(), distance_tolerance);
      assertEquals("Failed to read second uav's type.", uavCfgR.getUAVType(), uavCfgT.getUAVType());
      assertEquals("Failed to read second uav's orientation.", uavCfgR.getOrientation().asDegrees(), uavCfgT.getOrientation().asDegrees(), distance_tolerance);
   }
}
