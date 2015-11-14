package thesis.core.entities.uav.dubins;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

/**
 * Generates Dubin's Curves for UAVs to follow from waypoint to waypoint.
 *
 *This is a largely a port of the C++ code found here:
 *https://github.com/gieseanw/Dubins
 */
public class DubinsPathGenerator
{

   /**
    * Generate a path from the start to the ending pose with the given
    * constraints.
    *
    * @param minTurnRadius
    *           The minimum radius required for the uav to turn around.
    * @param start
    *           The starting position and orientation of the path.
    * @param end
    *           The ending position and orientation of the path.
    * @return A path satisfying the given constraints.
    */
   public static DubinsPath generate(final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      Circle startLeft = new Circle();
      Circle startRight = new Circle();
      Circle endLeft = new Circle();
      Circle endRight = new Circle();

      double theta = start.getHeading().asRadians();
      theta += Math.PI / 2.0;
      if (theta > Math.PI)
         theta -= 2.0 * Math.PI;

      startLeft.getCenter().getEast().setAsMeters(start.getEast().asMeters() + minTurnRadius.asMeters() * Math.cos(theta));
      startLeft.getCenter().getNorth().setAsMeters(start.getNorth().asMeters() + minTurnRadius.asMeters() * Math.sin(theta));
      startLeft.getRadius().copy(minTurnRadius);

      theta = start.getHeading().asRadians();
      theta -= Math.PI / 2.0;
      if (theta < -Math.PI)
         theta += 2.0 * Math.PI;

      startRight.getCenter().getEast().setAsMeters(start.getEast().asMeters() + minTurnRadius.asMeters() * Math.cos(theta));
      startRight.getCenter().getNorth().setAsMeters(start.getNorth().asMeters() + minTurnRadius.asMeters() * Math.sin(theta));
      startRight.getRadius().copy(minTurnRadius);

      theta = end.getHeading().asRadians();
      theta += Math.PI / 2.0;
      if (theta > Math.PI)
         theta -= 2.0 * Math.PI;

      endLeft.getCenter().getEast().setAsMeters(end.getEast().asMeters() + minTurnRadius.asMeters() * Math.cos(theta));
      endLeft.getCenter().getNorth().setAsMeters(end.getNorth().asMeters() + minTurnRadius.asMeters() * Math.sin(theta));
      endLeft.getRadius().copy(minTurnRadius);

      theta = end.getHeading().asRadians();
      theta -= Math.PI / 2.0;
      if (theta < -Math.PI)
         theta += 2.0 * Math.PI;

      endRight.getCenter().getEast().setAsMeters(end.getEast().asMeters() + minTurnRadius.asMeters() * Math.cos(theta));
      endRight.getCenter().getNorth().setAsMeters(end.getNorth().asMeters() + minTurnRadius.asMeters() * Math.sin(theta));
      endRight.getRadius().copy(minTurnRadius);

      DubinsPath path = genCSCPath(startLeft, startRight, endLeft, endRight, minTurnRadius, start, end);
      DubinsPath temp = genCCCPath(startLeft, startRight, endLeft, endRight, minTurnRadius, start, end);
      if ((temp.getPathType() != PathType.NO_PATH && temp.getPathLength().asMeters() < path.getPathLength().asMeters()) ||
    		  (path.getPathType() == PathType.NO_PATH && temp.getPathType() != PathType.NO_PATH))
      {
         path = temp;
      }

      path.getStartPose().copy(start);
      path.getEndPose().copy(end);

      return path;
   }

   private static DubinsPath genCSCPath(final Circle startLeft, final Circle startRight, final Circle endLeft,
         final Circle endRight, final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      List<PathSegment> rrTangents = computeTangentLines(startRight, endRight);
      List<PathSegment> llTangents = computeTangentLines(startLeft, endLeft);
      List<PathSegment> rlTangents = computeTangentLines(startRight, endLeft);
      List<PathSegment> lrTangents = computeTangentLines(startLeft, endRight);

      DubinsPath path = genRSRPath(rrTangents, startRight, endRight, start, end, minTurnRadius);

      DubinsPath temp = genLSLPath(llTangents, startLeft, endLeft, start, end, minTurnRadius);
      path = shorterValidPath(path, temp);

      temp = genRSLPath(rlTangents, startRight, endLeft, start, end, minTurnRadius);
      path = shorterValidPath(path, temp);

      temp = genLSRPath(lrTangents, startLeft, endRight, start, end, minTurnRadius);
      path = shorterValidPath(path, temp);

      return path;
   }

   private static DubinsPath genCCCPath(final Circle startLeft, final Circle startRight, final Circle endLeft,
         final Circle endRight, final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      // Initialize to an invalid path
      DubinsPath path = new DubinsPath();

      // find the relative angle for L and right
      double theta = 0.0;
      double distance = startRight.getCenter().distanceTo(endRight.getCenter()).asMeters();

      if (distance < (4.0 * minTurnRadius.asMeters()))
      {
         theta = Math.acos(distance / (4.0 * minTurnRadius.asMeters()));

         double delNorth = endRight.getCenter().getNorth().asMeters() - startRight.getCenter().getNorth().asMeters();
         double delEast = endRight.getCenter().getEast().asMeters() - startRight.getCenter().getEast().asMeters();

         theta += Math.atan2(delNorth, delEast);

         path = genRLRPath(theta, startRight, endRight, minTurnRadius, start, end);
      }

      distance = startLeft.getCenter().distanceTo(endLeft.getCenter()).asMeters();
      if (distance < (4.0 * minTurnRadius.asMeters()))
      {
         theta = Math.acos(distance / (4.0 * minTurnRadius.asMeters()));

         double delNorth = endLeft.getCenter().getNorth().asMeters() - startLeft.getCenter().getNorth().asMeters();
         double delEast = endLeft.getCenter().getEast().asMeters() - startLeft.getCenter().getEast().asMeters();

         theta += Math.atan2(delNorth, delEast);

         DubinsPath temp = genLRLPath(theta, startLeft, endLeft, minTurnRadius, start, end);
         path = shorterValidPath(path, temp);
      }
      return path;
   }

   /**
    * Compute all tangent line permutations between the two given circles.
    *
    * @param c1
    * @param c2
    * @return A list of all tangent lines between the two circles.
    */
   private static List<PathSegment> computeTangentLines(Circle c1, Circle c2)
   {
      double x1 = c1.getCenter().getEast().asMeters();
      double y1 = c1.getCenter().getNorth().asMeters();
      double x2 = c2.getCenter().getEast().asMeters();
      double y2 = c2.getCenter().getNorth().asMeters();
      double r1 = c1.getRadius().asMeters();
      double r2 = c2.getRadius().asMeters();

      List<PathSegment> tangents = new ArrayList<PathSegment>();

      double d_sq = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
      if (d_sq < (r1 - r2) * (r1 - r2))
      {
         // we may have a problem, the circles are either intersecting, one is
         // within the other, but still tangent
         // at one point, or one is completely in the other. We only have a
         // problem if one is within the other, but
         // not tangent to it anywhere
         if (d_sq != Math.max(r1, r2) && d_sq < Math.max(r1, r2))
         {
            // "Circles are contained with each other and not tangent, no lines
            // exist.
            return tangents;
         } // else they are intersecting or one is within the other, but still
           // tangent to it
           // in the other two cases, either 1 or 2 external tangent lines
           // remain, but there are no internal tangent
           // lines
      }

      double d = Math.sqrt(d_sq);
      double vx = (x2 - x1) / d;
      double vy = (y2 - y1) / d;
      for (int sign1 = +1; sign1 >= -1; sign1 -= 2)
      {
         double c = (r1 - sign1 * r2) / d;
         if (c * c > 1.0)
            continue; // want to be subtracting small from large, not adding
         double h = Math.sqrt(Math.max(0.0, 1.0 - c * c));

         for (int sign2 = +1; sign2 >= -1; sign2 -= 2)
         {
            double nx = vx * c - sign2 * h * vy;
            double ny = vy * c + sign2 * h * vx;

            PathSegment path = new PathSegment();
            path.getStart().getEast().setAsMeters(x1 + r1 * nx);
            path.getStart().getNorth().setAsMeters(y1 + r1 * ny);
            path.getEnd().getEast().setAsMeters(x2 + sign1 * r2 * nx);
            path.getEnd().getNorth().setAsMeters(y2 + sign1 * r2 * ny);
            tangents.add(path);
         }
      }
      return tangents;
   }

   /**
    * Compute the arc length from lhs to rhs with a circle centered at 'center.'
    *
    * @param center
    * @param lhs
    * @param rhs
    * @param radius
    * @param left
    *           True if this is the length for a left turn.
    * @return The arc length of the path.
    */
   private static Distance computeArcLength(final WorldCoordinate center, final WorldCoordinate lhs,
         final WorldCoordinate rhs, final Distance radius, final boolean left)
   {
      //Angle leftAngle = new Angle();
      //Angle rightAngle = new Angle();
      /*
      Angle leftAngle = center.bearingTo(lhs);
      Angle rightAngle = center.bearingTo(lhs);

      rightAngle.subtract(leftAngle);
      double theta = rightAngle.asRadians();*/

      double x1 = lhs.getEast().asMeters() - center.getEast().asMeters();
      double y1 = lhs.getNorth().asMeters() - center.getNorth().asMeters();
      
      double x2 = rhs.getEast().asMeters() - center.getEast().asMeters();
      double y2 = rhs.getNorth().asMeters() - center.getNorth().asMeters();

      double theta = Math.atan2(y2, x2) - Math.atan2(y1, x1);      
      
      if (theta < 0 && left)
         theta += 2.0 * Math.PI;
      else if (theta > 0 && !left)
         theta -= 2.0 * Math.PI;

      Distance arcLen = new Distance();
      arcLen.copy(radius);
      arcLen.scale(theta);
      if(arcLen.asMeters() < 0)
      {
         arcLen.scale(-1.0);
      }
      return arcLen;
   }

   private static DubinsPath genRSRPath(final List<PathSegment> rrTangents, final Circle startRight,
         final Circle endRight, final WorldPose startPose, final WorldPose endPose, final Distance minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (!rrTangents.isEmpty())
      {
         path.type = PathType.RSR;

         path.getWaypoint1().setCoordinate(rrTangents.get(0).getStart());
         path.getWaypoint2().setCoordinate(rrTangents.get(0).getEnd());

         // tangent pts function returns outer tangents for RR connection first
         Distance arcL1 = computeArcLength(startRight.getCenter(), startPose.getCoordinate(),
               rrTangents.get(0).getStart(), minTurnRadius, false);

         Distance arcL2 = rrTangents.get(0).getStart().distanceTo(rrTangents.get(0).getEnd());

         Distance arcL3 = computeArcLength(endRight.getCenter(), rrTangents.get(0).getEnd(), endPose.getCoordinate(),
               minTurnRadius, false);

         path.segmentLengths[0].copy(arcL1);
         path.segmentLengths[1].copy(arcL2);
         path.segmentLengths[2].copy(arcL3);
      }
      return path;
   }

   private static DubinsPath genLSLPath(final List<PathSegment> llTangents, final Circle startLeft,
         final Circle endLeft, final WorldPose startPose, final WorldPose endPose, final Distance minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (llTangents.size() > 1)
      {
         path.type = PathType.LSL;

         path.getWaypoint1().setCoordinate(llTangents.get(1).getStart());
         path.getWaypoint2().setCoordinate(llTangents.get(1).getEnd());

         // tangent pts function returns outer tangents for LL connection second
         Distance arcL1 = computeArcLength(startLeft.getCenter(), startPose.getCoordinate(),
               llTangents.get(1).getStart(), minTurnRadius, true);
         Distance arcL2 = llTangents.get(1).getStart().distanceTo(llTangents.get(1).getEnd());
         Distance arcL3 = computeArcLength(endLeft.getCenter(), llTangents.get(1).getEnd(), endPose.getCoordinate(),
               minTurnRadius, true);

         path.segmentLengths[0].copy(arcL1);
         path.segmentLengths[1].copy(arcL2);
         path.segmentLengths[2].copy(arcL3);
      }
      return path;
   }

   private static DubinsPath genRSLPath(final List<PathSegment> rlTangents, final Circle startRight,
         final Circle endLeft, final WorldPose startPose, final WorldPose endPose, final Distance minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (rlTangents.size() > 2)
      {
         path.type = PathType.RSL;

         path.getWaypoint1().setCoordinate(rlTangents.get(2).getStart());
         path.getWaypoint2().setCoordinate(rlTangents.get(2).getEnd());

         // tangent pts function returns inner tangents for RL connection third
         Distance arcL1 = computeArcLength(startRight.getCenter(), startPose.getCoordinate(),
               rlTangents.get(2).getStart(), minTurnRadius, false);
         Distance arcL2 = rlTangents.get(2).getStart().distanceTo(rlTangents.get(2).getEnd());
         Distance arcL3 = computeArcLength(endLeft.getCenter(), rlTangents.get(2).getEnd(), endPose.getCoordinate(),
               minTurnRadius, true);

         path.segmentLengths[0].copy(arcL1);
         path.segmentLengths[1].copy(arcL2);
         path.segmentLengths[2].copy(arcL3);
      }
      return path;
   }

   private static DubinsPath genLSRPath(final List<PathSegment> lrTangents, final Circle startLeft,
         final Circle endRight, final WorldPose startPose, final WorldPose endPose, final Distance minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (lrTangents.size() > 3)
      {
         path.type = PathType.LSR;

         path.getWaypoint1().setCoordinate(lrTangents.get(3).getStart());
         path.getWaypoint2().setCoordinate(lrTangents.get(3).getEnd());

         // tangent pts function returns inner tangents for LR connection fourth
         Distance arcL1 = computeArcLength(startLeft.getCenter(), startPose.getCoordinate(),
               lrTangents.get(3).getStart(), minTurnRadius, true);
         Distance arcL2 = lrTangents.get(3).getStart().distanceTo(lrTangents.get(3).getEnd());
         Distance arcL3 = computeArcLength(endRight.getCenter(), lrTangents.get(3).getEnd(), endPose.getCoordinate(),
               minTurnRadius, false);

         path.segmentLengths[0].copy(arcL1);
         path.segmentLengths[1].copy(arcL2);
         path.segmentLengths[2].copy(arcL3);
      }
      return path;
   }

   private static DubinsPath genLRLPath(final double _interiorTheta, final Circle startLeft, final Circle endLeft,
         final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      DubinsPath path = new DubinsPath();
      path.type = PathType.LRL;

      WorldCoordinate startTan = new WorldCoordinate();
      WorldCoordinate endTan = new WorldCoordinate();

      Circle rCircle = new Circle();
      rCircle.getRadius().copy(minTurnRadius);

      // compute tangent circle's location using law of cosines + atan2 of line
      // between agent and query circles
      Distance offsetEast = new Distance(minTurnRadius);
      Distance offsetNorth = new Distance(minTurnRadius);

      offsetEast.scale(2).scale(Math.cos(_interiorTheta));
      offsetNorth.scale(2).scale(Math.sin(_interiorTheta));

      offsetEast.add(startLeft.getCenter().getEast());
      offsetNorth.add(startLeft.getCenter().getNorth());

      rCircle.getCenter().setCoordinate(offsetNorth, offsetEast);

      // compute tangent points given tangent circle
      offsetEast = new Distance(rCircle.getCenter().getEast());
      offsetEast.add(startLeft.getCenter().getEast());
      offsetEast.scale(0.5);

      offsetNorth = new Distance(rCircle.getCenter().getNorth());
      offsetNorth.add(startLeft.getCenter().getNorth());
      offsetNorth.scale(0.5);

      startTan.setCoordinate(offsetNorth, offsetEast);

      offsetEast = new Distance(rCircle.getCenter().getEast());
      offsetEast.add(endLeft.getCenter().getEast());
      offsetEast.scale(0.5);

      offsetNorth = new Distance(rCircle.getCenter().getNorth());
      offsetNorth.add(endLeft.getCenter().getNorth());
      offsetNorth.scale(0.5);

      endTan.setCoordinate(offsetNorth, offsetEast);

      path.getWaypoint1().setCoordinate(startTan);
      path.getWaypoint2().setCoordinate(endTan);

      Distance arcL1 = computeArcLength(startLeft.getCenter(), start.getCoordinate(), startTan, minTurnRadius, true);
      Distance arcL2 = computeArcLength(rCircle.getCenter(), startTan, endTan, minTurnRadius, false);
      Distance arcL3 = computeArcLength(endLeft.getCenter(), endTan, end.getCoordinate(), minTurnRadius, true);

      path.segmentLengths[0].copy(arcL1);
      path.segmentLengths[1].copy(arcL2);
      path.segmentLengths[2].copy(arcL3);

      return path;
   }

   private static DubinsPath genRLRPath(final double _interiorTheta, final Circle startRight, final Circle endRight,
         final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      DubinsPath path = new DubinsPath();
      path.type = PathType.RLR;

      WorldCoordinate startTan = new WorldCoordinate();
      WorldCoordinate endTan = new WorldCoordinate();

      Circle lCircle = new Circle();
      lCircle.getRadius().copy(minTurnRadius);

      // compute tangent circle's pos using law of cosines + atan2 of line
      // between agent and query circles
      Distance offsetEast = new Distance(minTurnRadius);
      Distance offsetNorth = new Distance(minTurnRadius);

      offsetEast.scale(2).scale(Math.cos(_interiorTheta));
      offsetNorth.scale(2).scale(Math.sin(_interiorTheta));

      offsetEast.add(startRight.getCenter().getEast());
      offsetNorth.add(startRight.getCenter().getNorth());

      lCircle.getCenter().setCoordinate(offsetNorth, offsetEast);

      // compute tangent points given tangent circle
      offsetEast = new Distance(lCircle.getCenter().getEast());
      offsetEast.add(startRight.getCenter().getEast());
      offsetEast.scale(0.5);

      offsetNorth = new Distance(lCircle.getCenter().getNorth());
      offsetNorth.add(startRight.getCenter().getNorth());
      offsetNorth.scale(0.5);

      startTan.setCoordinate(offsetNorth, offsetEast);

      offsetEast = new Distance(lCircle.getCenter().getEast());
      offsetEast.add(endRight.getCenter().getEast());
      offsetEast.scale(0.5);

      offsetNorth = new Distance(lCircle.getCenter().getNorth());
      offsetNorth.add(endRight.getCenter().getNorth());
      offsetNorth.scale(0.5);

      endTan.setCoordinate(offsetNorth, offsetEast);

      path.getWaypoint1().setCoordinate(startTan);
      path.getWaypoint2().setCoordinate(endTan);

      Distance arcL1 = computeArcLength(startRight.getCenter(), start.getCoordinate(), startTan, minTurnRadius, false);
      Distance arcL2 = computeArcLength(lCircle.getCenter(), startTan, endTan, minTurnRadius, true);
      Distance arcL3 = computeArcLength(endRight.getCenter(), endTan, end.getCoordinate(), minTurnRadius, false);

      path.segmentLengths[0].copy(arcL1);
      path.segmentLengths[1].copy(arcL2);
      path.segmentLengths[2].copy(arcL3);

      return path;
   }

   private static DubinsPath shorterValidPath(DubinsPath path1, DubinsPath path2)
   {
	   DubinsPath betterPath = null;
	   if(path1 != null && path2 == null)
	   {
		   betterPath = path1;
	   }
	   else if(path1 == null && path2 != null)
	   {
		   betterPath = path2;
	   }
	   else if(path1 == null && path2 == null)
	   {
		   //Do nothing
	   }
	   else if(path1.getPathType() != PathType.NO_PATH && path2.getPathType() == PathType.NO_PATH)
	   {
		   betterPath = path1;
	   }
	   else if(path1.getPathType() == PathType.NO_PATH && path2.getPathType() != PathType.NO_PATH)
	   {
		   betterPath = path2;
	   }
	   else if(path1.getPathLength().asMeters() < path2.getPathLength().asMeters())
	   {
		   betterPath = path1;
	   }
	   else
	   {
		   betterPath = path2;
	   }
	   return betterPath;
   }

}
