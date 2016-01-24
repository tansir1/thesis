package thesis.core.serialization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DBConnectionsTests
{

   @Test
   public void configDBTest()
   {
      DBConnections testMe = new DBConnections();
      assertTrue("Failed to open config db.", testMe.openConfigDB());
      assertNotNull("Got a null config db connection.", testMe.getConfigDBConnection());
      testMe.closeConfigDB();
   }
}
