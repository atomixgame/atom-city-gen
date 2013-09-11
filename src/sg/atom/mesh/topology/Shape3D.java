package sg.atom.mesh.topology;

import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class Shape3D {

    String name;
    ArrayList<Vector3f> points;
    boolean curved;
    boolean pathClosed;
    Vector3f center;

    public Shape3D(String name) {
        this.name = name;
        this.points = new ArrayList<Vector3f>();
        this.center = new Vector3f();
    }

    public Shape3D(String name, ArrayList<Vector3f> points) {
        this.name = name;
        this.points = points;
    }

    public void setPoints(ArrayList<Vector3f> points) {
        this.points = points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Vector3f> getPoints() {
        return points;
    }

    public void add(Vector3f newPoint) {
        points.add(newPoint);
    }

    public boolean isCurved() {
        return curved;
    }

    public boolean isPathClosed() {
        return pathClosed;
    }

    public Spline getCurve() {
        return new Spline(Spline.SplineType.CatmullRom, points, 0.2f, pathClosed);
    }

    public void setCurved(boolean curved) {
        this.curved = curved;
    }

    public void setPathClosed(boolean pathClosed) {
        this.pathClosed = pathClosed;
    }

    public Shape3D cloneShape() {
        Shape3D result = new Shape3D(this.name + ".1");
        result.curved = this.curved;
        result.pathClosed = this.pathClosed;
        for (Vector3f v : points) {
            result.points.add(v.clone());
        }
        return result;
    }

    public void scale() {
        for (Vector3f v : points) {
            //v.
        }
    }

    public void rotate() {
    }

    public void translate() {
    }
}
