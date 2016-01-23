package thesis.core.experimental;

import java.util.List;

import thesis.core.common.CellCoordinate;
import thesis.core.targets.TargetMgr;

public class SensorScan
{
   private PayloadProbs pyldProbs;
   private TargetMgr tgtMgr;

   public SensorScan(PayloadProbs pyldProbs, TargetMgr tgtMgr)
   {
      this.pyldProbs = pyldProbs;
      this.tgtMgr = tgtMgr;
   }

   public void simulateScan(WorldBelief belief, int snsrType, List<CellCoordinate> snsrFOV)
   {
      //query tgt truth in the cells
   }
}
