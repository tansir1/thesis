package thesis.core.entities.uav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.TestUtils;
import thesis.core.common.Circle;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.UAVEntityConfig;
import thesis.core.serialization.world.WorldConfig;

public class UAVMgrTests
{

   @Test
   public void regionQueryTests()
   {
      final EntityTypes entTypes = TestUtils.randEntityTypes();

      // 100km x 100km world, 10x10 grid, each cell should be 10km x 10km
      final WorldConfig worldCfg = new WorldConfig();
      worldCfg.setNumColumns(10);
      worldCfg.setNumRows(10);
      worldCfg.setWorldHeight(100000);
      worldCfg.setWorldWidth(100000);

      Collection<UAVType> allUAVTypes = entTypes.getAllUAVTypes();
      final UAVType uavTypes[] = new UAVType[3];

      if (allUAVTypes.size() < 3)
      {
         fail("Test case assumes at least 3 uav types.");
      }
      else
      {
         Iterator<UAVType> itr = allUAVTypes.iterator();
         for (int i = 0; i < 3; ++i)
         {
            uavTypes[i] = itr.next();
         }
      }

      // Construct 3 UAVs in a west to east line.
      final UAVEntityConfig uav1 = new UAVEntityConfig();
      uav1.setUAVType(uavTypes[0].getTypeID());
      uav1.getLocation().setCoordinate(500, 0);

      final UAVEntityConfig uav2 = new UAVEntityConfig();
      uav2.setUAVType(uavTypes[1].getTypeID());
      uav2.getLocation().setCoordinate(500, 250);

      final UAVEntityConfig uav3 = new UAVEntityConfig();
      uav3.setUAVType(uavTypes[2].getTypeID());
      uav3.getLocation().setCoordinate(500, 500);

      worldCfg.uavCfgs.add(uav1);
      worldCfg.uavCfgs.add(uav2);
      worldCfg.uavCfgs.add(uav3);

      UAVMgr testMe = new UAVMgr();
      testMe.reset(entTypes, worldCfg, 0, new Random(), worldCfg.getMaxWorldDistance() * 0.1, 0.0f);

      // -----Perform test computations-----
      Circle testRegion = new Circle();
      testRegion.setRadius(50);
      // Slightly east of uav1
      testRegion.getCenter().setCoordinate(500, 10);

      // Should only contain uav1
      List<UAV> inRegion = testMe.getAllUAVsInRegion(testRegion);
      assertEquals("Incorrect number of UAVs in query region1", 1, inRegion.size());
      assertEquals("Incorrect uav detected in region.", uav1.getUAVType(), inRegion.get(0).getType().getTypeID());

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
         if (uav.getType().getTypeID() == uav2.getUAVType())
         {
            uav2Found = true;
         }
         else if (uav.getType().getTypeID() == uav3.getUAVType())
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
