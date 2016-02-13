package thesis.core.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import thesis.core.common.CellCoordinate;
import thesis.core.common.Rectangle;
import thesis.core.common.WorldCoordinate;

public class WorldGISTests
{

   @Test
   public void testCoordinateConversions()
   {
      final double oneHundredKM = 100000;

      // 100km x 100km world, 10x10 grid, each cell should be 10km x 10km
      WorldGIS testMe = new WorldGIS();
      testMe.reset(oneHundredKM, oneHundredKM, 10, 10);

      final double fifteenKM = 15000;
      final double thirtyFiveKM = 35000;

      WorldCoordinate wc = new WorldCoordinate(fifteenKM, thirtyFiveKM);
      CellCoordinate cc = testMe.convertWorldToCell(wc);
      // 35 km is in middle of column 4, using zero based indexing should be
      // index 3
      assertEquals("Invalid world x to cell x conversion.", 3, cc.getColumn());
      // 15 km is in middle of row 2, using zero based indexing should be index
      // 1
      assertEquals("Invalid world y to cell y conversion.", 1, cc.getRow());

      cc = new CellCoordinate(6, 2);
      wc = testMe.convertCellToWorld(cc);

      final double twentyFiveKM = 25000;
      final double sixtyFiveKM = 65000;

      // Column 2 should be 20-0km range, middle of that is 25km
      assertEquals("Invalid cell row to world conversion.", twentyFiveKM, wc.getEast(), 0.000001);
      // Row 6 should be 60-70km range, middle of that is 65km
      assertEquals("Invalid cell row to world conversion.", sixtyFiveKM, wc.getNorth(), 0.000001);
   }

   @Test
   public void cellsInRect()
   {
      // 100m x 100m world, 10x10 grid, each cell should be 10m x 10m
      WorldGIS testMe = new WorldGIS();
      testMe.reset(100, 100, 10, 10);

      final int minRow = 1;
      final int maxRow = 3;
      final int minCol = 1;
      final int maxCol = 3;

      Rectangle rect = new Rectangle();
      testMe.convertCellToWorld(maxRow, minCol, rect.getTopLeft());
      testMe.convertCellToWorld(maxRow, maxCol, rect.getTopRight());
      testMe.convertCellToWorld(minRow, minCol, rect.getBottomLeft());
      testMe.convertCellToWorld(minRow, maxCol, rect.getBottomRight());

//      rect.getTopLeft().setCoordinate(maxRow*10+1, minCol*10+1);
//      rect.getTopRight().setCoordinate(maxRow*10+1, maxCol*10+1);
//      rect.getBottomLeft().setCoordinate(minRow*10+1, minCol*10+1);
//      rect.getBottomRight().setCoordinate(minRow*10+1, maxCol*10+1);
      rect.convertToCanonicalForm();

      List<CellCoordinate> trueCellsInRect = new ArrayList<CellCoordinate>();
      for (int row = minRow; row <= maxRow; ++row)
      {
         for (int col = minCol; col <= maxCol; ++col)
         {
            trueCellsInRect.add(new CellCoordinate(row, col));
         }
      }

      List<CellCoordinate> cellsInRect = testMe.getCellsInRectangle(rect);
      trueCellsInRect.removeAll(cellsInRect);
      assertTrue("Did not find all cells in FOV.", trueCellsInRect.isEmpty());
   }
}
