/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package external.jung.road;

import org.apache.commons.collections15.Factory;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class VertexFactory implements Factory<Vertex3DInfo> {
    int i = 0;

    public Vertex3DInfo create() {
        return new Vertex3DInfo(i++);
    }
    
}
