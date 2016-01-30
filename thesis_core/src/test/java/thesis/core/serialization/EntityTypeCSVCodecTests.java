package thesis.core.serialization;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;

public class EntityTypeCSVCodecTests
{

   @Test
   public void loadEntityCfgs()
   {
      DBConnections dbConns = new DBConnections();
      assertTrue("Failed to open configuration db.", dbConns.openConfigDB());

      EntityTypeCfgs entCfgs = new EntityTypeCfgs();

      EntityTypeCSVCodec testMe = new EntityTypeCSVCodec();

      assertTrue("Failed to load entity configurations.",
            testMe.loadCSV(dbConns, new File("./testConfigs"), entCfgs));
      dbConns.closeConfigDB();
   }
}
