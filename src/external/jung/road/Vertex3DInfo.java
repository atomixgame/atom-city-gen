/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import com.jme3.math.Vector3f;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class Vertex3DInfo {
    int index;
    Point2D loc;
    ArrayList<Vector3f> point;

    Vertex3DInfo(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return String.valueOf(index);
    }
    
}
