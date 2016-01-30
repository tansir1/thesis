package thesis.core.world;

import thesis.core.common.CellCoordinate;

public class Havens
{
   private CellCoordinate[] locations;

   public Havens()
   {

   }

   public void reset(int numHavens)
   {
      locations = new CellCoordinate[numHavens];
      for (int i = 0; i < numHavens; ++i)
      {
         locations[i] = new CellCoordinate();
      }
   }

   public void copy(Havens copy)
   {
      int numHavens = copy.getNumHavens();
      this.reset(copy.locations.length);

      for (int i = 0; i < numHavens; ++i)
      {
         locations[i] = new CellCoordinate(copy.locations[i]);
      }
   }

   public int getNumHavens()
   {
      return locations.length;
   }

   public CellCoordinate getHavenByIndx(int indx)
   {
      return locations[indx];
   }

   public void setHavenByIndx(int indx, int row, int col)
   {
      locations[indx].setCoordinate(row, col);
   }

   public void setHavenByIndx(int indx, CellCoordinate cell)
   {
      locations[indx].setCoordinate(cell);
   }
}
