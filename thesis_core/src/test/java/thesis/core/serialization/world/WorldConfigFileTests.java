package thesis.core.serialization.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import thesis.core.TestUtils;
import thesis.core.common.WorldCoordinate;
import thesis.core.serialization.world.TargetEntityConfig;
import thesis.core.serialization.world.UAVEntityConfig;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.serialization.world.WorldConfigFile;
import thesis.core.world.RoadSegment;

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
      rs.getStart().setCoordinate(TestUtils.randWorldCoord());
      rs.getEnd().setCoordinate(TestUtils.randWorldCoord());
      testMe.roadSegments.add(rs);
      rs = new RoadSegment();
      rs.getStart().setCoordinate(TestUtils.randWorldCoord());
      rs.getEnd().setCoordinate(TestUtils.randWorldCoord());
      testMe.roadSegments.add(rs);
      
      WorldCoordinate wc = new WorldCoordinate();
      wc.setCoordinate(TestUtils.randWorldCoord());
      testMe.havens.add(wc);
      wc = new WorldCoordinate();
      wc.setCoordinate(TestUtils.randWorldCoord());
      testMe.havens.add(wc);
      
      TargetEntityConfig tarCfg = new TargetEntityConfig();
      tarCfg.getLocation().setCoordinate(TestUtils.randWorldCoord());
      tarCfg.getOrientation().setAsDegrees(34.547);
      tarCfg.setTargetType(76);
      testMe.targetCfgs.add(tarCfg);
      tarCfg = new TargetEntityConfig();
      tarCfg.getLocation().setCoordinate(TestUtils.randWorldCoord());
      tarCfg.getOrientation().setAsDegrees(69.547);
      tarCfg.setTargetType(2);
      testMe.targetCfgs.add(tarCfg);
      
      
      UAVEntityConfig uavCfg = new UAVEntityConfig();
      uavCfg.getLocation().setCoordinate(TestUtils.randWorldCoord());
      uavCfg.getOrientation().setAsDegrees(63.1547);
      uavCfg.setUAVType(3);
      testMe.uavCfgs.add(uavCfg);
      uavCfg = new UAVEntityConfig();
      uavCfg.getLocation().setCoordinate(TestUtils.randWorldCoord());
      uavCfg.getOrientation().setAsDegrees(206.852);
      uavCfg.setUAVType(22);
      testMe.uavCfgs.add(uavCfg);
      
      //Write the data to a byte buffer
      ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
      assertTrue("Failed to write to output stream.", WorldConfigFile.saveConfig(outBuff, testMe));
      
      //Read in the data buffer and parse it
      ByteArrayInputStream inBuff = new ByteArrayInputStream(outBuff.toByteArray());
      WorldConfig results = WorldConfigFile.loadConfig(inBuff);
      
      assertEquals("Failed to read world height.", results.height, testMe.height);
      assertEquals("Failed to read world width.", results.width, testMe.width);
      assertEquals("Failed to read num columns.", results.numColums, testMe.numColums);
      assertEquals("Failed to read num rows.", results.numRows, testMe.numRows);
      assertEquals("Failed to read random seed.", results.randSeed, testMe.randSeed);
      
      RoadSegment rsT = testMe.roadSegments.get(0);
      RoadSegment rsR = results.roadSegments.get(0);
      assertEquals("Failed to read first road segment north1.", rsR.getStart().getNorth(), rsT.getStart().getNorth());
      assertEquals("Failed to read first road segment north2.", rsR.getEnd().getNorth(), rsT.getEnd().getNorth());
      assertEquals("Failed to read first road segment east1.", rsR.getStart().getEast(), rsT.getStart().getEast());
      assertEquals("Failed to read first road segment east2.", rsR.getEnd().getEast(), rsT.getEnd().getEast());
      
      rsT = testMe.roadSegments.get(1);
      rsR = results.roadSegments.get(1);
      assertEquals("Failed to read second road segment north1.", rsR.getStart().getNorth(), rsT.getStart().getNorth());
      assertEquals("Failed to read second road segment north2.", rsR.getEnd().getNorth(), rsT.getEnd().getNorth());
      assertEquals("Failed to read second road segment east1.", rsR.getStart().getEast(), rsT.getStart().getEast());
      assertEquals("Failed to read second road segment east2.", rsR.getEnd().getEast(), rsT.getEnd().getEast());
      
      WorldCoordinate havenWCT = testMe.havens.get(0);
      WorldCoordinate havenWCR = results.havens.get(0);
      assertEquals("Failed to read first haven north.", havenWCR.getNorth(), havenWCT.getNorth());
      assertEquals("Failed to read first haven east.", havenWCR.getEast(), havenWCT.getEast());

      havenWCT = testMe.havens.get(1);
      havenWCR = results.havens.get(1);
      assertEquals("Failed to read second haven north.", havenWCR.getNorth(), havenWCT.getNorth());
      assertEquals("Failed to read second haven east.", havenWCR.getEast(), havenWCT.getEast());
      
      TargetEntityConfig tarCfgT = testMe.targetCfgs.get(0);
      TargetEntityConfig tarCfgR = results.targetCfgs.get(0);
      assertEquals("Failed to read first target's north.", tarCfgR.getLocation().getNorth(), tarCfgT.getLocation().getNorth());
      assertEquals("Failed to read first target's east.", tarCfgR.getLocation().getEast(), tarCfgT.getLocation().getEast());
      assertEquals("Failed to read first target's type.", tarCfgR.getTargetType(), tarCfgT.getTargetType());
      assertEquals("Failed to read first target's orientation.", tarCfgR.getOrientation(), tarCfgT.getOrientation());
      
      tarCfgT = testMe.targetCfgs.get(1);
      tarCfgR = results.targetCfgs.get(1);
      assertEquals("Failed to read second target's north.", tarCfgR.getLocation().getNorth(), tarCfgT.getLocation().getNorth());
      assertEquals("Failed to read second target's east.", tarCfgR.getLocation().getEast(), tarCfgT.getLocation().getEast());
      assertEquals("Failed to read second target's type.", tarCfgR.getTargetType(), tarCfgT.getTargetType());
      assertEquals("Failed to read second target's orientation.", tarCfgR.getOrientation(), tarCfgT.getOrientation());
    
      
      UAVEntityConfig uavCfgT = testMe.uavCfgs.get(0);
      UAVEntityConfig uavCfgR = results.uavCfgs.get(0);
      assertEquals("Failed to read first uav's north.", uavCfgR.getLocation().getNorth(), uavCfgT.getLocation().getNorth());
      assertEquals("Failed to read first uav's east.", uavCfgR.getLocation().getEast(), uavCfgT.getLocation().getEast());
      assertEquals("Failed to read first uav's type.", uavCfgR.getUAVType(), uavCfgT.getUAVType());
      assertEquals("Failed to read first uav's orientation.", uavCfgR.getOrientation(), uavCfgT.getOrientation());
      
      uavCfgT = testMe.uavCfgs.get(1);
      uavCfgR = results.uavCfgs.get(1);
      assertEquals("Failed to read second uav's north.", uavCfgR.getLocation().getNorth(), uavCfgT.getLocation().getNorth());
      assertEquals("Failed to read second uav's east.", uavCfgR.getLocation().getEast(), uavCfgT.getLocation().getEast());
      assertEquals("Failed to read second uav's type.", uavCfgR.getUAVType(), uavCfgT.getUAVType());
      assertEquals("Failed to read second uav's orientation.", uavCfgR.getOrientation(), uavCfgT.getOrientation());
   }
}
