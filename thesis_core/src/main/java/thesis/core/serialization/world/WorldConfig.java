package thesis.core.serialization.world;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.Graph;

/**
 * Container for all of the configuration parameters necessary to initialize a
 * world model.
 */
public class WorldConfig
{
   private Distance width;
   private Distance height;

   private int numRows;
   private int numCols;

   private Graph<WorldCoordinate> roadNet;

   private List<WorldCoordinate> havens;

   public List<TargetEntityConfig> targetCfgs;

   public List<UAVEntityConfig> uavCfgs;

   public WorldConfig()
   {
      this.width = new Distance();
      this.height = new Distance();

      havens = new ArrayList<WorldCoordinate>();
      targetCfgs = new ArrayList<TargetEntityConfig>();
      uavCfgs = new ArrayList<UAVEntityConfig>();
      roadNet = new Graph<WorldCoordinate>();
   }

   public Distance getWorldWidth()
   {
      return width;
   }

   public Distance getWorldHeight()
   {
      return height;
   }

   public int getNumColumns()
   {
      return numCols;
   }

   public Distance getMaxWorldDistance()
   {
      Distance max = new Distance();
      double maxM = height.asMeters() * height.asMeters() + width.asMeters() * width.asMeters();
      max.setAsMeters(maxM);
      return max;
   }

   public void setNumColumns(int numCols)
   {
      if (numCols < 0)
      {
         throw new IllegalArgumentException("Number of columns in the world cannot be less than 0.");
      }
      this.numCols = numCols;
   }

   public int getNumRows()
   {
      return numRows;
   }

   public void setNumRows(int numRows)
   {
      if (numRows < 0)
      {
         throw new IllegalArgumentException("Number of rows in the world cannot be less than 0.");
      }
      this.numRows = numRows;
   }

   public Graph<WorldCoordinate> getRoadNetwork()
   {
      return roadNet;
   }

   public List<WorldCoordinate> getHavens()
   {
      return havens;
   }

   @Override
   public String toString()
   {
      return "WorldConfig [width=" + width + ", height=" + height + ", numRows=" + numRows + ", numCols=" + numCols
            + "]";
   }

}
