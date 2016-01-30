package thesis.core.serialization;

import java.io.File;

import thesis.core.world.World;

public class WorldConfigLoader
{
   private final String gisCSV = "gis.csv";
   private final String havensCSV = "havens.csv";
   private final String targetsCSV = "targets.csv";
   private final String uavsCSV = "uavs.csv";

   public WorldConfigLoader()
   {

   }

   public boolean loadConfigs(DBConnections dbConns, File worldDir, World world)
   {
      boolean success = true;

      File gisCfgFile = new File(worldDir, gisCSV);
      File havensCfgFile = new File(worldDir, havensCSV);
      File tgtsCfgFile = new File(worldDir, targetsCSV);
      File uavsCfgFile = new File(worldDir, uavsCSV);

      WorldGISDAO gisDAO = new WorldGISDAO(worldDir.getName());
      HavensDAO havensDAO = new HavensDAO(worldDir.getName());


      success = gisDAO.loadCSV(dbConns.getWorldsDBConnection(), gisCfgFile, world.getWorldGIS());

      if (success)
      {
         success = havensDAO.loadCSV(dbConns.getWorldsDBConnection(), havensCfgFile, world.getHavens());
      }

      return success;
   }
}
