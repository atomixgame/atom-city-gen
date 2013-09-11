package sg.atom.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author hungcuong
 */
public class ExTerrain extends Node {

    private int width;
    private int height;
    private ExTerrainMesh mesh;
    private Geometry terrainGeo;
    private Material terrainMat;

    public ExTerrain(AssetManager assetManager, int width, int height) {
        super("Terrain");
        this.width = width;
        this.height = height;
        createTerrainMesh(assetManager);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private void createTerrainMesh(AssetManager assetManager) {
        mesh = new ExTerrainMesh(width, height, new TerrainTileSheet(4, 4));
        terrainGeo = new Geometry("TerrainGeo", mesh);
        terrainMat = assetManager.loadMaterial("Materials/ExTerrain/ExTerrain.j3m");
        terrainMat.getAdditionalRenderState().setWireframe(true);
        terrainGeo.setMaterial(terrainMat);
        terrainGeo.setCullHint(CullHint.Never);
        terrainGeo.setShadowMode(ShadowMode.CastAndReceive);
        this.attachChild(terrainGeo);
    }

    public void paint(Ray ray) {
        Vector2f intersect = mesh.intersectWherePlanar(ray);
        mesh.paint((int) intersect.x, (int) intersect.y, 15);
    }
}
