/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jts;

/**
 *
 * @author cuong.nguyenmanh2
 */
import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

/**
 *
 * @author Sun Ning/SNDA
 * @since 2010-3-3
 */
public class DelaunayAndVoronoiApp {

    /**
     * create some predefined sites
     * @return
     */
    public static Collection<Coordinate> getPredefinedSites() {
        double[][] coords = {{100, 27}, {28, 50}, {29, 40}, {32, 90}, {12, 26}};
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>(coords.length);

        for (int i = 0; i < coords.length; i++) {
            coordinates.add(new Coordinate(coords[i][0], coords[i][1]));
        }

        return coordinates;
    }

    /**
     *
     * @param coords
     * @return a geometry collection of triangulations
     */
    public static Geometry buildDelaunayTriangulation(Collection<Coordinate> coords) {
        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(coords);
        return builder.getTriangles(new GeometryFactory());
    }

    /**
     *
     * @param coords
     * @return a collection of polygons
     */
    public static Geometry buildVoronoiDiagram(Collection<Coordinate> coords) {
        VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
        builder.setSites(coords);
        return builder.getDiagram(new GeometryFactory());
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Collection<Coordinate> coordinates = getPredefinedSites();

        /**
         * Delauny
         */
        GeometryCollection triangulations = (GeometryCollection) buildDelaunayTriangulation(coordinates);

        int total = triangulations.getNumGeometries();
        System.out.printf("Total triangulations: %d\n", total);
        for (int i = 0; i < total; i++) {
            Geometry g = triangulations.getGeometryN(i);
            Coordinate[] coords = g.getCoordinates();
            System.out.printf("Triangulation %d: ", i);
            for (Coordinate c : coords) {
                System.out.printf("(%.3f, %.3f) ", c.x, c.y);
            }
            System.out.println();
        }

        /**
         * Voronoi
         */
        GeometryCollection diagram = (GeometryCollection) buildVoronoiDiagram(coordinates);
        int totalDia = diagram.getNumGeometries();
        for (int i = 0; i < totalDia; i++) {
            Geometry g = diagram.getGeometryN(i);
            Coordinate[] coords = g.getCoordinates();
            System.out.printf("Diagram %d: ", i);
            for (Coordinate c : coords) {
                System.out.printf("(%.3f, %.3f) ", c.x, c.y);
            }
            System.out.println();
        }
    }
}

/*
Total triangulations: 5
Triangulation 0: (32.000, 90.000) (12.000, 26.000) (28.000, 50.000) (32.000, 90.000)
Triangulation 1: (32.000, 90.000) (28.000, 50.000) (100.000, 27.000) (32.000, 90.000)
Triangulation 2: (100.000, 27.000) (28.000, 50.000) (29.000, 40.000) (100.000, 27.000)
Triangulation 3: (100.000, 27.000) (29.000, 40.000) (12.000, 26.000) (100.000, 27.000)
Triangulation 4: (12.000, 26.000) (29.000, 40.000) (28.000, 50.000) (12.000, 26.000)
Diagram 0: (-76.000, 88.625) (-76.000, 178.000) (176.713, 178.000) (72.699, 65.730) (-38.235, 76.824) (-76.000, 88.625)
Diagram 1: (-76.000, -62.000) (-76.000, 88.625) (-38.235, 76.824) (11.978, 43.348) (56.422, -10.619) (57.006, -62.000) (-76.000, -62.000)
Diagram 2: (11.978, 43.348) (-38.235, 76.824) (72.699, 65.730) (67.316, 48.882) (11.978, 43.348)
Diagram 3: (176.713, 178.000) (188.000, 178.000) (188.000, -62.000) (57.006, -62.000) (56.422, -10.619) (67.316, 48.882) (72.699, 65.730) (176.713, 178.000)
Diagram 4: (11.978, 43.348) (67.316, 48.882) (56.422, -10.619) (11.978, 43.348)
 */
