/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.terrain;

import com.jme3.math.FastMath;
import java.util.BitSet;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class ExTerrainTiler {

    ExTerrainMesh mesh;

    public ExTerrainTiler(ExTerrainMesh mesh) {
        this.mesh = mesh;
    }

    void getRandomTerrainTiles(int x, int y, int width, int height) {
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int type = FastMath.nextRandomInt(0, 15);
                mesh.setTileAt(x + j, y + i, type);
            }
        }
    }

    void fillTiles(int x, int y, int width, int height, int type) {
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                mesh.setTileAt(x + j, y + i, type);
            }
        }
    }

    void fillTiles2(int x, int y, int width, int height, int type) {
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                mesh.setTileAt(x + j, y + i, (j * height + i) % 16);
            }
        }
    }

    void setTileAt(int x, int y, int type) {
        if (!mesh.checkRangeX(x) || !mesh.checkRangeY(y)) {
            throw new IllegalArgumentException("Out of range x: " + x + " y:" + y);
        }
        mesh.setTileAt(x, y, type);
        // get tile Type
        BitSet thisBit = getTileBitSet(type);
        boolean empty = thisBit.isEmpty();
        changeBit(x - 1, y - 1, 3, empty);
        changeBit(x, y - 1, 2, empty);
        changeBit(x, y - 1, 3, empty);
        changeBit(x + 1, y - 1, 2, empty);

        changeBit(x - 1, y, 1, empty);
        changeBit(x - 1, y, 3, empty);
        changeBit(x + 1, y, 0, empty);
        changeBit(x + 1, y, 2, empty);

        changeBit(x - 1, y + 1, 1, empty);
        changeBit(x, y + 1, 0, empty);
        changeBit(x, y + 1, 1, empty);
        changeBit(x + 1, y + 1, 0, empty);

        // update Neighbor
    }

    public void changeBit(int x, int y, int bitNum, boolean empty) {
        if (mesh.checkRangeX(x) && mesh.checkRangeX(y)) {
            BitSet setLT = getTileBitSet(mesh.getTileAt(x, y));
            if (empty) {
                setLT.clear(bitNum);
            } else {
                setLT.set(bitNum);
            }
            mesh.setTileAt(x, y, toTileType(setLT));
        } else {
        }
    }

    public static int toTileType(BitSet bs) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result += toInt(bs.get(i)) << i;
        }
        return result;
    }

    public static int toInt(boolean b) {
        return b ? 1 : 0;
    }

    public static BitSet getTileBitSet(int type) {
        BitSet result = new BitSet(4);
        for (int i = 0; i < 4; i++) {
            if ((type & (1 << i)) >> i == 1) {
                result.set(i);
            }
        }
        return result;
    }

    public static int toTileType(byte b1, byte b2, byte b3, byte b4) {
        return b1 + b2 << 1 + b3 << 2 + b4 << 3;

    }

    public static byte[] getTileBit(int type) {
        type = (type % 16);
        byte b1 = (byte) ((type & 0x1));
        byte b2 = (byte) ((type & 0x2) >> 1);
        byte b3 = (byte) ((type & 0x4) >> 2);
        byte b4 = (byte) ((type & 0x8) >> 3);

        return new byte[]{b1, b2, b3, b4};
    }
    /*
     public static void main(String[] args) {
     System.out.println(" " + getTileBitSet(0).toString());
     }
     */
}
