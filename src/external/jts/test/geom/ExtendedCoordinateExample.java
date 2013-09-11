package external.jts.test.geom;

import com.vividsolutions.jts.geom.*;


/**
 * @version 1.7
 */
public class ExtendedCoordinateExample
{

  public static void main(String args[])
  {
    ExtendedCoordinateSequenceFactory seqFact = ExtendedCoordinateSequenceFactory.instance();

    ExtendedCoordinate[] array1 = new ExtendedCoordinate[] {
      new ExtendedCoordinate(0, 0, 0, 91),
      new ExtendedCoordinate(10, 0, 0, 92),
      new ExtendedCoordinate(10, 10, 0, 93),
      new ExtendedCoordinate(0, 10, 0, 94),
      new ExtendedCoordinate(0, 0, 0, 91),
    };
    CoordinateSequence seq1 = seqFact.create(array1);

    CoordinateSequence seq2 = seqFact.create(
    new ExtendedCoordinate[] {
      new ExtendedCoordinate(5, 5, 0, 91),
      new ExtendedCoordinate(15, 5, 0, 92),
      new ExtendedCoordinate(15, 15, 0, 93),
      new ExtendedCoordinate(5, 15, 0, 94),
      new ExtendedCoordinate(5, 5, 0, 91),
    });

    GeometryFactory fact = new GeometryFactory(
        ExtendedCoordinateSequenceFactory.instance());

    Geometry g1 = fact.createPolygon(fact.createLinearRing(seq1), null);
    Geometry g2 = fact.createPolygon(fact.createLinearRing(seq2), null);

    System.out.println("WKT for g1: " + g1);
    System.out.println("Internal rep for g1: " + ((Polygon) g1).getExteriorRing().getCoordinateSequence());

    System.out.println("WKT for g2: " + g2);
    System.out.println("Internal rep for g2: " + ((Polygon) g2).getExteriorRing().getCoordinateSequence());

    Geometry gInt = g1.intersection(g2);

    System.out.println("WKT for gInt: " + gInt);
    System.out.println("Internal rep for gInt: " + ((Polygon) gInt).getExteriorRing().getCoordinateSequence());
  }

  public ExtendedCoordinateExample() {
  }


}
