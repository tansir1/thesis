package thesis.core.serialization;

import java.io.File;

import thesis.core.EntityTypeCfgs;

public class ConfigLoader
{
   private final String snsrTypeCSV = "sensorTypes.csv";
   private final String targetTypeCSV = "targetTypes.csv";
   private final String wpnTypeCSV = "weaponTypes.csv";
   private final String uavTypeCSV = "uavTypes.csv";

   private final String snsrTargetProbCSV = "sensorTargetProb.csv";
   private final String wpnTargetProbCSV = "weaponTargetProb.csv";
   private final String uavSnsrCSV = "uavSensorMap.csv";
   private final String uavWpnsCSV = "uavWeaponMap.csv";

   public ConfigLoader()
   {

   }

   public boolean loadConfigs(DBConnections dbConns, File cfgDir, EntityTypeCfgs entCfgs)
   {
      boolean success = true;

      File snsrTypeFile = new File(cfgDir, snsrTypeCSV);
      File tgtTypeFile = new File(cfgDir, targetTypeCSV);
      File wpnTypeFile = new File(cfgDir, wpnTypeCSV);
      File uavTypeFile = new File(cfgDir, uavTypeCSV);
      File snsrTgtProbFile = new File(cfgDir, snsrTargetProbCSV);
      File wpnTgtProbFile = new File(cfgDir, wpnTargetProbCSV);
      File uavSnsrFile = new File(cfgDir, uavSnsrCSV);
      File uavWpnsFile = new File(cfgDir, uavWpnsCSV);

      SensorTypeConfigsDAO snsrTypeCfgsDAO = new SensorTypeConfigsDAO();
      TargetTypeConfigsDAO tgtTypeCfgsDAO = new TargetTypeConfigsDAO();
      WeaponTypeConfigsDAO wpnTypesCfgsDAO = new WeaponTypeConfigsDAO();
      UAVTypeConfigsDAO uavTypeCfgsDAO = new UAVTypeConfigsDAO();
      SensorProbsDAO sensorProbsDAO = new SensorProbsDAO();
      WeaponProbsDAO wpnProbsDAO = new WeaponProbsDAO();
      UAVSensorCfgsDAO uavSnsrCfgsDAO = new UAVSensorCfgsDAO();
      UAVWeaponCfgsDAO uavWpnsCfgsDAO = new UAVWeaponCfgsDAO();

      success = snsrTypeCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), snsrTypeFile, entCfgs.getSnsrTypeCfgs());

      if (success)
      {
         success = tgtTypeCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), tgtTypeFile, entCfgs.getTgtTypeCfgs());
      }

      if (success)
      {
         wpnTypesCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), wpnTypeFile, entCfgs.getWpnTypeCfgs());
      }

      if(success)
      {
         uavTypeCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), uavTypeFile, entCfgs.getUAVTypeCfgs());
      }

      if (success)
      {
         entCfgs.getSnsrProbs().reset(entCfgs.getSnsrTypeCfgs().getNumTypes(), entCfgs.getTgtTypeCfgs().getNumTypes());
         success = sensorProbsDAO.loadCSV(dbConns.getConfigDBConnection(), snsrTgtProbFile, entCfgs.getSnsrProbs());
      }

      if (success)
      {
         entCfgs.getWpnProbs().reset(entCfgs.getWpnTypeCfgs().getNumTypes(), entCfgs.getTgtTypeCfgs().getNumTypes());
         success = wpnProbsDAO.loadCSV(dbConns.getConfigDBConnection(), wpnTgtProbFile, entCfgs.getWpnProbs());
      }

      if( success)
      {
         entCfgs.getUAVSensorCfgs().reset(entCfgs.getUAVTypeCfgs().getNumTypes(), entCfgs.getSnsrTypeCfgs().getNumTypes());
         uavSnsrCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), uavSnsrFile, entCfgs.getUAVSensorCfgs());
      }

      if( success)
      {
         entCfgs.getUAVWeaponCfgs().reset(entCfgs.getUAVTypeCfgs().getNumTypes(), entCfgs.getWpnTypeCfgs().getNumTypes());
         uavWpnsCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), uavWpnsFile, entCfgs.getUAVWeaponCfgs());
      }



      return success;
   }
}
