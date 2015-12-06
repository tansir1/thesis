package thesis.core.world;

import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;

public class RenderOptions
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

   private BitSet options;

   public enum RenderOption
   {
      Graticule(0),
      Roads(1),
      Havens(2),
      Targets(3),
      UAVs(4),
      UavHistoryTrail(5),
      SensorFOV(6);

      private int index;
      private RenderOption(int index)
      {
         this.index = index;
      }
   }

   /**
    *Initialize an option set with the default options.
    */
   public RenderOptions()
   {
      options = new BitSet();
      enableDefaultOptions();
   }

   public void copy(RenderOptions copy)
   {
      this.options.clear();
      this.options.or(copy.options);
   }

   public void enableDefaultOptions()
   {
      options.set(RenderOption.Graticule.index);
      options.set(RenderOption.Roads.index);
      options.set(RenderOption.Havens.index);
      options.set(RenderOption.Targets.index);
      options.set(RenderOption.UAVs.index);
      options.set(RenderOption.UavHistoryTrail.index);
   }

   public void clearAllOptions()
   {
      logger.debug("Cleared all render options");
      options.clear();
   }

   public void setOption(final RenderOption opt)
   {
      logger.debug("Set render option: {}", opt);
      options.set(opt.index);
   }

   public void clearOption(final RenderOption opt)
   {
      logger.debug("Cleared render option: {}", opt);
      options.clear(opt.index);
   }

   public boolean isOptionEnabled(final RenderOption opt)
   {
      return options.get(opt.index);
   }
}
