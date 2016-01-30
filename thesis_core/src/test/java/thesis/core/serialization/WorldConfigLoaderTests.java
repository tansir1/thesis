package thesis.core.serialization;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.serialization.world.UAVStartCfg;
import thesis.core.world.World;

public class WorldConfigLoaderTests
{
   @Test
   public void loadEntityCfgs()
   {
      DBConnections dbConns = new DBConnections();
      assertTrue("Failed to open world db.", dbConns.openWorldsDB());

      World world = new World();
      WorldConfigLoader testMe = new WorldConfigLoader();

      List<UAVStartCfg> uavStartCfgs = new ArrayList<UAVStartCfg>();
      List<TargetStartCfg> tgtStartCfgs = new ArrayList<TargetStartCfg>();

      assertTrue("Failed to load world configurations.",
            testMe.loadConfigs(dbConns, new File("./testWorlds/test1"), world, uavStartCfgs, tgtStartCfgs));
      dbConns.closeWorldsDB();
   }
}
