package thesis.network.messages;

import java.nio.ByteBuffer;

import thesis.core.common.CellCoordinate;
import thesis.core.world.Havens;
import thesis.core.world.World;

@Deprecated
public class WorldCfgMsg extends InfrastructureMsg
{
   private World world;
   public WorldCfgMsg()
   {
      super(InfrastructureMsgType.WorldCfg);
      world = new World();
   }

   public void setWorld(World world)
   {
      this.world.copy(world);
   }

   public World getWorld()
   {
      return world;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      buf.putInt(world.getWorldGIS().getColumnCount());
      buf.putInt(world.getWorldGIS().getRowCount());
      buf.putDouble(world.getWorldGIS().getHeight());
      buf.putDouble(world.getWorldGIS().getWidth());

      Havens havens = world.getHavens();
      int numHavens = havens.getNumHavens();

      buf.putInt(numHavens);
      for(int i=0; i<numHavens; ++i)
      {
         CellCoordinate cell = havens.getHavenByIndx(i);
         buf.putInt(cell.getColumn());
         buf.putInt(cell.getRow());
      }

      int numRoads = world.getRoadNetwork().getNumTraversable();
      buf.putInt(numRoads);
      for(CellCoordinate cell : world.getRoadNetwork().getTraversableCells())
      {
         buf.putInt(cell.getColumn());
         buf.putInt(cell.getRow());
      }
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      int cols = buf.getInt();
      int rows = buf.getInt();
      double height = buf.getDouble();
      double width = buf.getDouble();
      world.getWorldGIS().reset(width, height, rows, cols);

      int numHavens = buf.getInt();
      world.getHavens().reset(numHavens);
      for(int i=0; i<numHavens; ++i)
      {
         int col = buf.getInt();
         int row = buf.getInt();
         world.getHavens().setHavenByIndx(i, row, col);
      }

      int numRoads = buf.getInt();
      world.getRoadNetwork().reset(rows, cols);
      for(int i=0; i<numRoads; ++i)
      {
         int col = buf.getInt();
         int row = buf.getInt();
         world.getRoadNetwork().setTraversable(row, col, true);
      }
   }

   @Override
   public long getEncodedSize()
   {
      long size = 0;

      //world gis
      size += Integer.BYTES * 2;
      size += Double.BYTES * 2;

      //havens
      int numHavens = world.getHavens().getNumHavens();
      size += Integer.BYTES; //numHavens value
      size += (Integer.BYTES * 2) * numHavens;

      //road network
      int numRoads = world.getRoadNetwork().getNumTraversable();
      size += Integer.BYTES; //numRoads value
      size += (Integer.BYTES * 2) * numRoads;

      return size;
   }

}
