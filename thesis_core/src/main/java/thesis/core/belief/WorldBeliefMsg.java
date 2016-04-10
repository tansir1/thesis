package thesis.core.belief;

import thesis.core.uav.comms.Message;

public class WorldBeliefMsg extends Message
{
   private WorldBelief belief;

   public WorldBeliefMsg(WorldBelief belief)
   {
      super(MsgType.WorldBelief);
      this.belief = belief;
   }

   public WorldBelief getBelief()
   {
      return belief;
   }

   @Override
   protected Message cloneMsgSpecificData()
   {
      //No need for a deep-copy/clone since receivers of this message will
      //use this data in a read-only fashion within the same process
      WorldBeliefMsg clone = new WorldBeliefMsg(belief);
      return clone;
   }
}
