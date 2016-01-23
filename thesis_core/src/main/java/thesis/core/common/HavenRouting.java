package thesis.core.common;

import java.util.List;
import java.util.Random;

import thesis.core.world.WorldGIS;

public class HavenRouting
{
   private RoadNetwork roadNet;
   private WorldGIS worldGIS;
   private List<CellCoordinate> havens;
   private Random randGen;

   public HavenRouting(RoadNetwork roadNet, WorldGIS worldGIS, List<CellCoordinate> havens, Random randGen)
   {
      this.roadNet = roadNet;
      this.worldGIS = worldGIS;
      this.havens = havens;
      this.randGen = randGen;
   }

   public void selectNewHavenDestination(WorldCoordinate curPos, WorldCoordinate destination,
         List<WorldCoordinate> path)
   {
      CellCoordinate start = worldGIS.convertWorldToCell(curPos);
      CellCoordinate end = null;
      int numHavens = havens.size();
      do
      {
         end = havens.get(randGen.nextInt(numHavens));
      } while (end.equals(start));

      //Copy destination to passed in reference
      destination.setCoordinate(worldGIS.convertCellToWorld(end));

      //Convert cell path to world coordinate path and copy to passed in reference
      path.clear();
      List<CellCoordinate> cellPath = roadNet.findPath(start, end);
      for(CellCoordinate cell : cellPath)
      {
         path.add(worldGIS.convertCellToWorld(cell));
      }
   }
}
