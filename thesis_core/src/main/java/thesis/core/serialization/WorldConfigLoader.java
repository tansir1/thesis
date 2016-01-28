package thesis.core.serialization;

import java.io.File;

import thesis.core.world.World;

public class WorldConfigLoader
{
   private final String gisCSV = "gis.csv";

   public WorldConfigLoader()
   {

   }

   public boolean loadConfigs(DBConnections dbConns, File worldDir, World world)
   {
      boolean success = true;

      File gisCfgFile = new File(worldDir, gisCSV);

      WorldGISDAO gisDAO = new WorldGISDAO(worldDir.getName());


      success = gisDAO.loadCSV(dbConns.getWorldsDBConnection(), gisCfgFile, world.getWorldGIS());
/*
      if (success)
      {
         success = tgtTypeCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), tgtTypeFile, entCfgs.getTgtTypeCfgs());
      }*/

      return success;
   }
}
