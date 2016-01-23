package thesis.core.sensors;

import java.util.List;

import thesis.core.common.CellCoordinate;
import thesis.core.experimental.PayloadProbs;
import thesis.core.experimental.WorldBelief;
import thesis.core.targets.Target;
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
      final int NUM_COORDS = snsrFOV.size();
      List<Target> tgtTruth = null;

      for(int i=0; i<NUM_COORDS; ++i)
      {
         tgtTruth = tgtMgr.getTargetsInRegion(snsrFOV.get(i));
      }
   }
}
