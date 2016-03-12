package thesis.core.serialization;

import java.io.File;

import thesis.core.EntityTypeCfgs;
import thesis.core.serialization.entities.SensorEmptyProbsDAO;
import thesis.core.serialization.entities.SensorMisclassifyProbsDAO;
import thesis.core.serialization.entities.SensorProbsDAO;
import thesis.core.serialization.entities.SensorTypeConfigsDAO;
import thesis.core.serialization.entities.TargetTypeConfigsDAO;
import thesis.core.serialization.entities.UAVSensorCfgsDAO;
import thesis.core.serialization.entities.UAVTypeConfigsDAO;
import thesis.core.serialization.entities.UAVWeaponCfgsDAO;
import thesis.core.serialization.entities.WeaponProbsDAO;
import thesis.core.serialization.entities.WeaponTypeConfigsDAO;

public class EntityTypeCSVCodec
{
   private final String snsrTypeCSV = "sensorTypes.csv";
   private final String targetTypeCSV = "targetTypes.csv";
   private final String wpnTypeCSV = "weaponTypes.csv";
   private final String uavTypeCSV = "uavTypes.csv";

   private final String snsrTargetProbCSV = "sensorTargetProb.csv";
   private final String snsrEmptyProbCSV = "sensorEmptyProb.csv";
   private final String wpnTargetProbCSV = "weaponTargetProb.csv";
   private final String uavSnsrCSV = "uavSensorMap.csv";
   private final String uavWpnsCSV = "uavWeaponMap.csv";
   private final String snsrMisclassCSV = "sensorMisclass.csv";

   public EntityTypeCSVCodec()
   {

   }

   public boolean writeCSV(DBConnections dbConns, File cfgDir, EntityTypeCfgs entCfgs)
   {
      boolean success = true;

      File snsrTypeFile = new File(cfgDir, snsrTypeCSV);
      File tgtTypeFile = new File(cfgDir, targetTypeCSV);
      File wpnTypeFile = new File(cfgDir, wpnTypeCSV);
      File uavTypeFile = new File(cfgDir, uavTypeCSV);
      File snsrTgtProbFile = new File(cfgDir, snsrTargetProbCSV);
      File snsrEmptyProbFile = new File(cfgDir, snsrEmptyProbCSV);
      File wpnTgtProbFile = new File(cfgDir, wpnTargetProbCSV);
      File uavSnsrFile = new File(cfgDir, uavSnsrCSV);
      File uavWpnsFile = new File(cfgDir, uavWpnsCSV);
      File snsrMisclassFile = new File(cfgDir, snsrMisclassCSV);

      SensorTypeConfigsDAO snsrTypeCfgsDAO = new SensorTypeConfigsDAO(dbConns.getConfigDBConnection());
      TargetTypeConfigsDAO tgtTypeCfgsDAO = new TargetTypeConfigsDAO(dbConns.getConfigDBConnection());
      WeaponTypeConfigsDAO wpnTypesCfgsDAO = new WeaponTypeConfigsDAO(dbConns.getConfigDBConnection());
      UAVTypeConfigsDAO uavTypeCfgsDAO = new UAVTypeConfigsDAO(dbConns.getConfigDBConnection());
      SensorProbsDAO sensorProbsDAO = new SensorProbsDAO(dbConns.getConfigDBConnection());
      SensorEmptyProbsDAO sensorEmptyProbsDAO = new SensorEmptyProbsDAO(dbConns.getConfigDBConnection());
      WeaponProbsDAO wpnProbsDAO = new WeaponProbsDAO(dbConns.getConfigDBConnection());
      UAVSensorCfgsDAO uavSnsrCfgsDAO = new UAVSensorCfgsDAO(dbConns.getConfigDBConnection());
      UAVWeaponCfgsDAO uavWpnsCfgsDAO = new UAVWeaponCfgsDAO(dbConns.getConfigDBConnection());
      SensorMisclassifyProbsDAO snsrMisclassDAO = new SensorMisclassifyProbsDAO(dbConns.getConfigDBConnection());

      snsrTypeCfgsDAO.createTable();
      tgtTypeCfgsDAO.createTable();
      wpnTypesCfgsDAO.createTable();
      uavTypeCfgsDAO.createTable();
      sensorProbsDAO.createTable();
      sensorEmptyProbsDAO.createTable();
      wpnProbsDAO.createTable();
      uavSnsrCfgsDAO.createTable();
      uavWpnsCfgsDAO.createTable();
      snsrMisclassDAO.createTable();

      success = snsrTypeCfgsDAO.saveData(entCfgs.getSnsrTypeCfgs());
      if (success)
      {
         success = snsrTypeCfgsDAO.writeCSV(snsrTypeFile);
      }

      if (success)
      {
         success = tgtTypeCfgsDAO.saveData(entCfgs.getTgtTypeCfgs());
         if (success)
         {
            success = tgtTypeCfgsDAO.writeCSV(tgtTypeFile);
         }
      }

      if (success)
      {
         success = wpnTypesCfgsDAO.saveData(entCfgs.getWpnTypeCfgs());
         if (success)
         {
            success = wpnTypesCfgsDAO.writeCSV(wpnTypeFile);
         }
      }

      if (success)
      {
         success = uavTypeCfgsDAO.saveData(entCfgs.getUAVTypeCfgs());
         if (success)
         {
            success = uavTypeCfgsDAO.writeCSV(uavTypeFile);
         }
      }

      if (success)
      {
         success = sensorProbsDAO.saveData(entCfgs.getSnsrProbs());
         if (success)
         {
            success = sensorProbsDAO.writeCSV(snsrTgtProbFile);
         }
      }

      if (success)
      {
         success = sensorEmptyProbsDAO.saveData(entCfgs.getSnsrProbs());
         if (success)
         {
            success = sensorEmptyProbsDAO.writeCSV(snsrEmptyProbFile);
         }
      }

      if (success)
      {
         success = wpnProbsDAO.saveData(entCfgs.getWpnProbs());
         if (success)
         {
            success = wpnProbsDAO.writeCSV(wpnTgtProbFile);
         }
      }

      if (success)
      {
         success = uavSnsrCfgsDAO.saveData(entCfgs.getUAVSensorCfgs());
         if (success)
         {
            success = uavSnsrCfgsDAO.writeCSV(uavSnsrFile);
         }
      }

      if (success)
      {
         success = uavWpnsCfgsDAO.saveData(entCfgs.getUAVWeaponCfgs());
         if (success)
         {
            success = uavWpnsCfgsDAO.writeCSV(uavWpnsFile);
         }
      }

      if (success)
      {
         success = snsrMisclassDAO.saveData(entCfgs.getSnsrProbs());
         if (success)
         {
            success = snsrMisclassDAO.writeCSV(snsrMisclassFile);
         }
      }

      return success;
   }

   public boolean loadCSV(DBConnections dbConns, File cfgDir, EntityTypeCfgs entCfgs)
   {
      boolean success = true;

      File snsrTypeFile = new File(cfgDir, snsrTypeCSV);
      File tgtTypeFile = new File(cfgDir, targetTypeCSV);
      File wpnTypeFile = new File(cfgDir, wpnTypeCSV);
      File uavTypeFile = new File(cfgDir, uavTypeCSV);
      File snsrTgtProbFile = new File(cfgDir, snsrTargetProbCSV);
      File snsrEmptyProbFile = new File(cfgDir, snsrEmptyProbCSV);
      File wpnTgtProbFile = new File(cfgDir, wpnTargetProbCSV);
      File uavSnsrFile = new File(cfgDir, uavSnsrCSV);
      File uavWpnsFile = new File(cfgDir, uavWpnsCSV);
      File snsrMisclassFile = new File(cfgDir, snsrMisclassCSV);

      SensorTypeConfigsDAO snsrTypeCfgsDAO = new SensorTypeConfigsDAO(dbConns.getConfigDBConnection());
      TargetTypeConfigsDAO tgtTypeCfgsDAO = new TargetTypeConfigsDAO(dbConns.getConfigDBConnection());
      WeaponTypeConfigsDAO wpnTypesCfgsDAO = new WeaponTypeConfigsDAO(dbConns.getConfigDBConnection());
      UAVTypeConfigsDAO uavTypeCfgsDAO = new UAVTypeConfigsDAO(dbConns.getConfigDBConnection());
      SensorProbsDAO sensorProbsDAO = new SensorProbsDAO(dbConns.getConfigDBConnection());
      SensorEmptyProbsDAO sensorEmptyProbsDAO = new SensorEmptyProbsDAO(dbConns.getConfigDBConnection());
      WeaponProbsDAO wpnProbsDAO = new WeaponProbsDAO(dbConns.getConfigDBConnection());
      UAVSensorCfgsDAO uavSnsrCfgsDAO = new UAVSensorCfgsDAO(dbConns.getConfigDBConnection());
      UAVWeaponCfgsDAO uavWpnsCfgsDAO = new UAVWeaponCfgsDAO(dbConns.getConfigDBConnection());
      SensorMisclassifyProbsDAO snsrMisclassDAO = new SensorMisclassifyProbsDAO(dbConns.getConfigDBConnection());

      success = snsrTypeCfgsDAO.loadCSV(snsrTypeFile);
      if (success)
      {
         success = snsrTypeCfgsDAO.loadData(entCfgs.getSnsrTypeCfgs());
      }

      if (success)
      {
         success = tgtTypeCfgsDAO.loadCSV(tgtTypeFile);
         if (success)
         {
            success = tgtTypeCfgsDAO.loadData(entCfgs.getTgtTypeCfgs());
         }
      }

      if (success)
      {
         success = wpnTypesCfgsDAO.loadCSV(wpnTypeFile);
         if (success)
         {
            success = wpnTypesCfgsDAO.loadData(entCfgs.getWpnTypeCfgs());
         }
      }

      if (success)
      {
         success = uavTypeCfgsDAO.loadCSV(uavTypeFile);

         if (success)
         {
            success = uavTypeCfgsDAO.loadData(entCfgs.getUAVTypeCfgs());
         }
      }

      if (success)
      {
         entCfgs.getSnsrProbs().reset(entCfgs.getSnsrTypeCfgs().getNumTypes(), entCfgs.getTgtTypeCfgs().getNumTypes());
         success = sensorProbsDAO.loadCSV(snsrTgtProbFile);

         if (success)
         {
            success = sensorProbsDAO.loadData(entCfgs.getSnsrProbs());
         }
      }

      if (success)
      {
         success = sensorEmptyProbsDAO.loadCSV(snsrEmptyProbFile);

         if (success)
         {
            success = sensorEmptyProbsDAO.loadData(entCfgs.getSnsrProbs());
         }
      }

      if (success)
      {
         entCfgs.getWpnProbs().reset(entCfgs.getWpnTypeCfgs().getNumTypes(), entCfgs.getTgtTypeCfgs().getNumTypes());
         success = wpnProbsDAO.loadCSV(wpnTgtProbFile);
         if (success)
         {
            success = wpnProbsDAO.loadData(entCfgs.getWpnProbs());
         }
      }

      if (success)
      {
         entCfgs.getUAVSensorCfgs().reset(entCfgs.getUAVTypeCfgs().getNumTypes(),
               entCfgs.getSnsrTypeCfgs().getNumTypes());
         uavSnsrCfgsDAO.loadCSV(uavSnsrFile);
         if (success)
         {
            success = uavSnsrCfgsDAO.loadData(entCfgs.getUAVSensorCfgs());
         }
      }

      if (success)
      {
         entCfgs.getUAVWeaponCfgs().reset(entCfgs.getUAVTypeCfgs().getNumTypes(),
               entCfgs.getWpnTypeCfgs().getNumTypes());
         uavWpnsCfgsDAO.loadCSV(uavWpnsFile);
         if (success)
         {
            success = uavWpnsCfgsDAO.loadData(entCfgs.getUAVWeaponCfgs());
         }
      }

      if (success)
      {
         snsrMisclassDAO.loadCSV(snsrMisclassFile);
         if (success)
         {
            success = snsrMisclassDAO.loadData(entCfgs.getSnsrProbs());
         }
      }

      return success;
   }
}
