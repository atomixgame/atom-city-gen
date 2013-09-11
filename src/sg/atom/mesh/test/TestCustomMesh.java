package sg.atom.mesh.test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Canvas;
import sg.atom.mesh.modifier.ExtrudeModifier;
import sg.atom.mesh.modifier.LoftModifier;
import sg.atom.mesh.topology.Shape3D;

public class TestCustomMesh extends SimpleApplication {

    int layer = 10;
    int numOfPoint = 8;
    Shape3D initShape;
    Vector3f direction;
    private Material matLighting;
    int currentTexture = 0;
    String[] textureList = {"windows_01.jpg", "windows_02.jpg", "windows_03.jpg"};

    public static void main(String[] args) {
        TestCustomMesh app = new TestCustomMesh();
        app.setShowSettings(false);
        app.start();
    }

    public Canvas createAndStartCanvas(int width, int height) {
        AppSettings settings = new AppSettings(true);
        settings.setWidth(width);
        settings.setHeight(height);

        setPauseOnLostFocus(true);
        setSettings(settings);
        createCanvas();
        startCanvas(true);

        JmeCanvasContext context = (JmeCanvasContext) getContext();
        Canvas canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());

        return canvas;
    }

    void fillInitShape(float radius) {
        this.initShape = new Shape3D("Shape1");
        if (!this.initShape.getPoints().isEmpty()) {
            this.initShape.getPoints().clear();
        }
        float angle = 0.0F;
        for (int i = 0; i < this.numOfPoint; i++) {
            angle += 6.283186F / this.numOfPoint;
            angle %= 6.283186F;
            Vector3f pl = new Vector3f(FastMath.cos(angle) * radius, 0.5F, FastMath.sin(angle) * radius);
            this.initShape.getPoints().add(pl);
        }
    }

    protected void createMark(Vector3f loc) {
        Sphere sphere = new Sphere(8, 8, 0.2f);
        Geometry mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        mark.setLocalTranslation(loc.clone());
        rootNode.attachChild(mark);
    }

    void createLight() {
        /**
         * Must add a light to make the lit object visible!
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(40f);
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        createGrid(40, 40);
        fillInitShape(2f);
        //createExtrude(true);
        createLoft(true);

        createLight();
        initInput();
    }

    void createCustomMesh() {
    }

    public void createGrid(int gw, int gh) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        Grid grid = new Grid(gw - 1, gh - 1, 1);
        Geometry gridGeo = new Geometry("Grid", grid);
        gridGeo.setMaterial(mat);
        gridGeo.setLocalTranslation(-gw / 2, 0f, -gh / 2);
        rootNode.attachChild(gridGeo);

        Line gridx = new Line(new Vector3f(gw, 0, 0), new Vector3f(-gw, 0, 0));
        Geometry gridXGeo = new Geometry("GridX", gridx);
        Material redMat = mat.clone();
        redMat.setColor("Color", ColorRGBA.Red);
        gridXGeo.setMaterial(redMat);
        rootNode.attachChild(gridXGeo);
        gridXGeo.setLocalTranslation(0, 0.01f, 0);
        Line gridy = new Line(new Vector3f(0, 0, -gh), new Vector3f(0, 0, gh));
        Geometry gridYGeo = new Geometry("GridY", gridy);
        Material blueMat = mat.clone();
        blueMat.setColor("Color", ColorRGBA.Blue);
        gridYGeo.setLocalTranslation(0, 0.01f, 0);
        gridYGeo.setMaterial(blueMat);
        rootNode.attachChild(gridYGeo);
    }

    void createLoft(boolean duplicate) {
        LoftModifier loft = new LoftModifier();
        loft.init(assetManager);
        loft.setInitShape(initShape);
        Shape3D loftPath = new Shape3D("Path");

        loftPath.add(new Vector3f(1, 0, 6));
        loftPath.add(new Vector3f(1, 5, 1));
        loftPath.add(new Vector3f(-2, 10, -2));
        loftPath.add(new Vector3f(3, 15, 3));
        loftPath.add(new Vector3f(-4, 20, -4));
        loftPath.setCurved(true);
        loftPath.setPathClosed(false);
        loft.setDebug(true);
        loft.setPath(loftPath);
        Mesh m = loft.createLoft(duplicate);

        Mesh wfMesh = m.clone();
        Geometry wfGeom = new Geometry("wireframeGeometry", wfMesh);
        Material matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
        matWireframe.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        m.setPointSize(2f);
        wfGeom.setMaterial(matWireframe);
        wfGeom.setLocalTranslation(0, 0, 0);

        rootNode.attachChild(wfGeom);
        rootNode.attachChild(loft.getDebugNode());
        Mesh lightingMesh = m.clone();
        Geometry lightingGeom = new Geometry("lightGeometry", lightingMesh);
        matLighting = getLightMat();
        lightingGeom.setMaterial(matLighting);
        lightingGeom.setLocalTranslation(4, 0, 4);

        rootNode.attachChild(lightingGeom);
    }

    void createExtrude(boolean duplicate) {
        ExtrudeModifier extrude = new ExtrudeModifier();
        extrude.init(assetManager);
        extrude.setInitShape(initShape);
        Mesh m = extrude.createExtrude(duplicate);

        Mesh wfMesh = m.clone();
        Geometry wfGeom = new Geometry("wireframeGeometry", wfMesh);
        Material matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
        matWireframe.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        m.setPointSize(2f);
        wfGeom.setMaterial(matWireframe);
        wfGeom.setLocalTranslation(0, 0, 0);

        rootNode.attachChild(wfGeom);

        Mesh lightingMesh = m.clone();
        Geometry lightingGeom = new Geometry("lightGeometry", lightingMesh);
        matLighting = getLightMat();
        lightingGeom.setMaterial(matLighting);
        lightingGeom.setLocalTranslation(4, 0, 4);

        rootNode.attachChild(lightingGeom);
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean pressed, float tpf) {
            if (name.equals("changeTexture") && pressed) {
                String nextTexture = textureList[currentTexture];
                if (currentTexture < textureList.length - 1) {
                    currentTexture++;
                } else {
                    currentTexture = 0;
                }
                matLighting.setTexture("DiffuseMap", assetManager.loadTexture("Textures/" + nextTexture));
            }
        }
    };

    public Material getUnshadeMat() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        return mat;
    }

    public Material getLightMat() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Building/windows_01.jpg"));
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        //matLighting.getAdditionalRenderState().setWireframe(true);
        return mat;
    }

    public void initInput() {
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        // Test multiple inputs per mapping
        inputManager.addMapping("changeTexture",
                new KeyTrigger(KeyInput.KEY_SPACE));

        // Test multiple listeners per mapping
        inputManager.addListener(actionListener, "changeTexture");
        cam.setLocation(new Vector3f(-10, 10, -10));
        cam.lookAt(Vector3f.ZERO.clone(), Vector3f.UNIT_Y.clone());
    }
}
