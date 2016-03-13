package thesis.core.uav.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.HavenRouting;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.Sensor;
import thesis.core.sensors.SensorGroup;
import thesis.core.sensors.SensorProbs;
import thesis.core.sensors.SensorScanLogic;
import thesis.core.sensors.SensorTypeConfigs;
import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.uav.Pathing;
import thesis.core.uav.UAVTypeConfigs;
import thesis.core.uav.logic.ConfirmTask.State;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;

public class ConfirmTaskTests
{
   private void initTarget(WorldBelief wb, TargetMgr tgtMgr, WorldGIS gis, WorldCoordinate targetCoord)
   {
      World world = new World();

      TargetTypeConfigs tgtTypeCfgs = new TargetTypeConfigs();
      tgtTypeCfgs.reset(1);
      tgtTypeCfgs.setTargetData(0, 50, 0);

      List<TargetStartCfg> tgtStartCfgs = new ArrayList<TargetStartCfg>();
      TargetStartCfg startCfg = new TargetStartCfg();
      startCfg.getLocation().setCoordinate(targetCoord);
      startCfg.setTargetType(0);
      tgtStartCfgs.add(startCfg);

      // initialize the one and only target
      tgtMgr.reset(tgtTypeCfgs, tgtStartCfgs, new HavenRouting(world, new Random()), gis);

      // Fake results of searching
      wb.getCellBelief(0, 0).updateEmptyBelief(0, 0);
      TargetBelief tb = wb.getTargetBelief(0);
      tb.getCoordinate().setCoordinate(targetCoord);
   }

   private void initSensor(SensorGroup snsrGrp, TargetMgr tgtMgr)
   {
      SensorTypeConfigs typeCfgs = new SensorTypeConfigs();
      typeCfgs.reset(1);

      typeCfgs.setSensorData(0, 45, 100, 300, 5);

      Sensor snsr = new Sensor(0, 0, typeCfgs, tgtMgr);
      snsrGrp.addSensor(snsr);
   }

   @Test
   public void test()
   {
      final double DISTANCE_TOLERANCE = 0.00001;

      //------Test Setup----
      WorldGIS gis = new WorldGIS();
      gis.reset(1000, 1000, 1, 1);// Single cell world

      WorldCoordinate targetCoord = new WorldCoordinate(500, 500);

      UAVTypeConfigs typeCfgs = new UAVTypeConfigs();
      typeCfgs.reset(1);
      typeCfgs.setUAVData(0, 10, 50);

      TargetMgr tgtMgr = new TargetMgr();

      Pathing pathing = new Pathing(0, 0, typeCfgs);
      WorldBelief worldBlf = new WorldBelief(1, 1, 1, 0d);

      initTarget(worldBlf, tgtMgr, gis, targetCoord);


      SensorProbs pyldProbs = new SensorProbs();
      SensorScanLogic snsrScanLogic = new SensorScanLogic(pyldProbs, tgtMgr, new Random());
      SensorGroup snsrGrp = new SensorGroup(snsrScanLogic, gis);

      initSensor(snsrGrp, tgtMgr);

      //-----Run test steps-----
      WorldCoordinate sensorLocation = new WorldCoordinate();
      pathing.setHeading(sensorLocation.bearingTo(targetCoord));
      ConfirmTask testMe = new ConfirmTask(0);
      testMe.reset(0);
      assertEquals("Should be in the initialization state.", State.Init, testMe.getState());
      testMe.stepSimulation(worldBlf, pathing, snsrGrp);

      assertTrue("Confirm location and flight path don't match.",
            pathing.getFlightPath().getEndPose().getCoordinate().distanceTo(targetCoord) < DISTANCE_TOLERANCE);


      //Move 50% of the way to the target
      sensorLocation.setCoordinate(targetCoord.getNorth() / 2, targetCoord.getEast() / 2);
      pathing.teleportTo(sensorLocation);
      testMe.stepSimulation(worldBlf, pathing, snsrGrp);
      assertEquals("Should be en-route", State.EnRoute, testMe.getState());

      //Move to minimum orbit range plus 5%
      double percentInsideOrbitRng = ConfirmTask.SENSOR_DISTANCE_ORBIT_PERCENT + 0.05;
      sensorLocation.setCoordinate(targetCoord.getNorth() * percentInsideOrbitRng,
            targetCoord.getEast() * percentInsideOrbitRng);
      pathing.teleportTo(sensorLocation);
      testMe.stepSimulation(worldBlf, pathing, snsrGrp);
      assertEquals("Should be orbiting", State.Orbiting, testMe.getState());
   }
}
