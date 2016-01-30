package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;

import thesis.core.serialization.world.WorldConfig;
import thesis.core.world.World;

public class WorldConfigLoader
{
   private final String gisCSV = "gis.csv";
   private final String havensCSV = "havens.csv";
   private final String targetsCSV = "targets.csv";
   private final String uavsCSV = "uavs.csv";
   private final String roadCSV = "roadnet.csv";

   public WorldConfigLoader()
   {

   }

   public boolean loadConfigs(DBConnections dbConns, File worldDir, WorldConfig worldCfg)
   {
      final Connection wrldCon = dbConns.getWorldsDBConnection();
      boolean success = true;


      File gisCfgFile = new File(worldDir, gisCSV);
      File havensCfgFile = new File(worldDir, havensCSV);
      File tgtsCfgFile = new File(worldDir, targetsCSV);
      File uavsCfgFile = new File(worldDir, uavsCSV);
      File roadsCfgFile = new File(worldDir, roadCSV);

      WorldGISDAO gisDAO = new WorldGISDAO(worldDir.getName());
      HavensDAO havensDAO = new HavensDAO(worldDir.getName());
      RoadNetworkDAO roadsDAO = new RoadNetworkDAO(worldDir.getName());
      UAVStartLocationDAO uavStartDAO = new UAVStartLocationDAO(worldDir.getName());
      TargetStartLocationDAO tgtStartDAO = new TargetStartLocationDAO(worldDir.getName());

      World world = worldCfg.getWorld();

      success = gisDAO.loadCSV(wrldCon, gisCfgFile, world.getWorldGIS());

      if (success)
      {
         success = havensDAO.loadCSV(wrldCon, havensCfgFile, world.getHavens());
      }

      if (success)
      {
         world.getRoadNetwork().reset(world.getWorldGIS().getRowCount(), world.getWorldGIS().getColumnCount());
         success = roadsDAO.loadCSV(wrldCon, roadsCfgFile, world.getRoadNetwork());
      }

      if (success)
      {
         success = uavStartDAO.loadCSV(wrldCon, uavsCfgFile, worldCfg.getUAVCfgs(), world.getWorldGIS());
      }

      if (success)
      {
         success = tgtStartDAO.loadCSV(wrldCon, tgtsCfgFile, worldCfg.getTargetCfgs(), world.getWorldGIS());
      }

      return success;
   }
}
