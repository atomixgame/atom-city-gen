/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import com.jme3.math.Vector3f;
import java.util.Comparator;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class ClockWireVector implements Comparator<Vector3f> {

    Vector3f center;
    Vector3f zero;

    public ClockWireVector(Vector3f center, Vector3f zero) {
        this.center = center.clone();
        this.zero = zero.clone();
    }

    public int compare(Vector3f o1, Vector3f o2) {
        Vector3f axis = zero.subtract(center);
        float a = axis.angleBetween(o1.clone().subtract(center)) - axis.angleBetween(o2.clone().subtract(center));
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
