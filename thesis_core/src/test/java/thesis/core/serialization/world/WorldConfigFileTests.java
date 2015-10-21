package thesis.core.serialization.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import thesis.core.TestUtils;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.Graph;
import thesis.core.common.graph.Vertex;

public class WorldConfigFileTests
{
   private Graph<WorldCoordinate> createRoadNet()
   {
      Graph<WorldCoordinate> roadNet = new Graph<WorldCoordinate>();
      Vertex<WorldCoordinate> vert1 = roadNet.createVertex(TestUtils.randWorldCoord());
      Vertex<WorldCoordinate> vert2 = roadNet.createVertex(TestUtils.randWorldCoord());
      Vertex<WorldCoordinate> vert3 = roadNet.createVertex(TestUtils.randWorldCoord());

      roadNet.createBidirectionalEdge(vert1, vert2, 1);
      roadNet.createBidirectionalEdge(vert2, vert3, 2);
      roadNet.createBidirectionalEdge(vert3, vert1, 3);

      return roadNet;
   }

   @Test
   public void serializeTest() throws FileNotFoundException
   {
      //Generate and write the test data
      WorldConfig testMe = new WorldConfig();
      testMe.getWorldHeight().setAsKilometers(1234.01234);
      testMe.getWorldWidth().setAsKilometers(654.432);
      testMe.setNumColumns(4);
      testMe.setNumRows(34);
      testMe.setRoadNetwork(createRoadNet());

      WorldCoordinate wc = new WorldCoordinate();
      wc.setCoordinate(TestUtils.randWorldCoord());
      testMe.getHavens().add(wc);
      wc = new WorldCoordinate();
      wc.setCoordinate(TestUtils.randWorldCoord());
      testMe.getHavens().add(wc);

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
      //FileOutputStream outBuff = new FileOutputStream("worldCfgTest.xml");
      assertTrue("Failed to write to output stream.", WorldConfigFile.saveConfig(outBuff, testMe));

      //Read in the data buffer and parse it
      ByteArrayInputStream inBuff = new ByteArrayInputStream(outBuff.toByteArray());
      //FileInputStream inBuff = new FileInputStream("worldCfgTest.xml");
      WorldConfig results = WorldConfigFile.loadConfig(inBuff);

      assertEquals("Failed to read world height.", results.getWorldHeight(), testMe.getWorldHeight());
      assertEquals("Failed to read world width.", results.getWorldWidth(), testMe.getWorldWidth());
      assertEquals("Failed to read num columns.", results.getNumColumns(), testMe.getNumColumns());
      assertEquals("Failed to read num rows.", results.getNumRows(), testMe.getNumRows());

      assertEquals("Failed to read road network.", testMe.getRoadNetwork(), results.getRoadNetwork());
      /*assertEquals("Number of road vertices does not match.", testMe.getRoadNetwork().getNumVertices(), results.getRoadNetwork().getNumVertices());
      assertEquals("Number of road edges does not match.", testMe.getRoadNetwork().getNumEdges(), results.getRoadNetwork().getNumEdges());

      final int numVerts = testMe.getRoadNetwork().getNumVertices();
      for(int i=0; i<numVerts; ++i)
      {
         Vertex<WorldCoordinate> vert1 = testMe.getRoadNetwork().getVertexByID(i);
         Vertex<WorldCoordinate> vert2 = results.getRoadNetwork().getVertexByID(i);

         assertEquals("Number of incoming edges in vertex " + Integer.toString(i) + " do not match.", vert1.getIncomingEdges().size(), vert2.getIncomingEdges().size());
         assertEquals("Number of outgoing edges in vertex " + Integer.toString(i) + " do not match.", vert1.getOutgoingEdges().size(), vert2.getOutgoingEdges().size());

         assertEquals("Failed to read vertex " + Integer.toString(i), vert1, vert2);

         vert1.getIncomingEdges().size();
         for(int j=0; j)
      }*/

/*
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
*/
      WorldCoordinate havenWCT = testMe.getHavens().get(0);
      WorldCoordinate havenWCR = results.getHavens().get(0);
      assertEquals("Failed to read first haven north.", havenWCR.getNorth(), havenWCT.getNorth());
      assertEquals("Failed to read first haven east.", havenWCR.getEast(), havenWCT.getEast());

      havenWCT = testMe.getHavens().get(1);
      havenWCR = results.getHavens().get(1);
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

