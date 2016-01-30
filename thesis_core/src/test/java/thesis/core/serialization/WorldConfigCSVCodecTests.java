package thesis.core.serialization;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import thesis.core.serialization.world.WorldConfig;

public class WorldConfigCSVCodecTests
{
   @Test
   public void loadEntityCfgs()
   {
      DBConnections dbConns = new DBConnections();
      assertTrue("Failed to open world db.", dbConns.openWorldsDB());

      WorldConfig worldCfg = new WorldConfig();
      WorldConfigCSVCodec testMe = new WorldConfigCSVCodec();


      assertTrue("Failed to load world configurations.",
            testMe.loadCSV(dbConns, new File("./testWorlds/test1"), worldCfg));
      dbConns.closeWorldsDB();
   }
}
