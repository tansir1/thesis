package thesis.core.belief;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
   private static double BELIEF_BROADCAST_RATE_MS = 1000;// Broadcast at 1hz

   /**
    * Coefficient to use in the alpha filter for merging target probability
    * data between two world beliefs.
    */
   public static double NEWER_TGT_ALPHA = 0.5;// Default to 0.5

   private double lastBeliefBroadcastTimeAccumulator;

   private CellBelief cells[][];

   private List<TargetBelief> tgtBeliefs;


   private int numTgtTypes;

   public WorldBelief(int numRows, int numCols, int numTgtTypes, double beliefDecayRate)
   {
      this.numTgtTypes = numTgtTypes;

      tgtBeliefs = new ArrayList<TargetBelief>();

      cells = new CellBelief[numRows][numCols];
      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            cells[i][j] = new CellBelief(i, j, numTgtTypes, beliefDecayRate);
         }
      }
   }

   public void stepSimulation(UAVComms comms)
   {
      final int numRows = cells.length;
      final int numCols = cells[0].length;

      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            cells[i][j].stepSimulation();
         }
      }

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

      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            cells[i][j].mergeBelief(other.cells[i][j]);
         }
      }

      mergeTargetBeliefs(other);
   }

   public CellBelief getCellBelief(CellCoordinate cell)
   {
      return getCellBelief(cell.getRow(), cell.getColumn());
   }

   public CellBelief getCellBelief(int row, int col)
   {
      return cells[row][col];
   }

   public int getNumRows()
   {
      return cells.length;
   }

   public int getNumCols()
   {
      return cells[0].length;
   }


   public TargetBelief getTargetBelief(int tgtID)
   {
      TargetBelief tgtBelief = null;
      boolean tgtFound = false;

      Iterator<TargetBelief> itr = tgtBeliefs.iterator();
      while (itr.hasNext() && !tgtFound)
      {
         tgtBelief = itr.next();
         if (tgtBelief.getTrueTargetID() == tgtID)
         {
            tgtFound = true;
         }
      }

      if (tgtBelief == null)
      {
         tgtBelief = new TargetBelief(numTgtTypes, tgtID);
         tgtBeliefs.add(tgtBelief);
      }
      return tgtBelief;
   }

   public boolean hasDetectedTarget(int tgtID)
   {
      boolean tgtFound = false;
      Iterator<TargetBelief> itr = tgtBeliefs.iterator();

      while (itr.hasNext() && !tgtFound)
      {
         if (itr.next().getTrueTargetID() == tgtID)
         {
            tgtFound = true;
         }
      }
      return tgtFound;
   }

   public void removeTarget(int tgtID)
   {
      boolean tgtFound = false;
      Iterator<TargetBelief> itr = tgtBeliefs.iterator();
      TargetBelief tgtBelief = null;

      while (itr.hasNext() && !tgtFound)
      {
         tgtBelief = itr.next();
         if (tgtBelief.getTrueTargetID() == tgtID)
         {
            itr.remove();
            tgtFound = true;
         }
      }
   }

   public int getNumTargetBeliefs()
   {
      return tgtBeliefs.size();
   }

   private void mergeTargetBeliefs(WorldBelief other)
   {
      List<TargetBelief> otherBeliefs = new ArrayList<TargetBelief>(other.tgtBeliefs);
      Iterator<TargetBelief> itr = otherBeliefs.iterator();

      while(itr.hasNext())
      {
         TargetBelief otherBelief = itr.next();
         if(hasDetectedTarget(otherBelief.getTrueTargetID()))
         {
            TargetBelief myBelief = getTargetBelief(otherBelief.getTrueTargetID());
            myBelief.merge(otherBelief, NEWER_TGT_ALPHA);
         }
         else
         {
            //Other belief has information on new targets
            tgtBeliefs.add(new TargetBelief(otherBelief));
         }
      }
   }
}
