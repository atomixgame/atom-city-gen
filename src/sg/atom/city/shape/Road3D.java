/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.city.shape;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Spline;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Curve;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector2d;
import sg.atom.geo.curve.CurveHelper;

/**
 *
 * @author hungcuong
 */
public class Road3D extends Node {

    Vector2d start, end;
    private Node roadLineNode;
    private AssetManager assetManager;
    ArrayList<Vector3f> points;
    BitmapFont guiFont;
    private Node roadLineDebugNode;

    public Road3D(String name, AssetManager assetManager) {
        super(name);
        this.assetManager = assetManager;
    }

    public Material getRoadMat() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture roadTex = assetManager.loadTexture("Textures/Road/Asphalt1.jpg");
        roadTex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", roadTex);
        //mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        //matLighting.getAdditionalRenderState().setWireframe(true);
        return mat;
    }

    public void createRoad() {
        roadLineNode = new Node("Roadline Nodes");
        points = makeRandomRoadLine(new Vector3f(0, 0, 0), new Vector3f(15, 0, 4));
        displayRoadLine(points);
        attachChild(roadLineNode);
        makeRoadMesh(points, true);
        //makeRoadMeshSpline(points);
        createDebug();
    }

    public void displayRoadLine(ArrayList<Vector3f> points) {

        Geometry splineGeo = new Geometry("Spline ", new Curve(new Spline(Spline.SplineType.CatmullRom, points, 0.3f, false), 16));

        int numOfPatches = points.size() - 1;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        splineGeo.setMaterial(mat);
        roadLineDebugNode = new Node("RoadDebugNode");
        roadLineDebugNode.attachChild(splineGeo);
        for (int i = 0; i < points.size(); i++) {
            Vector3f p1 = points.get(i);
            if (i < points.size() - 1) {
                Vector3f p2 = points.get(i + 1);
                Line l = new Line(p1, p2);
                Geometry lgeo = new Geometry("l" + i, l);
                lgeo.setMaterial(mat);
                roadLineDebugNode.attachChild(lgeo);
            }

            Sphere sp = new Sphere(6, 6, 0.2f);
            Geometry spGeo = new Geometry("Shiny rock", sp);
            spGeo.setLocalTranslation(p1);
            spGeo.setMaterial(mat);
            roadLineDebugNode.attachChild(spGeo);
        }
        roadLineDebugNode.setLocalTranslation(0, 0.6f, 0);
        roadLineNode.attachChild(roadLineDebugNode);
    }

    public void createDebug() {
        for (int i = 0; i < points.size(); i++) {
            Vector3f p1 = points.get(i);
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText text = new BitmapText(guiFont, false);
            text.setSize(1);
            text.setText(Integer.toString(i + 1));
            text.setLocalTranslation(p1);
            text.setQueueBucket(RenderQueue.Bucket.Translucent);
            text.addControl(new BillboardControl());

            roadLineNode.attachChild(text);
        }
    }

    public ArrayList<Vector3f> makeRandomRoadLine(Vector3f start, Vector3f end) {
        ArrayList<Vector3f> roadLine = new ArrayList<Vector3f>();
        float startV = 0f;
        roadLine.add(start.clone());

        while (startV < 1) {
            Vector3f dir = end.subtract(start);
            startV += (0.1 + FastMath.nextRandomFloat() * 0.2);

            if ((startV > 1) || FastMath.abs(1 - startV) < 0.01f) {
                break;
                // connect to the end;
            } else {
                dir.multLocal(startV);
                float randomAngle = FastMath.QUARTER_PI - FastMath.HALF_PI * FastMath.nextRandomFloat();
                Quaternion yawRAngle = new Quaternion().fromAngleAxis(randomAngle, new Vector3f(0, 1, 0));
                yawRAngle.multLocal(dir);
                Vector3f newPoint = start.add(dir);
                System.out.println(startV + " " + newPoint);
                roadLine.add(newPoint.clone());
            }
        }
        roadLine.add(end.clone());
        return roadLine;
    }

    public Mesh makeRoadMesh(ArrayList<Vector3f> points, boolean smooth) {
        //float roadWidth = 1 + 5 * FastMath.nextRandomFloat();
        float roadWidth = 1;
        Mesh roadMesh = new Mesh();
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setColor("Color", ColorRGBA.Yellow);
        mat = getRoadMat();

        int numOfPatches = points.size() - 1;
        Vector3f[] vertices = new Vector3f[numOfPatches * 4];
        Vector2f[] texCoord = new Vector2f[numOfPatches * 4];
        int[] indexes = new int[numOfPatches * 6];
        float[] colorArray = new float[numOfPatches * 4];
        float[] normals = new float[numOfPatches * 12];

        Vector3f rp0 = new Vector3f();
        Vector3f rp1 = new Vector3f();
        Vector3f rp2 = new Vector3f();
        Vector3f rp3 = new Vector3f();
        Vector3f rpl2 = new Vector3f();
        Vector3f rpl3 = new Vector3f();
        Vector3f rpn0 = new Vector3f();
        Vector3f rpn1 = new Vector3f();
        Vector3f rpn2 = new Vector3f();
        Vector3f rpn3 = new Vector3f();

        float u0 = 0;
        float u1 = 0;
        float u2 = 1;
        float u3 = 1;
        for (int i = 0; i < numOfPatches; i++) {
            System.out.println((i + 1) + ")");
            Vector3f p1 = points.get(i);
            Vector3f p2 = points.get(i + 1);

            alignQuad(p1, p2, FastMath.PI / 2, roadWidth, rp0, rp1, rp2, rp3);

            float l = p1.distance(p2) / roadWidth;
            System.out.println(" Length of patch : " + l);
            if (smooth) {
                Vector3f p3;
                // Find the uv of point 0,1
                if (i > 0) {
                    float su0 = FastMath.sign(signVec(rp0, rpl2, rp0, rp2));
                    float su1 = FastMath.sign(signVec(rp1, rpl3, rp1, rp3));
                    u0 = rp0.distance(rpl2) * su0 / roadWidth;
                    u1 = rp1.distance(rpl3) * su1 / roadWidth;
                    System.out.println(" Sign u0 " + su0 + " u0: " + u0);
                    System.out.println(" Sign u1 " + su1 + " u1: " + u1);
                    rp0.set(rpl2);
                    rp1.set(rpl3);
                } else {
                    u0 = 0;
                    u1 = 0;
                }

                if (i < numOfPatches - 1) {
                    p3 = points.get(i + 2);
                    alignQuad(p2, p3, FastMath.PI / 2, roadWidth, rpn0, rpn1, rpn2, rpn3);

                    // Intersect 1
                    Vector3f i1 = new Vector3f();
                    Plane pl1 = new Plane();
                    pl1.setPlanePoints(rpn0, rpn2, rpn0.add(Vector3f.UNIT_Y));
                    Ray r1 = new Ray(rp0, rp2.subtract(rp0));
                    r1.intersectsWherePlane(pl1, i1);

                    // Intersect 1
                    Vector3f i2 = new Vector3f();
                    Plane pl2 = new Plane();
                    pl2.setPlanePoints(rpn1, rpn3, rpn1.add(Vector3f.UNIT_Y));
                    Ray r2 = new Ray(rp1, rp3.subtract(rp1));
                    r2.intersectsWherePlane(pl2, i2);

                    float su2 = FastMath.sign(signVec(i1, rp2, rp0, rp2));
                    float su3 = FastMath.sign(signVec(i2, rp3, rp1, rp3));
                    u2 = l - rp2.distance(i1) * su2 / roadWidth;
                    u3 = l - rp3.distance(i2) * su3 / roadWidth;
                    System.out.println(" Sign u2 " + su2 + " u2: " + u2);
                    System.out.println(" Sign u3 " + su3 + " u3: " + u3);
                    rp2.set(i1);
                    rp3.set(i2);

                } else {
                    u2 = l;
                    u3 = l;
                }


            } else {
                // Default Position
                rp0.y += i * 0.01f;
                rp1.y += i * 0.01f;
                // Default UV
                u0 = 0;
                u1 = 0;
                u2 = l;
                u3 = l;
            }
            u0 = 0;
            u1 = 0;
            rp2.y += i * 0.01f;
            rp3.y += i * 0.01f;
            rpl2.set(rp2);
            rpl3.set(rp3);


            //addQuad(vertices, texCoord, normals, normals, i, l, rp0, rp1, rp2, rp3);

            int vi = 0, ti = 0, ii = 0, ni = 0;
            vi = i * 4;
            ti = i * 4;
            ii = i * 6;
            ni = i * 12;

            vertices[vi + 0] = rp0.clone();
            vertices[vi + 1] = rp1.clone();
            vertices[vi + 2] = rp2.clone();
            vertices[vi + 3] = rp3.clone();


            texCoord[ti + 0] = new Vector2f(0, u0);
            texCoord[ti + 1] = new Vector2f(1, u1);
            texCoord[ti + 2] = new Vector2f(0, u2);
            texCoord[ti + 3] = new Vector2f(1, u3);


            indexes[ii] = vi + 0;
            indexes[ii + 1] = vi + 2;
            indexes[ii + 2] = vi + 1;

            indexes[ii + 3] = vi + 2;
            indexes[ii + 4] = vi + 3;
            indexes[ii + 5] = vi + 1;

            normals[ni + 0] = 0;
            normals[ni + 1] = 0;
            normals[ni + 2] = 1;
            normals[ni + 3] = 0;
            normals[ni + 4] = 0;
            normals[ni + 5] = 1;
            normals[ni + 6] = 0;
            normals[ni + 7] = 0;
            normals[ni + 8] = 1;
            normals[ni + 9] = 0;
            normals[ni + 10] = 0;
            normals[ni + 11] = 1;

            System.out.println("------------------------------");
        }

        // Set the buffers
        roadMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        roadMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        roadMesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        roadMesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        // move mesh a bit so that it doesn't intersect with the first one
        //roadMesh.setMode(Mesh.Mode.Points);
        //roadMesh.setPointSize(4f);
        roadMesh.updateBound();
        roadMesh.setStatic();
        Geometry geom = new Geometry("RoadGeo", roadMesh);
        geom.setMaterial(mat);
        //mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setLocalTranslation(0, -0.1f, 0);
        roadLineNode.attachChild(geom);
        return roadMesh;
    }

    float signVec(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        return FastMath.cos(v2.subtract(v1).angleBetween(v4.subtract(v3)));
    }

    void alignQuad(Vector3f p1, Vector3f p2, float angle, float roadWidth, Vector3f rp0, Vector3f rp1, Vector3f rp2, Vector3f rp3) {
        Vector3f addVec = new Vector3f(1, 0, 0);
        Quaternion YAW090 = new Quaternion().fromAngleAxis(angle, new Vector3f(0, 1, 0));
        addVec = YAW090.multLocal(p1.subtract(p2).normalize().mult(roadWidth));

        rp0.set(p1.add(addVec));
        rp1.set(p1.add(addVec.negate()));
        rp2.set(p2.add(addVec));
        rp3.set(p2.add(addVec.negate()));
        //System.out.println(" " + rp0 + " " + rp1 + " " + rp2 + " " + rp3);
    }

    void addQuad(Vector3f[] vertices, Vector2f[] texCoord, float[] indexes, float[] normals, int i, float l, Vector3f rp0, Vector3f rp1, Vector3f rp2, Vector3f rp3) {
        int vi = 0, ti = 0, ii = 0, ni = 0;
        vi = i * 4;
        ti = i * 4;
        ii = i * 6;
        ni = i * 12;

        vertices[vi + 0] = rp0;
        vertices[vi + 1] = rp1;
        vertices[vi + 2] = rp2;
        vertices[vi + 3] = rp3;


        texCoord[ti + 0] = new Vector2f(0, 0);
        texCoord[ti + 1] = new Vector2f(1, 0);
        texCoord[ti + 2] = new Vector2f(0, l);
        texCoord[ti + 3] = new Vector2f(1, l);

        indexes[ii] = vi + 0;
        indexes[ii + 1] = vi + 2;
        indexes[ii + 2] = vi + 1;
        indexes[ii + 3] = vi + 2;
        indexes[ii + 4] = vi + 3;
        indexes[ii + 5] = vi + 1;

        normals[ni + 0] = 0;
        normals[ni + 1] = 1;
        normals[ni + 2] = 1;
        normals[ni + 3] = 0;
        normals[ni + 4] = 1;
        normals[ni + 5] = 1;
        normals[ni + 6] = 0;
        normals[ni + 7] = 1;
        normals[ni + 8] = 1;
        normals[ni + 9] = 0;
        normals[ni + 10] = 1;
        normals[ni + 11] = 1;
    }

    public Mesh makeRoadMeshSpline(ArrayList<Vector3f> controlPoints) {
        return makeRoadMesh(new Spline(Spline.SplineType.CatmullRom, controlPoints, 0.3f, false));
    }

    public Mesh makeRoadMesh(Spline roadLine) {
        ArrayList<Vector3f> interPoints = CurveHelper.interpolateCurve(roadLine, 5);
        return makeRoadMesh(interPoints, true);
    }
}
