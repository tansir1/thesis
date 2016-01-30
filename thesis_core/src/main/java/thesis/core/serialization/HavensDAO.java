package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.world.Havens;

public class HavensDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME;
   private final String rowsColName = "row";
   private final String colsColName = "col";

   public HavensDAO(String worldName)
   {
      TBL_NAME = "havens_" + worldName + "_cfg";
   }

   public boolean loadCSV(Connection dbCon, File csvFile, Havens havens)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);

         StringBuilder initTblSQL = new StringBuilder("create table ");
         initTblSQL.append(TBL_NAME);
         initTblSQL.append("(");
         initTblSQL.append(rowsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(colsColName);
         initTblSQL.append(" int not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(rowsColName + "," + colsColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         ResultSet rs = stmt.executeQuery("select count(*) from " + TBL_NAME);
         rs.next();
         int numTypes = rs.getInt(1);
         logger.info("Loading {} havens.", numTypes);
         rs.close();

         havens.reset(numTypes);

         rs = stmt.executeQuery("select * from " + TBL_NAME);
         int idx = 0;
         while(rs.next())
         {
            int row = rs.getInt(rowsColName);
            int col = rs.getInt(colsColName);
            havens.setHavenByIndx(idx, row, col);

            ++idx;
         }

         rs.close();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load haven configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
