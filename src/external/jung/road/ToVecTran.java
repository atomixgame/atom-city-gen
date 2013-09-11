/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import com.jme3.math.Vector3f;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class ToVecTran implements Transformer<Vertex3DInfo, Vector3f> {

    public Vector3f transform(Vertex3DInfo i) {
        return new Vector3f();
    }
    
}
