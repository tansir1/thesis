package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;

import thesis.core.serialization.world.HavensDAO;
import thesis.core.serialization.world.RoadNetworkDAO;
import thesis.core.serialization.world.TargetStartLocationDAO;
import thesis.core.serialization.world.UAVStartLocationDAO;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.serialization.world.WorldGISDAO;
import thesis.core.world.World;

public class WorldConfigCSVCodec
{
   private final String gisCSV = "gis.csv";
   private final String havensCSV = "havens.csv";
   private final String targetsCSV = "targets.csv";
   private final String uavsCSV = "uavs.csv";
   private final String roadCSV = "roadnet.csv";

   public WorldConfigCSVCodec()
   {

   }

   public boolean loadCSV(DBConnections dbConns, File worldDir, WorldConfig worldCfg)
   {
      final Connection wrldCon = dbConns.getWorldsDBConnection();
      boolean success = true;


      File gisCfgFile = new File(worldDir, gisCSV);
      File havensCfgFile = new File(worldDir, havensCSV);
      File tgtsCfgFile = new File(worldDir, targetsCSV);
      File uavsCfgFile = new File(worldDir, uavsCSV);
      File roadsCfgFile = new File(worldDir, roadCSV);

      WorldGISDAO gisDAO = new WorldGISDAO(wrldCon, worldDir.getName());
      HavensDAO havensDAO = new HavensDAO(wrldCon, worldDir.getName());
      RoadNetworkDAO roadsDAO = new RoadNetworkDAO(wrldCon, worldDir.getName());
      UAVStartLocationDAO uavStartDAO = new UAVStartLocationDAO(wrldCon, worldDir.getName());
      TargetStartLocationDAO tgtStartDAO = new TargetStartLocationDAO(wrldCon, worldDir.getName());

      World world = worldCfg.getWorld();

      success = gisDAO.loadCSV(gisCfgFile);
      if(success)
      {
         success = gisDAO.loadData(world.getWorldGIS());
      }

      if (success)
      {
         success = havensDAO.loadCSV(havensCfgFile);
         if(success)
         {
            success = havensDAO.loadData(world.getHavens());
         }
      }

      if (success)
      {
         world.getRoadNetwork().reset(world.getWorldGIS().getRowCount(), world.getWorldGIS().getColumnCount());
         success = roadsDAO.loadCSV(roadsCfgFile);
         if(success)
         {
            success = roadsDAO.loadData(world.getRoadNetwork());
         }
      }

      if (success)
      {
         success = uavStartDAO.loadCSV(uavsCfgFile);
         if(success)
         {
            success = uavStartDAO.loadData(worldCfg.getUAVCfgs(), world.getWorldGIS());
         }
      }

      if (success)
      {
         success = tgtStartDAO.loadCSV(tgtsCfgFile);
         if(success)
         {
            success = tgtStartDAO.loadData(worldCfg.getTargetCfgs(), world.getWorldGIS());
         }
      }

      return success;
   }

   public boolean writeCSV(DBConnections dbConns, File worldDir, WorldConfig worldCfg)
   {
      final Connection wrldCon = dbConns.getWorldsDBConnection();
      boolean success = true;


      File gisCfgFile = new File(worldDir, gisCSV);
      File havensCfgFile = new File(worldDir, havensCSV);
      File tgtsCfgFile = new File(worldDir, targetsCSV);
      File uavsCfgFile = new File(worldDir, uavsCSV);
      File roadsCfgFile = new File(worldDir, roadCSV);

      WorldGISDAO gisDAO = new WorldGISDAO(wrldCon, worldDir.getName());
      HavensDAO havensDAO = new HavensDAO(wrldCon, worldDir.getName());
      RoadNetworkDAO roadsDAO = new RoadNetworkDAO(wrldCon, worldDir.getName());
      UAVStartLocationDAO uavStartDAO = new UAVStartLocationDAO(wrldCon, worldDir.getName());
      TargetStartLocationDAO tgtStartDAO = new TargetStartLocationDAO(wrldCon, worldDir.getName());

      gisDAO.createTable();
      havensDAO.createTable();
      roadsDAO.createTable();
      uavStartDAO.createTable();
      tgtStartDAO.createTable();

      World world = worldCfg.getWorld();

      success = gisDAO.saveData(world.getWorldGIS());
      if(success)
      {
         success = gisDAO.writeCSV(gisCfgFile);
      }

      if (success)
      {
         success = havensDAO.saveData(world.getHavens());
         if(success)
         {
            success = havensDAO.writeCSV(havensCfgFile);
         }
      }

      if (success)
      {
         world.getRoadNetwork().reset(world.getWorldGIS().getRowCount(), world.getWorldGIS().getColumnCount());
         success = roadsDAO.saveData(world.getRoadNetwork());
         if(success)
         {
            success = roadsDAO.writeCSV(roadsCfgFile);
         }
      }

      if (success)
      {
         success = uavStartDAO.saveData(worldCfg.getUAVCfgs(), world.getWorldGIS());
         if(success)
         {
            success = uavStartDAO.writeCSV(uavsCfgFile);
         }
      }

      if (success)
      {
         success = tgtStartDAO.saveData(worldCfg.getTargetCfgs(), world.getWorldGIS());
         if(success)
         {
            success = tgtStartDAO.writeCSV(tgtsCfgFile);
         }
      }

      return success;
   }
}
