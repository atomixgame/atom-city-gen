/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.mesh.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.system.AppSettings;
import sg.atom.mesh.hemesh.HeMeshHelper;

/**
 *
 * Collection of export functions.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class TestHeMesh extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        HeMeshHelper hmHelper = new HeMeshHelper();
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        //setDisplayStatView(false);
        //setDisplayFps(false);
        /**
         * Illuminated bumpy rock with shiny effect. Uses Texture from
         * jme3-test-data library! Needs light source!
         */
        Mesh mesh = hmHelper.toJMesh(hmHelper.createCustomLatice());
        Geometry geo = new Geometry("Shiny rock", mesh);
        /**
         * A white, spot light source.
         */
        PointLight lamp = new PointLight();
        lamp.setPosition(Vector3f.ZERO);
        lamp.setColor(ColorRGBA.White);
        rootNode.addLight(lamp);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");

        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
        //mat.setColor("Color", new ColorRGBA(1f, 0f, 1f, 1f)); // purple

        /*
         //TangentBinormalGenerator.generate(rock);   // for lighting effect
         Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
         mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
         mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
         mat.setBoolean("UseMaterialColors", true);  // needed for shininess
         mat.setColor("Specular", ColorRGBA.White); // needed for shininess
         mat.setColor("Diffuse", ColorRGBA.White); // needed for shininess
         mat.setFloat("Shininess", 5f); // shininess from 1-128
         */
        //mat.getAdditionalRenderState().setWireframe(true);
        geo.setMaterial(mat);


        rootNode.attachChild(geo);

    }

    public static void main(String[] args) {
        TestHeMesh app = new TestHeMesh();
        AppSettings cfg = new AppSettings(true);
        //cfg.setFrameRate(60); // set to less than or equal screen refresh rate
        //cfg.setVSync(true);   // prevents page tearing
        cfg.setFrequency(60); // set to screen refresh rate
        cfg.setResolution(800, 600);
        //cfg.setFullscreen(true);
        cfg.setSamples(2);    // anti-aliasing
        cfg.setTitle("Test HEMesh"); // branding: window name
        app.setShowSettings(false); // or don't display splashscreen
        app.setSettings(cfg);

        app.start();
    }
}
