package thesis.core.entities.uav.belief;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Overall container for everything that a UAV thinks it knows about the world.
 */
public class BeliefState
{
   private List<TargetBelief> targets;
   private Map<Integer, OtherUAVBelief> team;

   public BeliefState()
   {
      targets = new ArrayList<TargetBelief>();
      team = new HashMap<Integer, OtherUAVBelief>();
   }

   /**
    * Get all targets the UAV thinks it knows about.
    *
    * @return
    */
   public List<TargetBelief> getTargets()
   {
      return Collections.unmodifiableList(targets);
   }

   /**
    * Add a new target to the UAV's belief state.
    *
    * @param tb
    *           The new target to remember.
    */
   public void addTarget(TargetBelief tb)
   {
      targets.add(tb);
   }

   /**
    * Remove a target from this UAV's belief state.
    *
    * @param tb
    *           The target to remove.
    */
   public void removeTarget(TargetBelief tb)
   {
      targets.remove(tb);
   }

   /**
    * Add another UAV teammate to this UAV's belief.
    *
    * @param oub
    *           The teammate to add.
    */
   public void addUAV(OtherUAVBelief oub)
   {
      team.put(oub.getUavID(), oub);
   }

   /**
    * Get the belief data about a teammate UAV.
    *
    * @param id
    *           The ID of the teammate to retrieve.
    * @return The requested belief information or null if no such data exists.
    */
   public OtherUAVBelief getOtherUAV(int id)
   {
      return team.get(id);
   }

   /**
    * Get all teammate UAVs that this UAV knows about.
    *
    * @return
    */
   public Collection<OtherUAVBelief> getOtherUAVs()
   {
      return team.values();
   }
}
