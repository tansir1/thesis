package thesis.core.experimental;

import java.util.Random;

import thesis.core.targets.TargetMgr;

public class GoodTest
{

   public static void main(String[] args)
   {
      int numRows = 2;
      int numCols = 2;
      int numTgtTypes = 2;
      int numSnsrTypes = 2;
      int numWpnTypes = 2;
      Random randGen = new Random();

      WorldBelief wb = new WorldBelief(numRows, numCols, numTgtTypes);
      PayloadProbs pyldProb = new PayloadProbs(numSnsrTypes, numWpnTypes, numTgtTypes);
      TargetTypeConfigs tgtTypeCfgs = new TargetTypeConfigs(numTgtTypes);

      TargetMgr tgtMgr = new TargetMgr();
      tgtMgr.reset(tgtTypeCfgs, worldCfg, randGen);
   }

}
