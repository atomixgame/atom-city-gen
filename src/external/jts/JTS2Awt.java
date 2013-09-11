/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Sun Ning/SNDA
 * @since 2010-3-3
 */
public class JTS2Awt {

    public static void showUI(final List<Shape> shape) {
        JFrame jframe = new JFrame("JTS Geometry to AWT Shape");

        JPanel jp = new JPanel() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
                if (shape != null) {
                    for (Shape s : shape) {
                        g2d.setColor(new Color(Color.HSBtoRGB(new Random().nextFloat(), 1f, 0.6f)));
                        g2d.draw(s);
                    }
                }
            }
        };
        jp.setPreferredSize(new Dimension(650, 650));



        jframe.getContentPane().add(jp);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
    }

    public static Shape toShape(Geometry geom) {
        ShapeWriter writer = new ShapeWriter();
        return writer.toShape(geom);
    }

    public static void main(String[] args) {
        Collection<Coordinate> coords = DelaunayAndVoronoiApp.getPredefinedSites();
        Geometry geomT = DelaunayAndVoronoiApp.buildDelaunayTriangulation(coords);
        Geometry geomD = DelaunayAndVoronoiApp.buildVoronoiDiagram(coords);
        ArrayList geoList = new ArrayList();
        //geoList.add(toShape(geomT));
        geoList.add(toShape(geomD));
        showUI(geoList);

        //showUI(unionTest());
    }

    public static ArrayList<Shape> getPolys() {
        ArrayList<Shape> arr = new ArrayList<Shape>();
        try {
            WKTReader rdr = new WKTReader();
            Collection lines = new ArrayList();

            lines.add(rdr.read("LINESTRING (0 0 , 10 10)"));   // isolated edge
            lines.add(rdr.read("LINESTRING (185 221, 100 100)"));   //dangling edge
            lines.add(rdr.read("LINESTRING (185 221, 88 275, 180 316)"));
            lines.add(rdr.read("LINESTRING (185 221, 292 281, 180 316)"));
            lines.add(rdr.read("LINESTRING (189 98, 83 187, 185 221)"));
            lines.add(rdr.read("LINESTRING (189 98, 325 168, 185 221)"));

            Polygonizer polygonizer = new Polygonizer();
            polygonizer.add(lines);

            Collection polys = polygonizer.getPolygons();

            for (Object obj : polys) {
                Geometry geo = (Geometry) obj;
                arr.add(toShape(geo));
            }
            return arr;
        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<Shape> unionTest() {
        ArrayList<Shape> arr = new ArrayList<Shape>();
        try {
            WKTReader rdr = new WKTReader();

            Geometry[] geom = new Geometry[3];
            geom[0] = rdr.read("POLYGON (( 100 180, 100 260, 180 260, 180 180, 100 180 ))");
            geom[1] = rdr.read("POLYGON (( 80 140, 80 200, 200 200, 200 140, 80 140 ))");
            geom[2] = rdr.read("POLYGON (( 160 160, 160 240, 240 240, 240 160, 160 160 ))");

            arr.add(toShape(unionUsingBuffer(geom)));
            return arr;
        } catch (Exception e) {
        }
        return null;
    }

    public static Geometry unionUsingBuffer(Geometry[] geom) {
        GeometryFactory fact = geom[0].getFactory();
        Geometry geomColl = fact.createGeometryCollection(geom);
        Geometry union = geomColl.buffer(0.0);
        return union;
    }
}
