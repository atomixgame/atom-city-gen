package sg.atom.builder;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Grid;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class EmptySimpleApplication extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(40f);
        createGrid(40, 40);
        initInput();
    }

    public void initInput() {
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);

        cam.setLocation(new Vector3f(-10, 10, -10));
        cam.lookAt(Vector3f.ZERO.clone(), Vector3f.UNIT_Y.clone());

        viewPort.setBackgroundColor(ColorRGBA.Blue);
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
}
