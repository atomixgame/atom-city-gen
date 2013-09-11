/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.mesh.modifier;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import sg.atom.geo.curve.CurveHelper;
import sg.atom.mesh.topology.Shape3D;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class LoftModifier extends BaseModifier {

    int steps = 5;
    int numOfPoint = 4;
    Node debugNode;
    Vector3f direction;
    ArrayList<Vector3f> guideCloud;
    Shape3D initShape;
    Shape3D path;
    Shape3D scalePath;
    private boolean debug;
    private Material debugMat;
    boolean align;
    boolean startAtShape = false;

    public void init(AssetManager assetManager) {
        super.init(assetManager);
        debugMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        guideCloud = new ArrayList();
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

    public Mesh createLoft(boolean duplicate) {

        Mesh m;
        Shape3D scaledInitShape=this.initShape.cloneShape();
        ArrayList<Vector3f> initPoints = scaledInitShape.getPoints();
        ArrayList<Vector3f> pathPoints;
        Vector3f startPoint;
        if (this.path == null || this.path.getPoints().isEmpty()) {
            throw new IllegalStateException("Loft Path should not be null or empty!");

        }
        if (this.scalePath != null) {
            if (this.scalePath.getPoints().size() != this.path.getPoints().size()) {
                throw new IllegalStateException("Scale Path must have the same number of points as Control Path:" + this.path.getPoints().size());
            }
        }
        if (startAtShape) {
            startPoint = new Vector3f();
        } else {
            startPoint = this.path.getPoints().get(0).clone();
        }
        if (!this.path.isCurved()) {
            pathPoints = this.path.getPoints();
        } else {
            pathPoints = CurveHelper.interpolateCurve(path.getCurve(), steps);
        }


        if (debug) {
            if (debugNode == null) {
                debugNode = new Node("LoftDebugNode");
            } else {
                debugNode.detachAllChildren();
            }


            for (int i = 0; i < pathPoints.size() - 1; i++) {
                // Path
                Line segment = new Line(pathPoints.get(i), pathPoints.get(i + 1));
                Geometry segGeo = new Geometry("segment" + i, segment);
                segGeo.setMaterial(debugMat);
                debugNode.attachChild(segGeo);

                //
            }

            for (int i = 0; i < this.path.getPoints().size(); i++) {
                // Path
                Sphere sp = new Sphere(8, 8, 0.1f);
                Geometry markGeo = new Geometry("segment" + i, sp);
                markGeo.setMaterial(debugMat);
                markGeo.setLocalTranslation(this.path.getPoints().get(i));
                debugNode.attachChild(markGeo);

                //
            }

        }
        int numOfLayers;
        int pointsInRingQuad;
        if (!duplicate) {
            numOfLayers = pathPoints.size();
            pointsInRingQuad = initPoints.size();
            int pathIndex = 0;
            for (int lc = 0; lc < numOfLayers; lc++) {
                // change the direction
                if (pathIndex != pathPoints.size() - 1) {
                    this.direction = pathPoints.get(pathIndex + 1).subtract(pathPoints.get(pathIndex));
                    pathIndex++;
                }

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
                        newPoint = (Vector3f) initPoints.get(pc).add(startPoint);
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

            if (!this.path.isCurved()) {
                numOfLayers = (pathPoints.size() - 1) * 2 * steps;
            } else {
                numOfLayers = (pathPoints.size() - 1) * 2;
                steps = 1;
            }
            pointsInRingQuad = initPoints.size() * 2;
            int pathIndex = 0;
            for (int lc = 0; lc < numOfLayers; lc++) {

                if (lc % (2 * steps) == 0 && pathIndex != pathPoints.size() - 1) {
                    this.direction = pathPoints.get(pathIndex + 1).subtract(pathPoints.get(pathIndex));
                    this.direction.divideLocal(steps);
                    System.out.println(" " + this.direction);
                    pathIndex++;
                }
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
                            newPoint.addLocal(startPoint);
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setInitShape(Shape3D initShape) {
        this.initShape = initShape;
    }

    public void setPath(Shape3D path) {
        this.path = path;
    }

    public void setScalePath(Shape3D scalePath) {
        this.scalePath = scalePath;
    }

    public Shape3D getScalePath() {
        return scalePath;
    }

    public void setAlign(boolean align) {
        this.align = align;
    }

    public boolean isAlign() {
        return align;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Node getDebugNode() {
        return debugNode;
    }
}
