package thesis.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import thesis.core.common.HavenRouting;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorProbs;
import thesis.core.sensors.SensorTypeConfigs;
import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.uav.UAVTypeConfigs;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;

/**
 * A collection of utility functions for running unit tests.
 */
public class TestUtils
{
   public static WorldCoordinate randomWorldCoordinate(WorldGIS gis)
   {
      Random rand = new Random();

      WorldCoordinate wc = new WorldCoordinate();
      wc.setEast(rand.nextDouble() * gis.getWidth());
      wc.setNorth(rand.nextDouble() * gis.getHeight());

      return wc;
   }

   public static TargetMgr genericTgtMgr(World world, EntityTypeCfgs entTypeCfgs, int numTgts)
   {
      Random rand = new Random();
      TargetMgr tgtMgr = new TargetMgr();

      List<TargetStartCfg> tgtStartCfgs = new ArrayList<TargetStartCfg>();

      for(int i=0; i<numTgts;++i)
      {
         TargetStartCfg tgtCfg = new TargetStartCfg();
         tgtCfg.setOrientation(rand.nextInt(359));
         tgtCfg.setTargetType(rand.nextInt(entTypeCfgs.getTgtTypeCfgs().getNumTypes()));
         tgtCfg.getLocation().setCoordinate(randomWorldCoordinate(world.getWorldGIS()));

         tgtStartCfgs.add(tgtCfg);
      }

      tgtMgr.reset(entTypeCfgs.getTgtTypeCfgs(), tgtStartCfgs, new HavenRouting(world, rand), world.getWorldGIS());
      return tgtMgr;
   }

   public static EntityTypeCfgs genericEntityCfgs(final int numTgtTypes, final int numSnsrTypes, final int numUAVTypes)
   {
      Random rand = new Random();

      // Sensor probabilities
      final double MAX_DETECT_TGT_PROB = 0.6;
      final double MAX_DETECT_EMPTY_PROB = 0.7;
      final double MAX_CONFIRM_PROB = 0.8;
      final double MAX_HEADING_COEFF = 0.4;
      final double MAX_MISCLASS_PROB = 0.4;

      // Sensor config limits
      final double Min_SNSR_RNG = 100;
      final double MAX_SNSR_RNG = 1000;
      final double MIN_FOV = 5;
      final double MAX_FOV = 45;
      final double MAX_SLEW_RT = 60;// 60 deg/s

      // UAV config limits
      final double MAX_UAV_SPD = 100;
      final double MIN_UAV_TURN_RT = 10;
      final double MAX_UAV_TURN_RT = 40;

      EntityTypeCfgs entCfgs = new EntityTypeCfgs();

      TargetTypeConfigs tgtTypeCfgs = entCfgs.getTgtTypeCfgs();
      tgtTypeCfgs.reset(numTgtTypes);
      for (int i = 0; i < numTgtTypes; ++i)
      {
         tgtTypeCfgs.setTargetData(i, -1f, rand.nextInt(360));
      }

      SensorTypeConfigs snsrTypeCfgs = entCfgs.getSnsrTypeCfgs();
      snsrTypeCfgs.reset(numSnsrTypes);
      for (int i = 0; i < numSnsrTypes; ++i)
      {
         double fov = (rand.nextDouble() * MAX_FOV) + MIN_FOV;
         snsrTypeCfgs.setSensorData(i, fov, Min_SNSR_RNG, rand.nextDouble() * MAX_SNSR_RNG,
               rand.nextDouble() * MAX_SLEW_RT);
      }

      SensorProbs pyldProb = entCfgs.getSnsrProbs();
      pyldProb.reset(numSnsrTypes, numTgtTypes);
      for (int i = 0; i < numSnsrTypes; ++i)
      {
         for (int j = 0; j < numTgtTypes; ++j)
         {
            pyldProb.setSensorDetectEmptyProb(i, rand.nextDouble() * MAX_DETECT_EMPTY_PROB);
            pyldProb.setSensorDetectTgtProb(i, j, rand.nextDouble() * MAX_DETECT_TGT_PROB);
            pyldProb.setSensorHeadingCoeff(i, j, rand.nextDouble() * MAX_HEADING_COEFF);

            for (int k = 0; k < numTgtTypes; ++k)
            {
               if (k != j)
               {
                  pyldProb.setSensorMisclassifyProb(i, j, k, rand.nextDouble() * MAX_MISCLASS_PROB);
               }
            }

         }
      }

      entCfgs.getUAVSensorCfgs().reset(numUAVTypes, numSnsrTypes);
      UAVTypeConfigs uavTypeCfgs = entCfgs.getUAVTypeCfgs();
      uavTypeCfgs.reset(numUAVTypes);
      for (int i = 0; i < numUAVTypes; ++i)
      {
         uavTypeCfgs.setUAVData(i, rand.nextDouble() * MAX_UAV_SPD,
               (rand.nextDouble() * MAX_UAV_TURN_RT) + MIN_UAV_TURN_RT);

         entCfgs.getUAVSensorCfgs().addSensorToUAV(i, rand.nextInt(numSnsrTypes));
      }

      return entCfgs;
   }

}
