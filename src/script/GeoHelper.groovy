/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package script
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Line;

class GeoHelper{
    public static def scale3D = 0.1f
    public static def toVec3f(c1){
        return new Vector3f((float)c1.x * scale3D,0f,(float)c1.y * scale3D);
    }
    
    public static def to3DPointList(poly){
        def pointList=[]
        def arr = poly.getCoordinates()
        for (int i=0;i<arr.length-1;i++){
            def c1=arr[i]
            def p1=toVec3f(c1)
            pointList<<p1
        }
        return pointList
    }
    
    public static def to3DLines(poly){
        def lineList=[]
        def arr = poly.getCoordinates()
        for (int i=0;i<arr.length-1;i++){
            def c1=arr[i]
            def c2=arr[i+1]
            def p1=toVec3f(c1)
            def p2=toVec3f(c2)
            def line = new Line(p1,p2);
            lineList<<line
        }
        return lineList
    }
    
    public static def draw3DGeos(rootNode,list){
        list.each{geo->
            rootNode.attachChild(geo)
        }
    }
}
