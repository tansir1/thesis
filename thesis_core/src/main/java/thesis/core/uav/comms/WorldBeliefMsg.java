package thesis.core.uav.comms;

import thesis.core.entities.belief.BeliefState;

public class BeliefStateMsg extends Message
{
   private BeliefState belief;

   public BeliefStateMsg(BeliefState belief)
   {
      super(MsgType.BeliefState);
      this.belief = belief;
   }

   public BeliefState getBelief()
   {
      return belief;
   }

   @Override
   protected Message cloneMsgSpecificData()
   {
      //No need for a deep-copy/clone since receivers of this message will
      //use this data in a read-only fashion within the same process
      BeliefStateMsg clone = new BeliefStateMsg(belief);
      return clone;
   }
}
