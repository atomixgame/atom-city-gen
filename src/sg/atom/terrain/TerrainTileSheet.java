/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.terrain;

import com.jme3.math.Vector2f;
import java.util.ArrayList;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class TerrainTileSheet extends TileSheet {

    public TerrainTileSheet(int width, int height) {
        super(width, height);
        this.width = 4;
        this.height = 4;
    }

    @Override
    public void splitTile() {
        float sx = 1f / width;
        float sy = 1f / height;
        tileList = new ArrayList<Tile>(width * height);
        tileList.add(aTileIndex(0, 0));
        tileList.add(aTileIndex(1, 3));
        tileList.add(aTileIndex(0, 3));
        tileList.add(aTileIndex(1, 1));

        tileList.add(aTileIndex(1, 2));
        tileList.add(aTileIndex(3, 0));
        tileList.add(aTileIndex(2, 1));
        tileList.add(aTileIndex(2, 2));

        tileList.add(aTileIndex(0, 2));
        tileList.add(aTileIndex(3, 1));
        tileList.add(aTileIndex(2, 0));
        tileList.add(aTileIndex(3, 2));

        tileList.add(aTileIndex(1, 0));
        tileList.add(aTileIndex(2, 3));
        tileList.add(aTileIndex(3, 3));
        tileList.add(aTileIndex(0, 1));
    }

    public Tile aTileIndex(int ix, int iy) {
        float sx = 1f / width;
        float sy = 1f / height;
        float x = sx * ix;
        float y = sy * iy;
        return new Tile(vec2(x, y), vec2(x + sx, y), vec2(x + sx, y + sy), vec2(x, y + sy));
    }

    public Tile aTile(float x, float y) {
        float sx = 1f / width;
        float sy = 1f / height;
        return new Tile(vec2(x, y), vec2(x + sx, y), vec2(x + sx, y + sy), vec2(x, y + sy));
    }

    public Vector2f vec2(float x, float y) {
        return new Vector2f(x, y);
    }
}
