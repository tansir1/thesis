package thesis.core.serialization;

import java.io.File;

import thesis.core.EntityCfgs;

public class ConfigLoader
{
   private final String snsrTypeCSV = "sensorTypes.csv";
   private final String targetTypeCSV = "targetTypes.csv";
   private final String wpnTypeCSV = "weaponTypes.csv";
   private final String snsrTargetProbCSV = "sensorTargetProb.csv";
   private final String wpnTargetProbCSV = "weaponTargetProb.csv";

   public ConfigLoader()
   {

   }

   public boolean loadConfigs(DBConnections dbConns, File cfgDir, EntityCfgs entCfgs)
   {
      boolean success = true;

      File snsrTypeFile = new File(cfgDir, snsrTypeCSV);
      File tgtTypeFile = new File(cfgDir, targetTypeCSV);
      File wpnTypeFile = new File(cfgDir, wpnTypeCSV);
      File snsrTgtProbFile = new File(cfgDir, snsrTargetProbCSV);
      File wpnTgtProbFile = new File(cfgDir, wpnTargetProbCSV);

      SensorTypeConfigsDAO snsrTypeCfgsDAO = new SensorTypeConfigsDAO();
      TargetTypeConfigsDAO tgtTypeCfgsDAO = new TargetTypeConfigsDAO();
      WeaponTypeConfigsDAO wpnTypesCfgsDAO = new WeaponTypeConfigsDAO();
      SensorProbsDAO sensorProbsDAO = new SensorProbsDAO();
      WeaponProbsDAO wpnProbsDAO = new WeaponProbsDAO();

      success = snsrTypeCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), snsrTypeFile, entCfgs.getSnsrTypeCfgs());

      if (success)
      {
         success = tgtTypeCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), tgtTypeFile, entCfgs.getTgtTypeCfgs());
      }

      if (success)
      {
         wpnTypesCfgsDAO.loadCSV(dbConns.getConfigDBConnection(), wpnTypeFile, entCfgs.getWpnTypeCfgs());
      }

      if (success)
      {
         sensorProbsDAO.loadCSV(dbConns.getConfigDBConnection(), snsrTgtProbFile, entCfgs.getSnsrProbs());
      }

      if (success)
      {
         wpnProbsDAO.loadCSV(dbConns.getConfigDBConnection(), wpnTgtProbFile, entCfgs.getWpnProbs());
      }

      return success;
   }
}
