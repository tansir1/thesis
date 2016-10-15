package thesis.cli;

import thesis.core.EntityTypeCfgs;
import thesis.core.SimModel;
import thesis.core.StatResults;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.SimModelConfig;

public class ThesisCLI
{
   private SimModel sim;

   public ThesisCLI()
   {

   }

   public void resetNewSim(SimModelConfig simCfg, WorldConfig worldCfg, EntityTypeCfgs entityTypes)
   {
      sim = new SimModel();
      sim.reset(simCfg.getRandomSeed(), worldCfg, entityTypes, simCfg.getCommsRngPercent(),
            simCfg.getCommsRelayProbability(), simCfg.getBeliefDecayRate(), simCfg.getMinWorldClearUncert());
   }

   public StatResults runSim()
   {
      while(!sim.stepSimulation())
      {
         ;
      }
      return sim.getResults();
   }
}
