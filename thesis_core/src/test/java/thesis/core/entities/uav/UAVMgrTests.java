package thesis.core.entities.uav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.Circle;
import thesis.core.serialization.world.UAVStartCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.uav.UAV;
import thesis.core.uav.UAVMgr;
import thesis.core.uav.comms.CommsConfig;
import thesis.core.world.WorldGIS;

public class UAVMgrTests
{
   private List<UAVStartCfg> initUAVs()
   {
      // Construct 3 UAVs in a west to east line.
      final UAVStartCfg uav1 = new UAVStartCfg();
      uav1.setUAVType(0);
      uav1.getLocation().setCoordinate(500, 0);

      final UAVStartCfg uav2 = new UAVStartCfg();
      uav2.setUAVType(1);
      uav2.getLocation().setCoordinate(500, 250);

      final UAVStartCfg uav3 = new UAVStartCfg();
      uav3.setUAVType(2);
      uav3.getLocation().setCoordinate(500, 500);

      List<UAVStartCfg> startCfgs = new ArrayList<UAVStartCfg>();
      startCfgs.add(uav1);
      startCfgs.add(uav2);
      startCfgs.add(uav3);
      return startCfgs;
   }

   @Test
   public void regionQueryTests()
   {
      final EntityTypeCfgs entTypes = new EntityTypeCfgs();
      entTypes.getUAVTypeCfgs().reset(3);

      // 100km x 100km world, 10x10 grid, each cell should be 10km x 10km
      final WorldGIS worldGIS = new WorldGIS();
      worldGIS.reset(100000, 100000, 10, 10);

      UAVMgr testMe = new UAVMgr();
      testMe.reset(entTypes, initUAVs(), new TargetMgr(), new Random(), new CommsConfig());

      // -----Perform test computations-----
      Circle testRegion = new Circle();
      testRegion.setRadius(50);
      // Slightly east of uav1
      testRegion.getCenter().setCoordinate(500, 10);

      // Should only contain uav1
      List<UAV> inRegion = testMe.getAllUAVsInRegion(testRegion);
      assertEquals("Incorrect number of UAVs in query region1", 1, inRegion.size());
      assertEquals("Incorrect uav detected in region.", 0, inRegion.get(0).getType());

      // Slightly east of uav3
      testRegion.getCenter().setCoordinate(500, 510);
      testRegion.setRadius(300);// UAVs are 250m apart, 300 will
                                // catch two of them
      inRegion = testMe.getAllUAVsInRegion(testRegion);
      assertEquals("Incorrect number of UAVs in query region2", 2, inRegion.size());

      boolean uav2Found = false;
      boolean uav3Found = false;
      for (UAV uav : inRegion)
      {
         if (uav.getType() == 1)
         {
            uav2Found = true;
         }
         else if (uav.getType() == 2)
         {
            uav3Found = true;
         }
         else
         {
            fail("Unknown UAV type found in query region.");
         }
      }
      assertTrue("Did not find UAV 2 in query region2.", uav2Found);
      assertTrue("Did not find UAV 3 in query region2.", uav3Found);
   }
}
