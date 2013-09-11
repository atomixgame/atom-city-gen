/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.city.creator;

import sg.atom.city.shape.House3DCreator;
import sg.atom.city.CityGen3DApp;
import sg.atom.city.CityMap;
import sg.atom.city.shape.House3D;
import java.util.Random;

/**
 *
 * @author hungcuong
 */
public class GridMapCreator implements AbstractMapCreator {

    int w;
    int h;
    Random random = new Random();
    float zoomFactor = 4;
    CityGen3DApp app;
    private House3DCreator houseCreator;

    public GridMapCreator(CityGen3DApp app, int w, int h) {
        this.app = app;
        this.w = w;
        this.h = h;
    }

    boolean bRandomPercent(float c) {
        float r = random.nextFloat();
        if ((r < c)) {
            return true;
        }
        return false;
    }

    CityMap createGridMap() {
        CityMap result = new CityMap();
        houseCreator = new House3DCreator(app);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float hz = 2 + random.nextInt(10);
                float hw = 1;
                float hl = 1;
                // Get a random chance to make a house
                if (bRandomPercent(0.4f)) {
                    House3D aHouse = houseCreator.createHouse(x, y, hw, hl, hz);
                    result.houses.add(aHouse);
                }
            }
        }
        return result;
    }

    public CityMap createMap() {
        return createGridMap();
    }
}
