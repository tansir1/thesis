package thesis.core.uav.dubins;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.Circle;
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
    *           The minimum radius required for the uav to turn around in meters.
    * @param start
    *           The starting position and orientation of the path.
    * @param end
    *           The ending position and orientation of the path.
    * @return A path satisfying the given constraints.
    */
   public static DubinsPath generate(final double minTurnRadius, final WorldPose start, final WorldPose end)
   {
      Circle startLeft = new Circle();
      Circle startRight = new Circle();
      Circle endLeft = new Circle();
      Circle endRight = new Circle();

      double theta = Math.toRadians(start.getHeading());
      theta += Math.PI / 2.0;
      if (theta > Math.PI)
         theta -= 2.0 * Math.PI;

      startLeft.getCenter().setEast(start.getEast() + minTurnRadius * Math.cos(theta));
      startLeft.getCenter().setNorth(start.getNorth() + minTurnRadius * Math.sin(theta));
      startLeft.setRadius(minTurnRadius);

      theta = Math.toRadians(start.getHeading());
      theta -= Math.PI / 2.0;
      if (theta < -Math.PI)
         theta += 2.0 * Math.PI;

      startRight.getCenter().setEast(start.getEast() + minTurnRadius * Math.cos(theta));
      startRight.getCenter().setNorth(start.getNorth() + minTurnRadius * Math.sin(theta));
      startRight.setRadius(minTurnRadius);

      theta = Math.toRadians(end.getHeading());
      theta += Math.PI / 2.0;
      if (theta > Math.PI)
         theta -= 2.0 * Math.PI;

      endLeft.getCenter().setEast(end.getEast() + minTurnRadius * Math.cos(theta));
      endLeft.getCenter().setNorth(end.getNorth() + minTurnRadius * Math.sin(theta));
      endLeft.setRadius(minTurnRadius);

      theta = Math.toRadians(end.getHeading());
      theta -= Math.PI / 2.0;
      if (theta < -Math.PI)
         theta += 2.0 * Math.PI;

      endRight.getCenter().setEast(end.getEast() + minTurnRadius * Math.cos(theta));
      endRight.getCenter().setNorth(end.getNorth() + minTurnRadius * Math.sin(theta));
      endRight.setRadius(minTurnRadius);

      DubinsPath path = genCSCPath(startLeft, startRight, endLeft, endRight, minTurnRadius, start, end);
      DubinsPath temp = genCCCPath(startLeft, startRight, endLeft, endRight, minTurnRadius, start, end);
      if ((temp.getPathType() != PathType.NO_PATH && temp.getPathLength() < path.getPathLength()) ||
    		  (path.getPathType() == PathType.NO_PATH && temp.getPathType() != PathType.NO_PATH))
      {
         path = temp;
      }

      path.getStartPose().copy(start);
      path.getEndPose().copy(end);

      return path;
   }

   private static DubinsPath genCSCPath(final Circle startLeft, final Circle startRight, final Circle endLeft,
         final Circle endRight, final double minTurnRadius, final WorldPose start, final WorldPose end)
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
         final Circle endRight, final double minTurnRadius, final WorldPose start, final WorldPose end)
   {
      // Initialize to an invalid path
      DubinsPath path = new DubinsPath();

      // find the relative angle for L and right
      double theta = 0.0;
      double distance = startRight.getCenter().distanceTo(endRight.getCenter());

      if (distance < (4.0 * minTurnRadius))
      {
         theta = Math.acos(distance / (4.0 * minTurnRadius));

         double delNorth = endRight.getCenter().getNorth() - startRight.getCenter().getNorth();
         double delEast = endRight.getCenter().getEast() - startRight.getCenter().getEast();

         theta += Math.atan2(delNorth, delEast);

         path = genRLRPath(theta, startRight, endRight, minTurnRadius, start, end);
      }

      distance = startLeft.getCenter().distanceTo(endLeft.getCenter());
      if (distance < (4.0 * minTurnRadius))
      {
         theta = Math.acos(distance / (4.0 * minTurnRadius));

         double delNorth = endLeft.getCenter().getNorth() - startLeft.getCenter().getNorth();
         double delEast = endLeft.getCenter().getEast() - startLeft.getCenter().getEast();

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
      double x1 = c1.getCenter().getEast();
      double y1 = c1.getCenter().getNorth();
      double x2 = c2.getCenter().getEast();
      double y2 = c2.getCenter().getNorth();
      double r1 = c1.getRadius();
      double r2 = c2.getRadius();

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
            path.getStart().setEast(x1 + r1 * nx);
            path.getStart().setNorth(y1 + r1 * ny);
            path.getEnd().setEast(x2 + sign1 * r2 * nx);
            path.getEnd().setNorth(y2 + sign1 * r2 * ny);
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
    * @return The arc length of the path in meters.
    */
   private static double computeArcLength(final WorldCoordinate center, final WorldCoordinate lhs,
         final WorldCoordinate rhs, final double radius, final boolean left)
   {
      //Angle leftAngle = new Angle();
      //Angle rightAngle = new Angle();
      /*
      Angle leftAngle = center.bearingTo(lhs);
      Angle rightAngle = center.bearingTo(lhs);

      rightAngle.subtract(leftAngle);
      double theta = rightAngle.asRadians();*/

      double x1 = lhs.getEast() - center.getEast();
      double y1 = lhs.getNorth() - center.getNorth();

      double x2 = rhs.getEast() - center.getEast();
      double y2 = rhs.getNorth() - center.getNorth();

      double theta = Math.atan2(y2, x2) - Math.atan2(y1, x1);

      if (theta < 0 && left)
         theta += 2.0 * Math.PI;
      else if (theta > 0 && !left)
         theta -= 2.0 * Math.PI;

      double arcLen = radius * theta;
      if(arcLen < 0)
      {
         arcLen = -arcLen;
      }
      return arcLen;
   }

   private static DubinsPath genRSRPath(final List<PathSegment> rrTangents, final Circle startRight,
         final Circle endRight, final WorldPose startPose, final WorldPose endPose, final double minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (!rrTangents.isEmpty())
      {
         path.type = PathType.RSR;

         path.getWaypoint1().setCoordinate(rrTangents.get(0).getStart());
         path.getWaypoint2().setCoordinate(rrTangents.get(0).getEnd());

         // tangent pts function returns outer tangents for RR connection first
         double arcL1 = computeArcLength(startRight.getCenter(), startPose.getCoordinate(),
               rrTangents.get(0).getStart(), minTurnRadius, false);

         double arcL2 = rrTangents.get(0).getStart().distanceTo(rrTangents.get(0).getEnd());

         double arcL3 = computeArcLength(endRight.getCenter(), rrTangents.get(0).getEnd(), endPose.getCoordinate(),
               minTurnRadius, false);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;
      }
      return path;
   }

   private static DubinsPath genLSLPath(final List<PathSegment> llTangents, final Circle startLeft,
         final Circle endLeft, final WorldPose startPose, final WorldPose endPose, final double minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (llTangents.size() > 1)
      {
         path.type = PathType.LSL;

         path.getWaypoint1().setCoordinate(llTangents.get(1).getStart());
         path.getWaypoint2().setCoordinate(llTangents.get(1).getEnd());

         // tangent pts function returns outer tangents for LL connection second
         double arcL1 = computeArcLength(startLeft.getCenter(), startPose.getCoordinate(),
               llTangents.get(1).getStart(), minTurnRadius, true);
         double arcL2 = llTangents.get(1).getStart().distanceTo(llTangents.get(1).getEnd());
         double arcL3 = computeArcLength(endLeft.getCenter(), llTangents.get(1).getEnd(), endPose.getCoordinate(),
               minTurnRadius, true);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;
      }
      return path;
   }

   private static DubinsPath genRSLPath(final List<PathSegment> rlTangents, final Circle startRight,
         final Circle endLeft, final WorldPose startPose, final WorldPose endPose, final double minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (rlTangents.size() > 2)
      {
         path.type = PathType.RSL;

         path.getWaypoint1().setCoordinate(rlTangents.get(2).getStart());
         path.getWaypoint2().setCoordinate(rlTangents.get(2).getEnd());

         // tangent pts function returns inner tangents for RL connection third
         double arcL1 = computeArcLength(startRight.getCenter(), startPose.getCoordinate(),
               rlTangents.get(2).getStart(), minTurnRadius, false);
         double arcL2 = rlTangents.get(2).getStart().distanceTo(rlTangents.get(2).getEnd());
         double arcL3 = computeArcLength(endLeft.getCenter(), rlTangents.get(2).getEnd(), endPose.getCoordinate(),
               minTurnRadius, true);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;
      }
      return path;
   }

   private static DubinsPath genLSRPath(final List<PathSegment> lrTangents, final Circle startLeft,
         final Circle endRight, final WorldPose startPose, final WorldPose endPose, final double minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (lrTangents.size() > 3)
      {
         path.type = PathType.LSR;

         path.getWaypoint1().setCoordinate(lrTangents.get(3).getStart());
         path.getWaypoint2().setCoordinate(lrTangents.get(3).getEnd());

         // tangent pts function returns inner tangents for LR connection fourth
         double arcL1 = computeArcLength(startLeft.getCenter(), startPose.getCoordinate(),
               lrTangents.get(3).getStart(), minTurnRadius, true);
         double arcL2 = lrTangents.get(3).getStart().distanceTo(lrTangents.get(3).getEnd());
         double arcL3 = computeArcLength(endRight.getCenter(), lrTangents.get(3).getEnd(), endPose.getCoordinate(),
               minTurnRadius, false);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;
      }
      return path;
   }

   private static DubinsPath genLRLPath(final double interiorTheta, final Circle startLeft, final Circle endLeft,
         final double minTurnRadius, final WorldPose start, final WorldPose end)
   {
      DubinsPath path = new DubinsPath();
      path.type = PathType.LRL;

      WorldCoordinate startTan = new WorldCoordinate();
      WorldCoordinate endTan = new WorldCoordinate();

      Circle rCircle = new Circle();
      rCircle.setRadius(minTurnRadius);

      // compute tangent circle's location using law of cosines + atan2 of line
      // between agent and query circles
      double offsetEast = minTurnRadius * 2 * Math.cos(interiorTheta);
      double offsetNorth = minTurnRadius * 2 * Math.sin(interiorTheta);

      offsetEast += startLeft.getCenter().getEast();
      offsetNorth += startLeft.getCenter().getNorth();

      rCircle.getCenter().setCoordinate(offsetNorth, offsetEast);

      // compute tangent points given tangent circle
      offsetEast = rCircle.getCenter().getEast();
      offsetEast += startLeft.getCenter().getEast();
      offsetEast *= 0.5;

      offsetNorth = rCircle.getCenter().getNorth();
      offsetNorth += startLeft.getCenter().getNorth();
      offsetNorth *= 0.5;

      startTan.setCoordinate(offsetNorth, offsetEast);

      offsetEast = rCircle.getCenter().getEast();
      offsetEast += endLeft.getCenter().getEast();
      offsetEast *= 0.5;

      offsetNorth = rCircle.getCenter().getNorth();
      offsetNorth += endLeft.getCenter().getNorth();
      offsetNorth *= 0.5;

      endTan.setCoordinate(offsetNorth, offsetEast);

      path.getWaypoint1().setCoordinate(startTan);
      path.getWaypoint2().setCoordinate(endTan);

      double arcL1 = computeArcLength(startLeft.getCenter(), start.getCoordinate(), startTan, minTurnRadius, true);
      double arcL2 = computeArcLength(rCircle.getCenter(), startTan, endTan, minTurnRadius, false);
      double arcL3 = computeArcLength(endLeft.getCenter(), endTan, end.getCoordinate(), minTurnRadius, true);

      path.segmentLengths[0] = arcL1;
      path.segmentLengths[1] = arcL2;
      path.segmentLengths[2] = arcL3;

      return path;
   }

   private static DubinsPath genRLRPath(final double interiorTheta, final Circle startRight, final Circle endRight,
         final double minTurnRadius, final WorldPose start, final WorldPose end)
   {
      DubinsPath path = new DubinsPath();
      path.type = PathType.RLR;

      WorldCoordinate startTan = new WorldCoordinate();
      WorldCoordinate endTan = new WorldCoordinate();

      Circle lCircle = new Circle();
      lCircle.setRadius(minTurnRadius);

      // compute tangent circle's pos using law of cosines + atan2 of line
      // between agent and query circles
      double offsetEast = minTurnRadius * 2 * Math.cos(interiorTheta);
      double offsetNorth = minTurnRadius * 2 * Math.sin(interiorTheta);

      offsetEast += startRight.getCenter().getEast();
      offsetNorth += startRight.getCenter().getNorth();

      lCircle.getCenter().setCoordinate(offsetNorth, offsetEast);

      // compute tangent points given tangent circle
      offsetEast = lCircle.getCenter().getEast();
      offsetEast += startRight.getCenter().getEast();
      offsetEast *= 0.5;

      offsetNorth = lCircle.getCenter().getNorth();
      offsetNorth += startRight.getCenter().getNorth();
      offsetNorth *= 0.5;

      startTan.setCoordinate(offsetNorth, offsetEast);

      offsetEast = lCircle.getCenter().getEast();
      offsetEast += endRight.getCenter().getEast();
      offsetEast *= 0.5;

      offsetNorth = lCircle.getCenter().getNorth();
      offsetNorth += endRight.getCenter().getNorth();
      offsetNorth *= 0.5;

      endTan.setCoordinate(offsetNorth, offsetEast);

      path.getWaypoint1().setCoordinate(startTan);
      path.getWaypoint2().setCoordinate(endTan);

      double arcL1 = computeArcLength(startRight.getCenter(), start.getCoordinate(), startTan, minTurnRadius, false);
      double arcL2 = computeArcLength(lCircle.getCenter(), startTan, endTan, minTurnRadius, true);
      double arcL3 = computeArcLength(endRight.getCenter(), endTan, end.getCoordinate(), minTurnRadius, false);

      path.segmentLengths[0] = arcL1;
      path.segmentLengths[1] = arcL2;
      path.segmentLengths[2] = arcL3;

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
	   else if(path1.getPathLength() < path2.getPathLength())
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
