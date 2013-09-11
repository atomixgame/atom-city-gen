/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sg.atom.shapegrammar.production

import com.jme3.scene.shape.Line;
import com.jme3.scene.Geometry;
public class LineBuilder{
    
    public static def build(name,pointList){
        def p1=pointList.get(0);
        def p2=pointList.get(1);
        return build(name,p1,p2)
    }
    public static def build(name,p1,p2){
        def line = new Line(p1,p2);
        Geometry geo =new Geometry(name,line)
        return geo;
    }
}
