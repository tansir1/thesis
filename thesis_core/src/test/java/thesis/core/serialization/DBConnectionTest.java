package thesis.core.serialization;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

import thesis.core.targets.TargetTypeConfigs;

public class DBConnectionTest
{
/*
   @Test
   public void connectionTest()
   {

      try
      {
         Class.forName("org.h2.Driver");
         Connection conn = DriverManager.
             getConnection("jdbc:h2:./cfg", "sa", "");

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
   }*/

   @Test
   public void stest2()
   {


      try
      {
         Class.forName("org.h2.Driver");
         Connection conn = DriverManager.
             getConnection("jdbc:h2:../config/cfg", "sa", "");

         TargetTypeConfigs tgtTypeCfg = new TargetTypeConfigs();
         TargetTypeConfigsDAO testMe = new TargetTypeConfigsDAO();
         testMe.loadCSV(conn, new File("../config/targetTypes.csv"), tgtTypeCfg);

         conn.close();
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }
}
