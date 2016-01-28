package thesis.core;

import thesis.core.entities.uav.UAVTypeConfigs;
import thesis.core.sensors.SensorProbs;
import thesis.core.sensors.SensorTypeConfigs;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.weapons.WeaponProbs;
import thesis.core.weapons.WeaponTypeConfigs;

public class EntityTypeCfgs
{
   private SensorTypeConfigs snsrTypeCfgs;
   private TargetTypeConfigs tgtTypeCfgs;
   private WeaponTypeConfigs wpnTypeCfgs;
   private UAVTypeConfigs uavTypeCfgs;

   private SensorProbs snsrProbs;
   private WeaponProbs wpnProbs;

   public EntityTypeCfgs()
   {
      snsrTypeCfgs = new SensorTypeConfigs();
      tgtTypeCfgs = new TargetTypeConfigs();
      wpnTypeCfgs = new WeaponTypeConfigs();
      uavTypeCfgs = new UAVTypeConfigs();

      snsrProbs = new SensorProbs();
      wpnProbs = new WeaponProbs();
   }

   public SensorTypeConfigs getSnsrTypeCfgs()
   {
      return snsrTypeCfgs;
   }

   public TargetTypeConfigs getTgtTypeCfgs()
   {
      return tgtTypeCfgs;
   }

   public WeaponTypeConfigs getWpnTypeCfgs()
   {
      return wpnTypeCfgs;
   }

   public UAVTypeConfigs getUAVTypeCfgs()
   {
      return uavTypeCfgs;
   }

   public SensorProbs getSnsrProbs()
   {
      return snsrProbs;
   }

   public WeaponProbs getWpnProbs()
   {
      return wpnProbs;
   }

}
