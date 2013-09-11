/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import com.jme3.math.Vector3f;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class Edge3DInfo {
    int index;
    Vector3f[] point;

    Edge3DInfo(int index) {
        this.index = index;
        point = new Vector3f[4];
    }

    @Override
    public String toString() {
        return String.valueOf(index);
    }
    
}
