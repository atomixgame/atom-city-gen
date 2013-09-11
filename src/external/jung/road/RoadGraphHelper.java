/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import external.jung.road.Vertex3DInfo;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import sg.atom.geo.intersect.Intersector;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import external.jung.road.ClockWireVertex;
import external.jung.demo.RoadEditorDemo;
import org.apache.commons.collections15.Factory;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class RoadGraphHelper {

    /**
     * the graph
     */
    Graph<Vertex3DInfo, Edge3DInfo> graph;
    public ArrayList<Vector3f> pointList = new ArrayList<Vector3f>();
    public ArrayList<Vector3f> ipointList = new ArrayList<Vector3f>();
    public ArrayList<Pair<Vector3f>> lineList = new ArrayList<Pair<Vector3f>>();
    public ArrayList<Pair<Vector3f>> orderLineList = new ArrayList<Pair<Vector3f>>();
    public ArrayList<Pair<Vector3f>> outLineList = new ArrayList<Pair<Vector3f>>();
    public float size = 50;
    public HashMap<Vertex3DInfo, ArrayList<Vector3f>> crossRoadMap = new HashMap<Vertex3DInfo, ArrayList<Vector3f>>();
    public AbstractLayout<Vertex3DInfo, Edge3DInfo> layout;
    public Factory<Vertex3DInfo> vertexFactory;
    public Factory<Edge3DInfo> edgeFactory;

    public void resetExtraDraw() {
        pointList.clear();
        ipointList.clear();
        lineList.clear();
        orderLineList.clear();
        outLineList.clear();
    }

    public RoadGraphHelper() {
    }

    public Graph<Vertex3DInfo, Edge3DInfo> createRoadGraph() {
        // create a simple graph for the demo
        graph = new SparseMultigraph<Vertex3DInfo, Edge3DInfo>();
        vertexFactory = new VertexFactory();
        edgeFactory = new EdgeFactory();
        return graph;
    }

    public Vertex3DInfo otherEnds(Edge3DInfo edge, Vertex3DInfo ver) {
        Pair<Vertex3DInfo> pair = graph.getEndpoints(edge);
        if (pair.getFirst().equals(ver)) {
            return pair.getSecond();
        } else {
            return pair.getFirst();
        }
    }

    public boolean isFirst(Edge3DInfo edge, Vertex3DInfo ver) {
        Pair<Vertex3DInfo> pair = graph.getEndpoints(edge);
        if (pair.getFirst().equals(ver)) {
            return true;
        } else {
            return false;
        }
    }

    void vertexAngle() {

        for (Vertex3DInfo v : graph.getVertices()) {
            Point2D loc = layout.transform(v);
            Vector3f loc3d = point2dToVec3f(loc);
            if (graph.getNeighborCount(v) > 1) {
                ArrayList<Vector3f> neiList = new ArrayList<Vector3f>();
                for (Vertex3DInfo n : graph.getNeighbors(v)) {
                    Point2D neiLoc = layout.transform(n);
                    Vector3f neiLoc3d = point2dToVec3f(neiLoc);
                    neiList.add(neiLoc3d);
                }
                Collections.sort(neiList, new ClockWireVector(loc3d, neiList.get(0)));
                for (int i = 0; i < neiList.size(); i++) {
                    int i2 = (i == neiList.size() - 1) ? 0 : i + 1;
                    Vector3f v1 = neiList.get(i).subtract(loc3d);
                    Vector3f v2 = neiList.get(i2).subtract(loc3d);
                    Vector3f newVec = v1.normalize().add(v2.normalize());
                    newVec.normalizeLocal().multLocal(size);
                    newVec.addLocal(loc3d);
                    pointList.add(newVec);
                    System.out.println(newVec);
                }
            }
        }
    }

    Vector2f vec2fOffset(Vector2f direction, Vector2f origin, float angle, boolean clockwise) {
        Vector2f p1 = direction.normalize();
        p1.multLocal(size);
        p1.rotateAroundOrigin(angle, clockwise);
        p1.addLocal(origin);
        return p1;
    }

    Vector2f point2dToVec2f(Point2D v) {
        return new Vector2f((float) v.getX(), (float) v.getY());
    }

    void edgeAngle(float size) {

        for (Edge3DInfo e : graph.getEdges()) {
            if (graph.getIncidentCount(e) == 2) {
                ArrayList<Vector2f> endPoint2f = new ArrayList<Vector2f>();
                for (Vertex3DInfo v : graph.getEndpoints(e)) {
                    Point2D loc = layout.transform(v);
                    endPoint2f.add(point2dToVec2f(loc));
                }
                Vector2f ep1 = endPoint2f.get(0);
                Vector2f ep2 = endPoint2f.get(1);
                Vector2f v1 = ep1.subtract(ep2);
                Vector2f v2 = v1.negate();
                Vector2f p1 = vec2fOffset(v1, ep2, FastMath.HALF_PI, true);
                Vector2f p2 = vec2fOffset(v1, ep2, FastMath.HALF_PI, false);
                Vector2f p3 = vec2fOffset(v2, ep1, FastMath.HALF_PI, true);
                Vector2f p4 = vec2fOffset(v2, ep1, FastMath.HALF_PI, false);
                pointList.add(vec2fToVec3f(p1));
                pointList.add(vec2fToVec3f(p2));
                pointList.add(vec2fToVec3f(p3));
                pointList.add(vec2fToVec3f(p4));
                lineList.add(new Pair<Vector3f>(vec2fToVec3f(p2), vec2fToVec3f(p3)));
                lineList.add(new Pair<Vector3f>(vec2fToVec3f(p1), vec2fToVec3f(p4)));
            }
        }
    }

    public Vector3f vec2fToVec3f(Vector2f v) {
        return new Vector3f((float) v.getX(), 0, (float) v.getY());
    }

    public Vector3f point2dToVec3f(Point2D v) {
        return new Vector3f((float) v.getX(), 0, (float) v.getY());
    }

    public void vertexIntersect() {
        for (Vertex3DInfo v : graph.getVertices()) {
            ArrayList<Edge3DInfo> eList = new ArrayList<Edge3DInfo>(graph.getIncidentEdges(v));
            if (graph.getNeighborCount(v) > 1) {
                Collections.sort(eList, new ClockWireVertex(this, layout, v));
                ArrayList<Vector3f> crossRoadVecList = new ArrayList<Vector3f>();
                for (int i = 0; i < eList.size(); i++) {
                    int i2 = (i == eList.size() - 1) ? 0 : i + 1;
                    Edge3DInfo e1 = eList.get(i);
                    Edge3DInfo e2 = eList.get(i2);
                    Vector2f ep1 = point2dToVec2f(layout.transform(otherEnds(e1, v)));
                    Vector2f ep2 = point2dToVec2f(layout.transform(otherEnds(e2, v)));
                    Vector2f c = point2dToVec2f(layout.transform(v));
                    Vector2f v1 = c.subtract(ep1);
                    Vector2f v2 = c.subtract(ep2);
                    Vector2f p1 = vec2fOffset(v1, c, FastMath.HALF_PI, true);
                    Vector2f p2 = vec2fOffset(v1.negate(), ep1, FastMath.HALF_PI, false);
                    Vector2f p3 = vec2fOffset(v2, c, FastMath.HALF_PI, false);
                    Vector2f p4 = vec2fOffset(v2.negate(), ep2, FastMath.HALF_PI, true);
                    Vector2f result = testIntersetor(p1, p2, p3, p4);
                    if (result != null) {
                        crossRoadVecList.add(vec2fToVec3f(result));
                        if (isFirst(e1, v)) {
                            e1.point[0] = vec2fToVec3f(result);
                        } else {
                            e1.point[2] = vec2fToVec3f(result);
                        }
                        if (isFirst(e2, v)) {
                            e2.point[3] = vec2fToVec3f(result);
                        } else {
                            e2.point[1] = vec2fToVec3f(result);
                        }
                    }
                    orderLineList.add(new Pair<Vector3f>(vec2fToVec3f(c), vec2fToVec3f(ep1)));
                }
                crossRoadMap.put(v, crossRoadVecList);
            } else if (graph.getNeighborCount(v) == 1) {
                Edge3DInfo e1 = eList.get(0);
                Vector2f ep1 = point2dToVec2f(layout.transform(otherEnds(e1, v)));
                Vector2f c = point2dToVec2f(layout.transform(v));
                Vector2f v1 = c.subtract(ep1);
                Vector2f p1 = vec2fOffset(v1, c, FastMath.HALF_PI, true);
                Vector2f p2 = vec2fOffset(v1, c, FastMath.HALF_PI, false);
                System.out.println(" The vertex " + v + " is single");
                if (!isFirst(e1, v)) {
                    e1.point[2] = vec2fToVec3f(p1);
                    e1.point[1] = vec2fToVec3f(p2);
                    System.out.println(" The  vertex " + v + " is 1 - 2");
                } else {
                    e1.point[0] = vec2fToVec3f(p1);
                    e1.point[3] = vec2fToVec3f(p2);
                    System.out.println(" The  vertex " + v + " is 0 - 3");
                }
            }
        }
        System.out.println(" =========================== ");
        for (Edge3DInfo ed : graph.getEdges()) {
            for (int i = 0; i < ed.point.length; i++) {
                if (ed.point[i] == null) {
                    System.out.println(" The edge " + ed + " point " + i + " is null !");
                }
            }
            outLineList.add(new Pair<Vector3f>(ed.point[3], ed.point[2]));
            outLineList.add(new Pair<Vector3f>(ed.point[1], ed.point[0]));
            outLineList.add(new Pair<Vector3f>(ed.point[0], ed.point[3]));
            outLineList.add(new Pair<Vector3f>(ed.point[1], ed.point[2]));
        }
    }

    Vector2f testIntersetor(Vector2f pi1, Vector2f pi2, Vector2f pi3, Vector2f pi4) {
        //resetExtraDraw();
        /*
         // Test intersector
         Vector2f pi1 = new Vector2f(30, 15);
         Vector2f pi2 = new Vector2f(200, 550);
         Vector2f pi3 = new Vector2f(250, 15);
         Vector2f pi4 = new Vector2f(0, 150);
         */

        pointList.add(vec2fToVec3f(pi1));
        pointList.add(vec2fToVec3f(pi2));
        pointList.add(vec2fToVec3f(pi3));
        pointList.add(vec2fToVec3f(pi4));

        lineList.add(new Pair<Vector3f>(vec2fToVec3f(pi1), vec2fToVec3f(pi2)));
        lineList.add(new Pair<Vector3f>(vec2fToVec3f(pi3), vec2fToVec3f(pi4)));

        Intersector intersetor1 = new Intersector(pi1, pi2, pi3, pi4);
        if (!intersetor1.isCollinear()) {
            Vector2f result = intersetor1.getAnyIntersection();
            ipointList.add(vec2fToVec3f(result));
            return result;
        }
        return null;
    }

    public void setLayout(AbstractLayout<Vertex3DInfo, Edge3DInfo> layout) {
        this.layout = layout;
    }

    public void testAngle() {
        /*
         Vector2f h12 = new Vector2f(0f, -1f);
         Vector2f h9 = new Vector2f(-1f, 0f);
         Vector2f h3 = new Vector2f(1f, 0f);

         System.out.println(" " + FastMath.RAD_TO_DEG * h12.angleBetween(h3));
         System.out.println(" " + FastMath.RAD_TO_DEG * h12.angleBetween(h9));
         */
    }
}
