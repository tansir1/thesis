package thesis.core.world;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.CellCoordinate;
import thesis.core.common.WorldCoordinate;

public class WorldGISTests
{

   @Test
   public void testCoordinateConversions()
   {
      final double oneHundredKM = 100000;

      //100km x 100km world, 10x10 grid, each cell should be 10km x 10km
      WorldGIS testMe = new WorldGIS();
      testMe.reset(oneHundredKM, oneHundredKM, 10, 10);

      final double fifteenKM = 15000;
      final double thirtyFiveKM = 35000;

      WorldCoordinate wc = new WorldCoordinate(fifteenKM, thirtyFiveKM);
      CellCoordinate cc = testMe.convertWorldToCell(wc);
      //35 km is in middle of column 4, using zero based indexing should be index 3
      assertEquals("Invalid world x to cell x conversion.", 3, cc.getColumn());
      //15 km is in middle of row 2, using zero based indexing should be index 1
      assertEquals("Invalid world y to cell y conversion.", 1, cc.getRow());

      cc = new CellCoordinate(6, 2);
      wc = testMe.convertCellToWorld(cc);

      final double twentyFiveKM = 25000;
      final double sixtyFiveKM = 65000;

      //Column 2 should be 20-0km range, middle of that is 25km
      assertEquals("Invalid cell row to world conversion.", twentyFiveKM, wc.getEast(), 0.000001);
      //Row 6 should be 60-70km range, middle of that is 65km
      assertEquals("Invalid cell row to world conversion.", sixtyFiveKM, wc.getNorth(), 0.000001);
   }
}
