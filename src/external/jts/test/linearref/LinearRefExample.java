package external.jts.test.linearref;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;
import com.vividsolutions.jts.linearref.*;

/**
 * Examples of Linear Referencing
 *
 * @version 1.7
 */

public class LinearRefExample {

  static GeometryFactory fact = new GeometryFactory();
  static WKTReader rdr = new WKTReader(fact);

  public static void main(String[] args)
      throws Exception
  {
    LinearRefExample example = new LinearRefExample();
    example.run();
  }


  public LinearRefExample() {
  }

  public void run()
      throws Exception
  {
    runExtractedLine("LINESTRING (0 0, 10 10, 20 20)", 1, 10);
    runExtractedLine("MULTILINESTRING ((0 0, 10 10), (20 20, 25 25, 30 40))", 1, 20);
  }

  public void runExtractedLine(String wkt, double start, double end)
    throws ParseException
  {
    System.out.println("=========================");
    Geometry g1 = rdr.read(wkt);
    System.out.println("Input Geometry: " + g1);
    System.out.println("Indices to extract: " + start + " " + end);

    LengthIndexedLine indexedLine = new LengthIndexedLine(g1);

    Geometry subLine = indexedLine.extractLine(start, end);
    System.out.println("Extracted Line: " + subLine);

    double[] index = indexedLine.indicesOf(subLine);
    System.out.println("Indices of extracted line: " + index[0] + " " + index[1]);

    Coordinate midpt = indexedLine.extractPoint((index[0] + index[1]) / 2);
    System.out.println("Midpoint of extracted line: " + midpt);
  }
}