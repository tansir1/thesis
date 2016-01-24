package thesis.core.serialization;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import thesis.core.EntityCfgs;

public class ConfigLoaderTests
{

   @Test
   public void loadEntityCfgs()
   {
      DBConnections dbConns = new DBConnections();
      assertTrue("Failed to open configuration db.", dbConns.openConfigDB());

      EntityCfgs entCfgs = new EntityCfgs();

      ConfigLoader testMe = new ConfigLoader();

      assertTrue("Failed to load entity configurations.",
            testMe.loadConfigs(dbConns, new File("./testConfigs"), entCfgs));
   }
}
