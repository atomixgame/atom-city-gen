/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.city.shape;

import sg.atom.city.CityGen3DApp;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author hungcuong
 */
public class House3DCreator {

    private final CityGen3DApp app;
    private float scaleFactor = 10f;

    public House3DCreator(CityGen3DApp app) {
        this.app = app;
    }

    public House3D createHouse(float x, float y, float hw, float hl, float hz) {
        House3D aHouse = new House3D(new Vector2f(x, y));
        Vector2f scaleTex = new Vector2f(hw, hz);
        // Build a house from geometry
        hz *= 1f;
        hw *= 4f;
        hl *= 4f;
        Box box = new Box(hw, hz, hl);
        // Set location by 
        Geometry houseGeo = new Geometry("houseGeo", box);
        //box.scaleTextureCoordinates(scaleTex);
        //Material houseGeoMat = getLightMat();
        Material houseGeoMat = app.getAssetManager().loadMaterial("Materials/SimpleHouse/SimpleHouse.j3m");
        houseGeo.setMaterial(houseGeoMat);
        aHouse.setLocalTranslation(new Vector3f(x * scaleFactor, hz / 2, y * scaleFactor));

        houseGeo.setShadowMode(RenderQueue.ShadowMode.Cast);
        aHouse.attachChild(houseGeo);
        return aHouse;
    }

    public Material getLightMat() {
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture window;
        int randomTexNum = FastMath.nextRandomInt(1, 6);
        window = app.getAssetManager().loadTexture("Textures/Building/windows_0" + randomTexNum + ".jpg");

        window.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", window);
        //mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        //matLighting.getAdditionalRenderState().setWireframe(true);
        return mat;
    }
}
