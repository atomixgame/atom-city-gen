/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.geo.curve;

import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungcuong
 */
public class CurveHelper {

    public static ArrayList<Vector3f> interpolateCurve(Spline curve, int segs) {
        List<Vector3f> controlPoints = curve.getControlPoints();
        ArrayList<Vector3f> interPoints = new ArrayList<Vector3f>();
        for (int i = 0; i < controlPoints.size() - 1; i++) {
            interPoints.add(controlPoints.get(i).clone());
            for (int j = 1; j < segs; j++) {
                Vector3f newPoint = new Vector3f();
                curve.interpolate((float) j / segs, i, newPoint);
                interPoints.add(newPoint);
            }
        }
        interPoints.add(controlPoints.get(controlPoints.size() - 1).clone());
        return interPoints;
    }
}
