package thesis.core.serialization;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

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
   }
}
