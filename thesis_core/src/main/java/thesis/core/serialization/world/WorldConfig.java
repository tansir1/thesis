package thesis.core.serialization.world;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.RoadNetwork;
import thesis.core.common.WorldCoordinate;

/**
 * Container for all of the configuration parameters necessary to initialize a
 * world model.
 */
public class WorldConfig
{
   /**
    * Meters
    */
   private double width;
   /**
    * Meters
    */
   private double height;

   private int numRows;
   private int numCols;

   private RoadNetwork roadNet;

   private List<WorldCoordinate> havens;

   public List<TargetEntityConfig> targetCfgs;

   public List<UAVEntityConfig> uavCfgs;

   public WorldConfig()
   {
      this.width = 0;
      this.height = 0;

      havens = new ArrayList<WorldCoordinate>();
      targetCfgs = new ArrayList<TargetEntityConfig>();
      uavCfgs = new ArrayList<UAVEntityConfig>();
      roadNet = new RoadNetwork();
   }

   /**
    * @return Width of the world in meters.
    */
   public double getWorldWidth()
   {
      return width;
   }

   /**
    * @param width Meters
    */
   public void setWorldWidth(double width)
   {
      this.width = width;
   }

   /**
    * @return Height of the world in meters.
    */
   public double getWorldHeight()
   {
      return height;
   }

   /**
    * @param width Meters
    */
   public void setWorldHeight(double height)
   {
      this.height = height;
   }

   public int getNumColumns()
   {
      return numCols;
   }

   /**
    * @return The maximum distance between the farthest points in the world in meters.
    */
   public double getMaxWorldDistance()
   {
      WorldCoordinate origin = new WorldCoordinate();
      WorldCoordinate maxWidthHeight = new WorldCoordinate(height, width);
      return origin.distanceTo(maxWidthHeight);
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

   public RoadNetwork getRoadNetwork()
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
