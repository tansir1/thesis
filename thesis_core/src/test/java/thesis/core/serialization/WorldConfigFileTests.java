package thesis.core.serialization;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import thesis.core.world.RoadSegment;
import thesis.core.world.WorldCoordinate;

public class WorldConfigFileTests
{

   @Test
   public void loadCfg()
   {
      InputStream testFile = this.getClass().getResourceAsStream("testCfg.xml");
      final double distance_tolerance = 0.00001;
      
      WorldConfig cfg = WorldConfigFile.loadConfig(testFile);
      assertEquals("Failed to read world height.", 2222.0, cfg.height.asMeters(), distance_tolerance);
      assertEquals("Failed to read world width.", 1234.0, cfg.width.asMeters(), distance_tolerance);
      assertEquals("Failed to read num columns.", 4, cfg.numColums);
      assertEquals("Failed to read num rows.", 3, cfg.numRows);
      assertEquals("Failed to read random seed.", 66758, cfg.randSeed);
      
      RoadSegment rs = cfg.roadSegments.get(0);
      assertEquals("Failed to read first road segment north1.", 34.0, rs.getStart().getNorth(), distance_tolerance);
      assertEquals("Failed to read first road segment north2.", 54.0, rs.getEnd().getNorth(), distance_tolerance);
      assertEquals("Failed to read first road segment east1.", 123.0, rs.getStart().getEast(), distance_tolerance);
      assertEquals("Failed to read first road segment east2.", 76.0, rs.getEnd().getEast(), distance_tolerance);
      
      rs = cfg.roadSegments.get(1);
      assertEquals("Failed to read second road segment north1.", 43.0, rs.getStart().getNorth(), distance_tolerance);
      assertEquals("Failed to read second road segment north2.", 45.0, rs.getEnd().getNorth(), distance_tolerance);
      assertEquals("Failed to read second road segment east1.", 321.0, rs.getStart().getEast(), distance_tolerance);
      assertEquals("Failed to read second road segment east2.", 67.0, rs.getEnd().getEast(), distance_tolerance);
      
      WorldCoordinate havenWC = cfg.havens.get(0);
      assertEquals("Failed to read first haven north.", 78.0, havenWC.getNorth(), distance_tolerance);
      assertEquals("Failed to read first haven east.", 123.0, havenWC.getEast(), distance_tolerance);

      havenWC = cfg.havens.get(1);
      assertEquals("Failed to read second haven north.", 7128.0, havenWC.getNorth(), distance_tolerance);
      assertEquals("Failed to read second haven east.", 5123.0, havenWC.getEast(), distance_tolerance);      
   }
}
