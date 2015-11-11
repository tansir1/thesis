package thesis.core.entities.uav.dubins;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.PathSegment;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class DubinsPathGenerator
{

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

      startLeft.getCenter().setEast(Distance.add(start.getEast(), minTurnRadius).scale(Math.cos(theta)));
      startLeft.getCenter().setNorth(Distance.add(start.getNorth(), minTurnRadius).scale(Math.sin(theta)));
      startLeft.getRadius().copy(minTurnRadius);

      theta = start.getHeading().asRadians();
      theta -= Math.PI / 2.0;
      if (theta < -Math.PI)
         theta += 2.0 * Math.PI;

      startRight.getCenter().setEast(Distance.add(start.getEast(), minTurnRadius).scale(Math.cos(theta)));
      startRight.getCenter().setNorth(Distance.add(start.getNorth(), minTurnRadius).scale(Math.sin(theta)));
      startRight.getRadius().copy(minTurnRadius);

      theta = end.getHeading().asRadians();
      theta += Math.PI / 2.0;
      if (theta > Math.PI)
         theta -= 2.0 * Math.PI;

      endLeft.getCenter().setEast(Distance.add(end.getEast(), minTurnRadius).scale(Math.cos(theta)));
      endLeft.getCenter().setNorth(Distance.add(end.getNorth(), minTurnRadius).scale(Math.sin(theta)));
      endLeft.getRadius().copy(minTurnRadius);

      theta = end.getHeading().asRadians();
      theta -= Math.PI / 2.0;
      if (theta < -Math.PI)
         theta += 2.0 * Math.PI;

      endRight.getCenter().setEast(Distance.add(end.getEast(), minTurnRadius).scale(Math.cos(theta)));
      endRight.getCenter().setNorth(Distance.add(end.getNorth(), minTurnRadius).scale(Math.sin(theta)));
      endRight.getRadius().copy(minTurnRadius);

      DubinsPath path = genCSCPath(startLeft, startRight, endLeft, endRight, minTurnRadius, start, end);
      DubinsPath temp = genCCCPath(startLeft, startRight, endLeft, endRight, minTurnRadius, start, end);
      if (temp.length < path.length)
      {
         path = temp;
      }
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
      if (temp.length < path.length)
         path = temp;

      temp = genRSLPath(rlTangents, startRight, endLeft, start, end, minTurnRadius);
      if (temp.length < path.length)
         path = temp;

      temp = genLSRPath(lrTangents, startLeft, endRight, start, end, minTurnRadius);
      if (temp.length < path.length)
         path = temp;

      return path;
   }

   private static DubinsPath genCCCPath(final Circle startLeft, final Circle startRight, final Circle endLeft,
         final Circle endRight, final double minTurnRadius, final WorldPose start, final WorldPose end)
   {
      // Initialize to an invalid path
      DubinsPath path = new DubinsPath();

      // find the relative angle for L and right
      double theta = 0.0;
      double distance = startRight.getCenter().distanceTo(endRight.getCenter()).asMeters();

      if (distance < 4.0 * minTurnRadius)
      {
         theta = Math.acos(distance / (4.0 * minTurnRadius));

         theta += Math.atan2(endRight.center.y - startRight.center.y, endRight.center.x - startRight.center.x);

         path = genRLRPath(theta, startRight, endRight, minTurnRadius, start, end);
      }

      distance = startLeft.center.distance(endLeft.center);
      if (distance < 4.0 * minTurnRadius)
      {
         theta = Math.acos(distance / (4.0 * minTurnRadius));

         theta = Math.atan2(endLeft.center.y - startLeft.center.y, endLeft.center.x - startLeft.center.x) - theta;

         DubinsPath temp = genLRLPath(theta, startLeft, endLeft, minTurnRadius, start, end);
         if (path != null && temp.length < path.length)
         {
            path = temp;
         }
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
   private static Distance computeArcLength(final WorldCoordinate center, final WorldCoordinate lhs, final WorldCoordinate rhs,
         final Distance radius, final boolean left)
   {
      Angle leftAngle = center.bearingTo(lhs);
      Angle rightAngle = center.bearingTo(lhs);

      rightAngle.subtract(leftAngle);
      double theta = rightAngle.asRadians();

      if (theta < 0 && left)
         theta += 2.0 * Math.PI;
      else if (theta > 0 && !left)
         theta -= 2.0 * Math.PI;

      Distance arcLen = new Distance();
      arcLen.copy(radius);
      arcLen.scale(theta);
      return arcLen;
   }

   private static DubinsPath genRSRPath(final List<PathSegment> rrTangents, final Circle startRight,
         final Circle endRight, final WorldPose startPose, final WorldPose endPose, final double minTurnRadius)
   {
      DubinsPath path = new DubinsPath();

      if (!rrTangents.isEmpty())
      {
         path.type = PathType.RSR;

         path.waypoint1 = rrTangents.get(0).getStart();
         path.waypoint2 = rrTangents.get(0).getEnd();

         // tangent pts function returns outer tangents for RR connection first
         double arcL1 = computeArcLength(startRight.getCenter(), startPose.getCoordinate(), rrTangents.get(0).getStart(), minTurnRadius, false);

         double arcL2 = rrTangents.get(0).getStart().distance(rrTangents.get(0).getEnd());

         double arcL3 = computeArcLength(endRight.getCenter(), rrTangents.get(0).getEnd(), endPose.getCoordinate(), minTurnRadius, false);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;

         path.length = arcL1 + arcL2 + arcL3;
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

         path.waypoint1 = llTangents.get(1).getStart();
         path.waypoint2 = llTangents.get(1).getEnd();

         // tangent pts function returns outer tangents for LL connection second
         double arcL1 = computeArcLength(startLeft.center, startPose.getCoordinate(), llTangents.get(1).getStart(), minTurnRadius, true);
         double arcL2 = llTangents.get(1).getStart().distance(llTangents.get(1).getEnd());
         double arcL3 = computeArcLength(endLeft.center, llTangents.get(1).getEnd(), endPose.pos, minTurnRadius, true);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;

         path.length = arcL1 + arcL2 + arcL3;
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

         path.waypoint1 = rlTangents.get(2).getStart();
         path.waypoint2 = rlTangents.get(2).getEnd();

         // tangent pts function returns inner tangents for RL connection third
         double arcL1 = computeArcLength(startRight.center, startPose.getCoordinate(), rlTangents.get(2).getStart(), minTurnRadius, false);
         double arcL2 = rlTangents.get(2).getStart().distance(rlTangents.get(2).getEnd());
         double arcL3 = computeArcLength(endLeft.center, rlTangents.get(2).getEnd(), endPose.pos, minTurnRadius, true);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;

         path.length = arcL1 + arcL2 + arcL3;
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

         path.waypoint1 = lrTangents.get(3).getStart();
         path.waypoint2 = lrTangents.get(3).getEnd();

         // tangent pts function returns inner tangents for LR connection fourth
         double arcL1 = computeArcLength(startLeft.center, startPose.getCoordinate(), lrTangents.get(3).getStart(), minTurnRadius, true);
         double arcL2 = lrTangents.get(3).getStart().distance(lrTangents.get(3).getEnd());
         double arcL3 = computeArcLength(endRight.center, lrTangents.get(3).getEnd(), endPose.pos, minTurnRadius, false);

         path.segmentLengths[0] = arcL1;
         path.segmentLengths[1] = arcL2;
         path.segmentLengths[2] = arcL3;

         path.length = arcL1 + arcL2 + arcL3;
      }
      return path;
   }

   private static DubinsPath genLRLPath(final double _interiorTheta, final Circle startLeft, final Circle endLeft,
         final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      DubinsPath path = new DubinsPath();
      path.type = PathType.LRL;

      Point2D.Double startTan = new Point2D.Double();
      Point2D.Double endTan = new Point2D.Double();

      Circle rCircle = new Circle();
      rCircle.getRadius().copy(minTurnRadius);

      // compute tangent circle's pos using law of cosines + atan2 of line
      // between agent and query circles
      rCircle.center.x = startLeft.center.x + (2.0 * minTurnRadius * Math.cos(_interiorTheta));
      rCircle.center.y = startLeft.center.y + (2.0 * minTurnRadius * Math.sin(_interiorTheta));

      // compute tangent points given tangent circle
      startTan.x = (rCircle.center.x + startLeft.center.x) / 2.0;
      startTan.y = (rCircle.center.y + startLeft.center.y) / 2.0;

      endTan.x = (rCircle.center.x + endLeft.center.x) / 2.0;
      endTan.y = (rCircle.center.y + endLeft.center.y) / 2.0;

      double arcL1 = computeArcLength(startLeft.center, start.pos, startTan, minTurnRadius, true);
      double arcL2 = computeArcLength(rCircle.center, startTan, endTan, minTurnRadius, false);
      double arcL3 = computeArcLength(endLeft.center, endTan, end.pos, minTurnRadius, true);

      path.segmentLengths[0] = arcL1;
      path.segmentLengths[1] = arcL2;
      path.segmentLengths[2] = arcL3;

      path.length = arcL1 + arcL2 + arcL3;
      return path;
   }

   private static DubinsPath genRLRPath(final double _interiorTheta, final Circle startRight, final Circle endRight,
         final Distance minTurnRadius, final WorldPose start, final WorldPose end)
   {
      DubinsPath path = new DubinsPath();
      path.type = PathType.RLR;

      Point2D.Double agentTan = new Point2D.Double();
      Point2D.Double queryTan = new Point2D.Double();

      Circle lCircle = new Circle();
      lCircle.getRadius().copy(minTurnRadius);

      // compute tangent circle's pos using law of cosines + atan2 of line
      // between agent and query circles
      lCircle.center.x = startRight.center.x + (2.0 * minTurnRadius * Math.cos(_interiorTheta));
      lCircle.center.y = startRight.center.y + (2.0 * minTurnRadius * Math.sin(_interiorTheta));

      // compute tangent points given tangent circle
      agentTan.x = (lCircle.center.x + startRight.center.x) / 2.0;
      agentTan.y = (lCircle.center.y + startRight.center.y) / 2.0;

      queryTan.x = (lCircle.center.x + endRight.center.x) / 2.0;
      queryTan.y = (lCircle.center.y + endRight.center.y) / 2.0;

      double arcL1 = computeArcLength(startRight.getCenter(), start.getCoordinate(), agentTan, minTurnRadius, false);
      double arcL2 = computeArcLength(lCircle.getCenter(), agentTan, queryTan, minTurnRadius, true);
      double arcL3 = computeArcLength(endRight.getCenter(), queryTan, end.getCoordinate(), minTurnRadius, false);

      path.segmentLengths[0] = arcL1;
      path.segmentLengths[1] = arcL2;
      path.segmentLengths[2] = arcL3;

      path.length = arcL1 + arcL2 + arcL3;
      return path;
   }

}
