package thesis.core.belief;

import thesis.core.common.CellCoordinate;
import thesis.core.common.SimTime;
import thesis.core.uav.comms.Message;
import thesis.core.uav.comms.UAVComms;
import thesis.core.uav.comms.WorldBeliefMsg;

public class WorldBelief
{
   /**
    * When this amount of simulation time elapses the UAV will broadcast its
    * current belief state.
    */
   private static double BELIEF_BROADCAST_RATE_MS = 1000;//Broadcast at 1hz

   private double lastBeliefBroadcastTimeAccumulator;

   private CellBelief cells[][];

   public WorldBelief(int numRows, int numCols, int numTgtTypes)
   {
      cells = new CellBelief[numRows][numCols];
      for(int i=0; i<numRows; ++i)
      {
         for(int j=0; j<numCols; ++j)
         {
            cells[i][j] = new CellBelief(numTgtTypes);
         }
      }
   }

   public void stepSimulation(UAVComms comms)
   {
      lastBeliefBroadcastTimeAccumulator += SimTime.SIM_STEP_RATE_MS;
      if (lastBeliefBroadcastTimeAccumulator > BELIEF_BROADCAST_RATE_MS)
      {
         lastBeliefBroadcastTimeAccumulator = 0;
         WorldBeliefMsg msg = new WorldBeliefMsg(this);
         comms.transmit(msg, Message.BROADCAST_ID);
      }
   }

   public void mergeBelief(final WorldBelief other)
   {
      final int numRows = cells.length;
      final int numCols = cells[0].length;

      for(int i=0; i<numRows; ++i)
      {
         for(int j=0; j<numCols; ++j)
         {
            cells[i][j].mergeBelief(other.cells[i][j]);
         }
      }
   }

   public CellBelief getCellBelief(CellCoordinate cell)
   {
      return getCellBelief(cell.getRow(), cell.getColumn());
   }

   public CellBelief getCellBelief(int row, int col)
   {
      return cells[row][col];
   }

}