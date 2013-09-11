package sg.atom.terrain;

import com.jme3.math.Vector2f;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class Tile {

    Vector2f topLeft;
    Vector2f topRight;
    Vector2f bottomRight;
    Vector2f bottomLeft;

    public Tile() {
    }

    public Tile(Vector2f topLeft, Vector2f topRight, Vector2f bottomRight, Vector2f bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
    }
}
