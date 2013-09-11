/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.city.shape;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author hungcuong
 */
public class House3D extends Node{

    String name = "EmptyHouse";
    public Vector2f gridPos = new Vector2f();
    Facade decoration;
    ArrayList<Floor> floors;
    float height;
    int floorNum = 1;

    public class Facade {

        String imagePath;
    }

    public class Floor {

        int rooms;
        String imagePath;
        String name;
        float height;
    }

    public House3D(Vector2f gridPos) {
        this.gridPos = gridPos;
        
        
    }
}
