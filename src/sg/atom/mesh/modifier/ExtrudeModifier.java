/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.mesh.modifier;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import java.util.ArrayList;
import sg.atom.mesh.topology.Shape3D;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class ExtrudeModifier extends BaseModifier {

    int layers = 10;
    int numOfPoint = 4;
    Vector3f direction;
    Shape3D initShape;
    ArrayList<Vector3f> guideCloud;

    public void init(AssetManager assetManager) {
        super.init(assetManager);
        guideCloud = new ArrayList();
        this.direction = new Vector3f(Vector3f.UNIT_Y);
    }

    public void reset() {
        if (!initShape.getPoints().isEmpty()) {
            initShape.getPoints().clear();
        }
        if (!guideCloud.isEmpty()) {
            guideCloud.clear();
        }
        if (!pointCloud.isEmpty()) {
            pointCloud.clear();
        }
        if (!uvList.isEmpty()) {
            uvList.clear();
        }
        if (!indexList.isEmpty()) {
            indexList.clear();
        }
    }

    public Mesh createExtrude(boolean duplicate) {

        Mesh m;
        ArrayList<Vector3f> initPoints = this.initShape.getPoints();
        int numOfLayers;
        int pointsInRingQuad;
        
        if (!duplicate) {
            numOfLayers = this.layers;
            pointsInRingQuad = initPoints.size();
            for (int lc = 0; lc < numOfLayers; lc++) {
                for (int pc = 0; pc < pointsInRingQuad; pc++) {
                    int pi0, pi1, pi2, pi3;
                    pi0 = (lc - 1) * pointsInRingQuad + pc;

                    if (pc != numOfLayers - 1) {
                        pi1 = pi0 + 1;
                        pi2 = pi1 + pointsInRingQuad;
                    } else {
                        pi1 = (lc - 1) * pointsInRingQuad;
                        pi2 = lc * pointsInRingQuad;
                    }
                    pi3 = pi0 + pointsInRingQuad;
                    Vector3f newPoint;
                    if (lc == 0) {
                        newPoint = (Vector3f) initPoints.get(pc);
                    } else {
                        Vector3f previousPoint = (Vector3f) this.pointCloud.get(pi0);

                        newPoint = previousPoint.add(this.direction);

                        this.indexList.add(Integer.valueOf(pi0));
                        this.indexList.add(Integer.valueOf(pi3));
                        this.indexList.add(Integer.valueOf(pi2));
                        this.indexList.add(Integer.valueOf(pi0));
                        this.indexList.add(Integer.valueOf(pi2));
                        this.indexList.add(Integer.valueOf(pi1));
                    }
                    this.pointCloud.add(newPoint);

                    this.uvList.add(new Vector2f(0.0F, 0.0F));
                }
            }
        } else {
            numOfLayers = this.layers * 2 - 2;
            pointsInRingQuad = initPoints.size() * 2;
            for (int lc = 0; lc < numOfLayers; lc++) {
                for (int pc = 0; pc < pointsInRingQuad; pc++) {
                    int pi0, pi1, pi2, pi3;
                    pi0 = (lc - 1) * pointsInRingQuad + pc;


                    if (pc != pointsInRingQuad - 1) {
                        pi1 = pi0 + 1;
                        pi2 = pi1 + pointsInRingQuad;
                    } else {
                        pi1 = (lc - 1) * pointsInRingQuad;
                        pi2 = lc * pointsInRingQuad;
                    }
                    pi3 = pi0 + pointsInRingQuad;
                    Vector3f newPoint;

                    if (lc == 0) {
                        if (pc % 2 != 0) {
                            newPoint = (Vector3f) initPoints.get((pc - 1) / 2);
                        } else {
                            newPoint = (Vector3f) initPoints.get(pc / 2);
                        }

                    } else if ((lc - 1) % 2 != 0) {
                        // Odd layer
                        Vector3f previousPoint = (Vector3f) this.pointCloud.get(pi0);

                        newPoint = previousPoint.add(this.direction);

                        // Odd point
                        if (pc % 2 != 0) {
                            this.indexList.add(Integer.valueOf(pi0));
                            this.indexList.add(Integer.valueOf(pi3));
                            this.indexList.add(Integer.valueOf(pi2));
                            this.indexList.add(Integer.valueOf(pi0));
                            this.indexList.add(Integer.valueOf(pi2));
                            this.indexList.add(Integer.valueOf(pi1));
                            this.uvList.add(new Vector2f(0.0F, 0.0F));
                        } else {
                            this.uvList.add(new Vector2f(1.0F, 0.0F));
                        }
                    } else {
                        Vector3f previousPoint = (Vector3f) this.pointCloud.get(pi0);
                        newPoint = previousPoint.clone();

                        // Odd point
                        if (pc % 2 != 0) {
                            this.uvList.add(new Vector2f(0.0F, 1.0F));
                        } else {
                            this.uvList.add(new Vector2f(1.0F, 1.0F));
                        }

                    }

                    this.pointCloud.add(newPoint);
                }
            }
        }
        m = makeMeshFrom(this.pointCloud, this.uvList, this.indexList);
        return m;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    public void setInitShape(Shape3D initShape) {
        this.initShape = initShape;
    }
}
