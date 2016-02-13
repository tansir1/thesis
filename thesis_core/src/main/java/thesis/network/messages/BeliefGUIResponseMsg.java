package thesis.network.messages;

import java.nio.ByteBuffer;

import thesis.core.belief.CellBelief;
import thesis.core.belief.WorldBelief;

public class BeliefGUIResponseMsg extends InfrastructureMsg
{
   private WorldBelief belief;

   public BeliefGUIResponseMsg()
   {
      super(InfrastructureMsgType.BeliefGUIResponse);
   }

   public void setWorldBelief(WorldBelief wb)
   {
      this.belief = wb;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      final int numRows = belief.getNumRows();
      final int numCols = belief.getNumCols();
      final int numTgtTypes = belief.getNumTgtTypes();

      buf.putInt(numRows);
      buf.putInt(numCols);
      buf.putShort((short) numTgtTypes);
      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            CellBelief cb = belief.getCellBelief(i, j);
            for(int k=0; k<numTgtTypes; ++k)
            {
               byte prob = (byte)(cb.getTargetProb(k) * 100);
               buf.put(prob);
            }
         }
      }
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      final int numRows = buf.getInt();
      final int numCols = buf.getInt();
      final int numTgtTypes = buf.getShort();
      belief = new WorldBelief(numRows, numCols, numTgtTypes);

      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            CellBelief cb = belief.getCellBelief(i, j);
            for(int k=0; k<numTgtTypes; ++k)
            {
               double prob = buf.get();
               prob *= 0.01;//Convert from whole number back to rational number
               //Don't care about hdg and time on the GUI
               cb.updateTargetEstimates(k, prob, 0, 0);
            }
         }
      }
   }

   @Override
   public long getEncodedSize()
   {
      final int numRows = belief.getNumRows();
      final int numCols = belief.getNumCols();
      final int numTgtTypes = belief.getNumTgtTypes();

      return numRows * numCols * numTgtTypes;
   }

}
