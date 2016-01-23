package thesis.core.serialization;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

public class DBConnectionTest
{

   @Test
   public void connectionTest()
   {

      try
      {
         Class.forName("org.h2.Driver");
         Connection conn = DriverManager.
             getConnection("jdbc:h2:~/test", "sa", "");

         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM TEST");
         while(rs.next())
         {
            int id = rs.getInt("ID");
            String name = rs.getString("NAME");
            System.out.println(String.format("%d, %s",id, name));
         }
         rs.close();
         stmt.close();
         conn.close();
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }
}
