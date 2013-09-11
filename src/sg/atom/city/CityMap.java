/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.city;

import sg.atom.city.shape.House3D;
import sg.atom.city.shape.Road3D;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author hungcuong
 */
public class CityMap {

    public ArrayList<House3D> houses;
    public ArrayList<Road3D> roads;

    public CityMap() {
        houses = new ArrayList<House3D>();
        roads = new ArrayList<Road3D>();
    }
    
    public void attachMap(Node rootNode){
        for (House3D house:houses){
            rootNode.attachChild(house);
        }
    }
}
