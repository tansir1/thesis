package thesis.core.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class RoadNetworkTests
{

   @Test
   public void pathFinding()
   {
      RoadNetwork testMe = new RoadNetwork();
      testMe.reset(3, 3);

      // This is a depiction of the test network
      // T = traversable, N = Not traversable
      // T | T | T
      // N | N | T
      // N | N | T

      testMe.setTraversable(0, 0, true);
      testMe.setTraversable(0, 1, true);
      testMe.setTraversable(0, 2, true);
      testMe.setTraversable(1, 2, true);
      testMe.setTraversable(2, 2, true);

      assertFalse("Bad invalid traversable lookup.", testMe.isTraversable(2, 1));
      assertTrue("Bad valid traversable lookup.", testMe.isTraversable(0, 1));

      List<CellCoordinate> path = testMe.findPath(0, 0, 2, 2);
      assertEquals("Invalid path found.", 5, path.size());

      CellCoordinate temp = path.get(0);
      assertEquals("Bad cell 0 row", 0, temp.getRow());
      assertEquals("Bad cell 0 col", 0, temp.getColumn());

      temp = path.get(1);
      assertEquals("Bad cell 1 row", 0, temp.getRow());
      assertEquals("Bad cell 1 col", 1, temp.getColumn());

      temp = path.get(2);
      assertEquals("Bad cell 2 row", 0, temp.getRow());
      assertEquals("Bad cell 2 col", 2, temp.getColumn());

      temp = path.get(3);
      assertEquals("Bad cell 3 row", 1, temp.getRow());
      assertEquals("Bad cell 3 col", 2, temp.getColumn());

      temp = path.get(4);
      assertEquals("Bad cell 4 row", 2, temp.getRow());
      assertEquals("Bad cell 4 col", 2, temp.getColumn());
   }
}
