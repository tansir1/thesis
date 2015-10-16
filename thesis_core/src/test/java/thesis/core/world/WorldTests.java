package thesis.core.world;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import thesis.core.common.CellCoordinate;
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;

public class WorldTests
{

   @Test
   public void testCoordinateConversions()
   {
      final Distance oneHundredKM = new Distance();
      oneHundredKM.setAsKilometers(100);

      //100km x 100km world, 10x10 grid, each cell should be 10km x 10km
      World testMe = new World(oneHundredKM, oneHundredKM, 10, 10, new Random());
      
      final Distance fifteenKM = new Distance();
      fifteenKM.setAsKilometers(15);
      final Distance thirtyFiveKM = new Distance();
      thirtyFiveKM.setAsKilometers(35);

      WorldCoordinate wc = new WorldCoordinate(fifteenKM, thirtyFiveKM);
      CellCoordinate cc = testMe.convertWorldToCell(wc);
      //35 km is in middle of column 4, using zero based indexing should be index 3
      assertEquals("Invalid world x to cell x conversion.", 3, cc.getColumn());
      //15 km is in middle of row 2, using zero based indexing should be index 1
      assertEquals("Invalid world y to cell y conversion.", 1, cc.getRow());

      cc = new CellCoordinate(6, 2);
      wc = testMe.convertCellToWorld(cc);

      final Distance twentyFiveKM = new Distance();
      twentyFiveKM.setAsKilometers(25);
      final Distance sixtyFiveKM = new Distance();
      sixtyFiveKM.setAsKilometers(65);

      //Column 2 should be 20-0km range, middle of that is 25km
      assertEquals("Invalid cell row to world conversion.", twentyFiveKM, wc.getEast());
      //Row 6 should be 60-70km range, middle of that is 65km
      assertEquals("Invalid cell row to world conversion.", sixtyFiveKM, wc.getNorth());      
   }
}
