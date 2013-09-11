package external.jts.test.operation.distance;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * Example of computing distance and closest points between geometries
 * using the DistanceOp class.
 *
 * @version 1.7
 */
public class ClosestPointExample
{
  static GeometryFactory fact = new GeometryFactory();
  static WKTReader wktRdr = new WKTReader(fact);

  public static void main(String[] args) {
    ClosestPointExample example = new ClosestPointExample();
    example.run();
  }

  public ClosestPointExample()
  {
  }

  public void run()
  {
    findClosestPoint(
        "POLYGON ((200 180, 60 140, 60 260, 200 180))",
       "POINT (140 280)");
    findClosestPoint(
        "POLYGON ((200 180, 60 140, 60 260, 200 180))",
       "MULTIPOINT (140 280, 140 320)");
    findClosestPoint(
        "LINESTRING (100 100, 200 100, 200 200, 100 200, 100 100)",
       "POINT (10 10)");
    findClosestPoint(
        "LINESTRING (100 100, 200 200)",
       "LINESTRING (100 200, 200 100)");
    findClosestPoint(
        "LINESTRING (100 100, 200 200)",
       "LINESTRING (150 121, 200 0)");
    findClosestPoint(
        "POLYGON (( 76 185, 125 283, 331 276, 324 122, 177 70, 184 155, 69 123, 76 185 ), ( 267 237, 148 248, 135 185, 223 189, 251 151, 286 183, 267 237 ))",
       "LINESTRING ( 153 204, 185 224, 209 207, 238 222, 254 186 )");
    findClosestPoint(
        "POLYGON (( 76 185, 125 283, 331 276, 324 122, 177 70, 184 155, 69 123, 76 185 ), ( 267 237, 148 248, 135 185, 223 189, 251 151, 286 183, 267 237 ))",
       "LINESTRING ( 120 215, 185 224, 209 207, 238 222, 254 186 )");
  }

  public void findClosestPoint(String wktA, String wktB)
  {
    System.out.println("-------------------------------------");
    try {
      Geometry A = wktRdr.read(wktA);
      Geometry B = wktRdr.read(wktB);
      System.out.println("Geometry A: " + A);
      System.out.println("Geometry B: " + B);
      DistanceOp distOp = new DistanceOp(A, B);

      double distance = distOp.distance();
      System.out.println("Distance = " + distance);

      Coordinate[] closestPt = distOp.closestPoints();
      LineString closestPtLine = fact.createLineString(closestPt);
      System.out.println("Closest points: " + closestPtLine
                         + " (distance = " + closestPtLine.getLength() + ")");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
