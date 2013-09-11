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
public class EdgeFactory implements Factory<Edge3DInfo> {
    int i = 0;

    public Edge3DInfo create() {
        return new Edge3DInfo(i++);
    }
    
}
