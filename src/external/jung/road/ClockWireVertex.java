/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import external.jung.road.Vertex3DInfo;
import com.jme3.math.Vector2f;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import java.util.Comparator;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class ClockWireVertex implements Comparator<Edge3DInfo> {

    Vertex3DInfo center;
    AbstractLayout layout;
    private RoadGraphHelper roadGraphHelper;

    public ClockWireVertex(RoadGraphHelper roadGraphHelper, AbstractLayout layout, Vertex3DInfo center) {
        this.center = center;
        this.layout = layout;
        this.roadGraphHelper = roadGraphHelper;
    }

    public int compare(Edge3DInfo o1, Edge3DInfo o2) {
        Vector2f zeroVec = new Vector2f(0, -1f);
        Vector2f centerVec = roadGraphHelper.point2dToVec2f(layout.transform(center));
        Vector2f o1Vec = roadGraphHelper.point2dToVec2f(layout.transform(roadGraphHelper.otherEnds(o1, center)));
        o1Vec.subtractLocal(centerVec);
        Vector2f o2Vec = roadGraphHelper.point2dToVec2f(layout.transform(roadGraphHelper.otherEnds(o2, center)));
        o2Vec.subtractLocal(centerVec);
        Vector2f axis = zeroVec.add(centerVec);
        //Vector2f axis = zeroVec;
        float a = axis.angleBetween(o1Vec) - axis.angleBetween(o2Vec);
        if (a == 0) {
            return 0;
        } else if (a > 0) {
            return 1;
        } else if (a < 0) {
            return -1;
        }
        return 0;
    }
}
