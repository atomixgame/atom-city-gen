package sg.atom.terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.AppSettings;
import java.util.HashMap;

public class TestTerrainApplication extends SimpleApplication {

    ExTerrain terrain;
    private HashMap<String, JmeCursor> cursors = new HashMap<String, JmeCursor>();
    Node gizmo = new Node("gizmo");

    public static void main(String[] args) {
        TestTerrainApplication app = new TestTerrainApplication();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);
        app.setDisplayStatView(true);
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initCurrors();
        buildExTerrain();
        createLight();
        makeGizmo();
        setupInput();
    }

    void initCurrors() {
        cursors.put("normal", (JmeCursor) assetManager.loadAsset("Textures/Cursors/TRONnormal.ani"));
        inputManager.setMouseCursor(cursors.get("normal"));
        inputManager.setCursorVisible(true);
        //flyCam.setEnabled(true);
        flyCam.setMoveSpeed(40f);
        cam.setLocation(new Vector3f(5, 20, 4));
        cam.lookAt(new Vector3f(5, 0, 5), Vector3f.UNIT_X);


        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
    }

    void buildExTerrain() {
        terrain = new ExTerrain(assetManager, 10, 10);
        rootNode.attachChild(terrain);
    }

    void createLight() {
        /**
         * A white, spot light source.
         */
        PointLight lamp = new PointLight();
        lamp.setPosition(Vector3f.ZERO);
        lamp.setColor(ColorRGBA.White);
        rootNode.addLight(lamp);
        /**
         * Advanced shadows for uneven surfaces
         */
        /*
        PssmShadowRenderer pssm = new PssmShadowRenderer(assetManager, 1024, 3);
        pssm.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        viewPort.addProcessor(pssm);
        */ 

    }

    public Geometry putShape(Node node, Mesh shape, ColorRGBA color) {
        Geometry g = new Geometry("shape", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        node.attachChild(g);
        return g;
    }

    public void putArrow(Vector3f pos, Vector3f dir, ColorRGBA color) {
        Arrow arrow = new Arrow(dir);
        arrow.setLineWidth(4); // make arrow thicker

        putShape(gizmo, arrow, color).setLocalTranslation(pos);
        rootNode.attachChild(gizmo);
        gizmo.scale(1);
    }

    void makeGizmo() {
        putArrow(Vector3f.ZERO, Vector3f.UNIT_X, ColorRGBA.Red);
        putArrow(Vector3f.ZERO, Vector3f.UNIT_Y, ColorRGBA.Green);
        putArrow(Vector3f.ZERO, Vector3f.UNIT_Z, ColorRGBA.Blue);
    }

    void setupInput() {
        /**
         * Map one or more inputs to an action
         */
        inputManager.addMapping("paint",
                new KeyTrigger(KeyInput.KEY_SPACE),
                new MouseAxisTrigger(MouseInput.BUTTON_LEFT, false));
        /**
         * Add an action to one or more listeners
         */
        //inputManager.addListener(analogListener, "paint");

    }
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals("paint")) {

                // Convert screen click to 3d position
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                // Aim the ray from the clicked spot forwards.
                Ray ray = new Ray(click3d, dir);
                // Collect intersections between ray and all nodes in results list.

                terrain.paint(ray);
            }
        }
    };
}
