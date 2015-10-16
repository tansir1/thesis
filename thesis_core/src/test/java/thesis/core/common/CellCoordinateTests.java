package thesis.core.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.CellCoordinate;

public class CellCoordinateTests
{

   @Test
   public void basicAccessorsAndMutators()
   {
      CellCoordinate testMe = new CellCoordinate();

      assertEquals("Row did not initialize to zero.", 0, testMe.getRow());
      assertEquals("Column did not initialize to zero.", 0, testMe.getColumn());

      int row = 43;
      int col = 123;
      testMe.setRow(row);
      testMe.setColumn(col);
      
      assertEquals("Row did not set to value.", row, testMe.getRow());
      assertEquals("Column did not set to value.", col, testMe.getColumn());
   }
   
   @Test
   public void advancedFunctions()
   {
      CellCoordinate testMe = new CellCoordinate();

      int row = 843;
      int col = 143;
      
      testMe.setCoordinate(row, col);
      assertEquals("Row did not set to value.", row, testMe.getRow());
      assertEquals("Column did not set to value.", col, testMe.getColumn());
      
      int delRow = 3;
      int delCol = -5;
      testMe.translate(delRow, delCol);
      
      assertEquals("Row did not translate.", row + delRow, testMe.getRow());
      assertEquals("Column did not translate.", col + delCol, testMe.getColumn());
   }
}
