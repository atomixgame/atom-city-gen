package sg.atom.shapegrammar.production
import com.jme3.scene.shape.Box;
import com.jme3.math.*;
import com.jme3.scene.Geometry;

public class BoxBuilder{
    public static def build(name,p0,p1,p2,p5){
        Vector3f wv = p0.subtract(p1)
        Vector3f lv = p2.subtract(p1)
        Vector3f hv = p5.subtract(p1)
        
        Vector3f wvn = wv.normalize()
        Vector3f lvn = lv.normalize()
        Vector3f hvn = hv.normalize()
        
        float w = p1.distance(p0)/2f
        float l = p1.distance(p2)/2f 
        float h = p1.distance(p5)/2f
        
        Quaternion rot = new Quaternion();
        rot.fromAxes(wvn,hvn,lvn)
        
        println wvn.toString() + " " + hvn.toString() + " " + lvn.toString()
        
        def box = new Box(new Vector3f(Vector3f.ZERO),w,h,l)
        Geometry geo =new Geometry(name,box)
        Vector3f loc = p1.clone()
        loc.addLocal(wv.mult(0.5f))
        loc.addLocal(lv.mult(0.5f))
        loc.addLocal(hv.mult(0.5f))
        
        geo.setLocalTranslation(loc)
        geo.setLocalRotation(rot)
        
        return geo;
    }
}
