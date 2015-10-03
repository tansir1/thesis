package thesis.core.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class WorldTests
{

   @Test
   public void testCoordinateConversions()
   {
      //100km x 100km world, 10x10 grid, each cell should be 10km x 10km
      World testMe = new World(100, 100, 10, 10);
      
      WorldCoordinate wc = new WorldCoordinate(15, 35);
      CellCoordinate cc = testMe.convertWorldToCell(wc);
      //35 km is in middle of column 4, using zero based indexing should be index 3
      assertEquals("Invalid world x to cell x conversion.", 3, cc.getColumn());
      //15 km is in middle of row 2, using zero based indexing should be index 1
      assertEquals("Invalid world y to cell y conversion.", 1, cc.getRow());

      final double EPS_THRESHOLD = 0.00001;
      cc = new CellCoordinate(6, 2);
      wc = testMe.convertCellToWorld(cc);
      //Column 2 should be 20-0km range, middle of that is 25km
      assertEquals("Invalid cell row to world conversion.", 25.0, wc.getEast(), EPS_THRESHOLD);
      //Row 6 should be 60-70km range, middle of that is 65km
      assertEquals("Invalid cell row to world conversion.", 65.0, wc.getNorth(), EPS_THRESHOLD);      
   }
}
