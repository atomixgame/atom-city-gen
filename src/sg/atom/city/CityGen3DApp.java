package sg.atom.city;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import java.awt.Canvas;
import java.util.logging.Level;
import java.util.logging.Logger;
import sg.atom.city.shape.Road3D;
import sg.atom.mesh.topology.Shape3D;
import sg.atom.mesh.modifier.ExtrudeModifier;
import tonegod.skydome.SkyDome;

public class CityGen3DApp extends SimpleApplication {

    int layer = 10;
    int numOfPoint = 4;
    Shape3D initShape;
    Vector3f direction;
    private Material matLighting;
    int currentTexture = 0;
    String[] textureList = {"window.jpg", "windows2.jpg", "windows_big.JPG"};
    private Geometry ground;
    private Material matGroundL;
    private DirectionalLightShadowRenderer dlsr;
    private DirectionalLightShadowFilter dlsf;
    private Road3D road;

    public static void main(String[] args) {
        CityGen3DApp app = new CityGen3DApp();
        app.setShowSettings(false);
        app.start();

        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(40f);
        //fillInitShape(2f);
        //createExtrude(true);

        createTerrain();
        createGrid(40, 40);
        createRoad();
        //createInteriorMappingHouse();

        createLight(false);
        initInput();
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

        viewPort.setBackgroundColor(ColorRGBA.Blue);
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

    protected void createMark(Vector3f loc) {
        Sphere sphere = new Sphere(8, 8, 0.2f);
        Geometry mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        mark.setLocalTranslation(loc.clone());
        rootNode.attachChild(mark);
    }

    void createLight(boolean makeSkydome) {
        /**
         * LIGHTS
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -4, -1).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
        rootNode.addLight(sun);
        /**
         * A white, spot light source.
         */
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0, 20, 20));
        lamp.setColor(ColorRGBA.White);
        rootNode.addLight(lamp);

        dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
        dlsr.setLight(sun);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        //dlsr.displayDebug();
        viewPort.addProcessor(dlsr);

        /*
         dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 3);
         dlsf.setLight(sun);
         dlsf.setLambda(0.55f);
         dlsf.setShadowIntensity(0.6f);
         dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
         dlsf.setEnabled(true);

         FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
         fpp.addFilter(dlsf);

         viewPort.addProcessor(fpp);
         */
        // SKY
        if (!makeSkydome) {
            Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
            sky.setLocalScale(350);

            rootNode.attachChild(sky);
        } else {
            // Toneod sky
            SkyDome skyDome = new SkyDome(assetManager, cam,
                    "tonegod/skydome/models/SkyDome.j3o",
                    "tonegod/skydome/textures/SkyNight_L.png",
                    "tonegod/skydome/textures/Sun_L.png",
                    "tonegod/skydome/textures/Moon_L.png",
                    "tonegod/skydome/textures/Clouds_L.png",
                    "tonegod/skydome/textures/Fog_Alpha.png");
            Node sky = new Node();
            sky.setQueueBucket(Bucket.Sky);
            sky.addControl(skyDome);
            sky.setCullHint(Spatial.CullHint.Never);

            // Either add a reference to the control for the existing JME fog filter or use the one I posted…
            // But… REMEMBER!  If you use JME’s… the sky dome will have fog rendered over it.
            // Sorta pointless at that point
            //        FogFilter fog = new FogFilter(ColorRGBA.Blue, 0.5f, 10f);
            //        skyDome.setFogFilter(fog, viewPort);

            // Set some fog colors… or not (defaults are cool)
            skyDome.setFogColor(ColorRGBA.Blue);
            skyDome.setFogNightColor(new ColorRGBA(0.5f, 0.5f, 1f, 1f));
            skyDome.setDaySkyColor(new ColorRGBA(0.5f, 0.5f, 0.9f, 1f));

            // Enable the control to modify the fog filter
            //skyDome.setControlFog(true);

            // Add the directional light you use for sun… or not
            skyDome.setSun(sun);


            // Set some sunlight day/night colors… or not
            skyDome.setSunDayLight(new ColorRGBA(1, 1f, 1f, 1));
            skyDome.setSunNightLight(new ColorRGBA(0.5f, 0.5f, 0.9f, 1f));

            // Enable the control to modify your sunlight
            skyDome.setControlSun(true);

            // Enable the control
            skyDome.setEnabled(true);
            skyDome.cycleNightToDay();
            // Add the skydome to the root… or where ever
            rootNode.attachChild(sky);

        }

    }

    public void createGrid(int gw, int gh) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.DarkGray);

        Grid grid = new Grid(gw - 1, gh - 1, 1);
        Geometry gridGeo = new Geometry("Grid", grid);
        gridGeo.setMaterial(mat);
        gridGeo.setLocalTranslation(-gw / 2, -0.5f, -gh / 2);
        rootNode.attachChild(gridGeo);
    }
// EXTRUDE FUNCTION

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

    void createExtrude(boolean duplicate) {
        ExtrudeModifier extrude = new ExtrudeModifier();
        extrude.init(assetManager);
        extrude.setInitShape(initShape);
        Mesh m = extrude.createExtrude(duplicate);

        // *************************************************************************
        // Third mesh will use a wireframe shader to show wireframe
        // *************************************************************************
        Mesh wfMesh = m.clone();
        Geometry wfGeom = new Geometry("wireframeGeometry", wfMesh);
        Material matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
        //matWireframe.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
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
        //Texture window = assetManager.loadTexture("Textures/window.jpg");
        //window.setWrap(Texture.WrapMode.Repeat);
        //mat.setTexture("DiffuseMap", window);
        //mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        //matLighting.getAdditionalRenderState().setWireframe(true);
        return mat;
    }

    public void createTerrain() {
        Box b = new Box(new Vector3f(0, -1, 550), 1000, 0.01f, 1000);
        b.scaleTextureCoordinates(new Vector2f(40, 40));
        ground = new Geometry("soil", b);
        matGroundL = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matGroundL.setTexture("DiffuseMap", grass);

        ground.setMaterial(matGroundL);

        ground.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(ground);

    }

    private void createRoad() {
        road = new Road3D("Road1", assetManager);
        road.createRoad();
        rootNode.attachChild(road);
    }

    void createInteriorMappingHouse() {

        Box box = new Box(Vector3f.ZERO, 4f, 4f, 4f);
        Geometry house = new Geometry("Interior", box);
        house.setLocalTranslation(0, 2f, 0);
        Material houseMat = assetManager.loadMaterial("Materials/SimpleHouse/SimpleHouse.j3m");
        //house.setQueueBucket(Bucket.Transparent);
        //houseMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        //houseMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        house.setMaterial(houseMat);

        rootNode.attachChild(house);
    }
}
