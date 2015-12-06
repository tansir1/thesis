package thesis.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import thesis.core.SimModel;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.common.graph.DirectedEdge;
import thesis.core.common.graph.Graph;
import thesis.core.common.graph.Vertex;

public class Target
{
   private static final double PROB_TO_IGNORE_HAVEN = 0.2;
   private final TargetType type;
   private final WorldPose pose;
   private final Graph<WorldCoordinate> roadNet;
   private final List<WorldCoordinate> havens;
   private final Random randGen;
   private final double worldW, worldH;

   /**
    * The coordinate of the next location to traverse to in mobile targets
    */
   private WorldCoordinate intermediateCoordDest;

   /**
    * The path the target will follow along the road network to its final
    * destination.
    */
   private List<DirectedEdge<WorldCoordinate>> path;

   public Target(TargetType type, Graph<WorldCoordinate> roadNet, List<WorldCoordinate> havens, Random randGen,
         double worldW, double worldH)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      if (roadNet == null)
      {
         throw new NullPointerException("Road network cannot be null.");
      }

      if (havens == null)
      {
         throw new NullPointerException("Havens cannot be null.");
      }

      if (randGen == null)
      {
         throw new NullPointerException("Random generator cannot be null.");
      }

      this.type = type;
      this.roadNet = roadNet;
      this.havens = havens;
      this.randGen = randGen;
      this.worldH = worldH;
      this.worldW = worldW;

      pose = new WorldPose();
      // This arraylist gets garbage collected after the first simulation step
      // It's only initialized to something to prevent having 'if(path == null)'
      // checks everywhere. It's not actually used for anything useful.
      path = new ArrayList<DirectedEdge<WorldCoordinate>>();
   }

   public TargetType getType()
   {
      return type;
   }

   public WorldCoordinate getCoordinate()
   {
      return pose.getCoordinate();
   }

   public double getHeading()
   {
      return pose.getHeading();
   }

   public void setHeading(double hdg)
   {
      pose.setHeading(hdg);
   }

   /**
    * Step the simulation forward by {@link SimModel#SIM_STEP_RATE_MS} amount of
    * time.
    */
   public void stepSimulation()
   {
      if (type.isMobile())
      {
         if (isAtDestination())
         {
            selectNewDestination();

            double newHdg = pose.getCoordinate().bearingTo(intermediateCoordDest);
            pose.setHeading(newHdg);
         }

         double deltaSeconds = SimTime.SIM_STEP_RATE_MS / 1000.0;
         double spd = type.getMaxSpeed();

         // east distance = time * speed * east component
         double easting = deltaSeconds * spd * Math.cos(Math.toRadians(pose.getHeading()));
         // north distance = time * speed * north component
         double northing = deltaSeconds * spd * Math.sin(Math.toRadians(pose.getHeading()));

         pose.getCoordinate().translateCart(northing, easting);
      }

   }

   private boolean isAtDestination()
   {
      boolean arrived = false;

      if (intermediateCoordDest == null)
      {
         arrived = true;
      }
      else
         if (pose.getCoordinate().distanceTo(intermediateCoordDest) < type.getMaxSpeed())
      {
         // If we're within one frame of the destination
         arrived = true;
      }

      return arrived;
   }

   private void selectNewDestination()
   {
      if (path.isEmpty())
      {
         Vertex<WorldCoordinate> start = findNearestVertex(pose.getCoordinate());

         // Select a new haven or road intersection if no havens are present
         Vertex<WorldCoordinate> vert = selectNewHavenOrIntersection(start);
         if (vert != null)
         {
            path = roadNet.findPath(start, vert);
         }
      }

      // Path should be filled again if havens and/or roads exist
      if (!path.isEmpty())
      {
         DirectedEdge<WorldCoordinate> edge = path.remove(0);
         intermediateCoordDest = edge.getEndVertex().getUserData();
      }
      else// No havens or roads in simulation, pick a random coordinate
      {
         intermediateCoordDest.setCoordinate(randGen.nextDouble() * worldH,
               randGen.nextDouble() * worldW);
      }
   }

   private Vertex<WorldCoordinate> selectNewHavenOrIntersection(Vertex<WorldCoordinate> current)
   {
      boolean forceHavenIgnore = false;
      if(randGen.nextDouble() < PROB_TO_IGNORE_HAVEN)
      {
         forceHavenIgnore = true;
      }

      Vertex<WorldCoordinate> vert = null;
      if (havens.size() > 1 && !forceHavenIgnore)
      {
         do
         {
            // Select a new haven
            vert = roadNet.getVertexByData(havens.get(randGen.nextInt(havens.size())));
         } while (vert.getID() == current.getID());
      }
      else if(havens.size() == 1 && !forceHavenIgnore)
      {
         vert = roadNet.getVertexByData(havens.get(0));
         if(vert.getID() == current.getID())
         {
            //Force another type of selection so the target doesn't go
            //from the haven to the same haven
            vert = null;
         }
      }

      if(vert == null && roadNet.getNumVertices() > 0)
      {
         // No havens so pick a random road intersection
         vert = roadNet.getVertexByID(randGen.nextInt(roadNet.getNumVertices()));
      }

      return vert;
   }

   private Vertex<WorldCoordinate> findNearestVertex(WorldCoordinate nearestTo)
   {
      Vertex<WorldCoordinate> nearestVert = null;
      double distToNearestVert = 0;
      for (Vertex<WorldCoordinate> vert : roadNet.getVertices())
      {
         double distToIterateVert = nearestTo.distanceTo(vert.getUserData());
         distToIterateVert = Math.abs(distToIterateVert);

         if (nearestVert == null)
         {
            nearestVert = vert;
            distToNearestVert = distToIterateVert;
         }
         else if (distToIterateVert < distToNearestVert)
         {
            nearestVert = vert;
            distToNearestVert = distToIterateVert;
         }
      }
      return nearestVert;
   }
}
