package sg.atom.terrain;

import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class ExTerrainMesh extends Mesh {

    private int width;
    private int height;
    Image heightMap;
    Image lMap;
    //For mesh building
    ArrayList<Vector3f> pointCloud;
    ArrayList<Vector2f> uvList;
    ArrayList<Integer> indexList;
    private final TileSheet tileSheet;
    private int maxY = 3;
    // Raw heightmap
    int[][] hMap;
    int[][] tMap;
    private ExTerrainTiler tiler;

    ExTerrainMesh(int width, int height, TileSheet tileSheet) {
        this.width = width;
        this.height = height;
        this.tileSheet = tileSheet;

        buildMesh();
    }

    public void setHeightMap(Image map) {
    }

    public void setLevelrageMap(Image map) {
    }

    private int addQuad(Vector3f[] vertexs, int firstIndex, int t, boolean flip) {
        return addQuad(vertexs, firstIndex, tileSheet.get(t), flip);
    }

    private int addQuad(Vector3f[] vertexs, int firstIndex, Tile t, boolean flip) {
        return addQuad(vertexs, firstIndex, new Vector2f[]{
                    t.topLeft,
                    t.topRight,
                    t.bottomRight,
                    t.bottomLeft}, flip);
    }

    private int addQuad(Vector3f[] vertexs, int firstIndex, Vector2f[] uvs, boolean flip) {
        for (int j = 0; j < vertexs.length; j++) {
            pointCloud.add(vertexs[j]);
        }
        if (!flip) {
            // 0-3-1
            indexList.add(firstIndex + 0);
            indexList.add(firstIndex + 2);
            indexList.add(firstIndex + 1);
            // 0-3-2
            indexList.add(firstIndex + 0);
            indexList.add(firstIndex + 3);
            indexList.add(firstIndex + 2);
        } else {
            // 0-1-2
            indexList.add(firstIndex + 0);
            indexList.add(firstIndex + 1);
            indexList.add(firstIndex + 2);
            // 0-2-3
            indexList.add(firstIndex + 0);
            indexList.add(firstIndex + 2);
            indexList.add(firstIndex + 3);
        }
        for (int j = 0; j < uvs.length; j++) {
            uvList.add(uvs[j]);
        }
        firstIndex += 4;
        return firstIndex;
    }

    private void buildMesh() {
        // Init
        this.pointCloud = new ArrayList<Vector3f>(width * height * 4);
        this.indexList = new ArrayList<Integer>(width * height * 6);
        this.uvList = new ArrayList<Vector2f>(width * height * 4);
        float sizeX = 1f;
        float sizeY = 1f;
        float sizeZ = 1f;
        int y = 0;
        hMap = new int[width][height];
        tMap = new int[width][height];
        tiler = new ExTerrainTiler(this);
        tiler.fillTiles(0, 0, width, height, 0);

        int firstIndex = 0;
        // add UpFace Quad
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {

                // random height
                if (z < 2 || z > 7) {
                    if (x < 2 || x > 7) {
                        y = FastMath.nextRandomInt(0, maxY);
                    }
                }
                y = 0;
                hMap[x][z] = y;
                // Tile
                Tile t = tileSheet.get(tMap[x][z]);
                firstIndex = addQuad(new Vector3f[]{
                            new Vector3f(x, y, z),
                            new Vector3f(x + sizeX, y, z),
                            new Vector3f(x + sizeX, y, z + sizeZ),
                            new Vector3f(x, y, z + sizeZ)}, firstIndex,
                        t,
                        false);

                //
            }
        }
        // Add Side quad
        int hc = 0, hn = 0, h = 0, up = 0, down = 0;
        boolean flip;
        Tile t = tileSheet.get(15);
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                hc = hMap[x][z];
                hn = (!checkRangeX(x - 1)) ? 0 : hMap[x - 1][z];
                h = hn - hc;
                up = Math.max(hc, hc + h);
                down = Math.min(hc, hc + h);
                flip = h < 0;
                for (y = down; y < up; y++) {
                    firstIndex = addQuad(new Vector3f[]{
                                new Vector3f(x, y, z),
                                new Vector3f(x, y, z + sizeZ),
                                new Vector3f(x, y + sizeY, z + sizeZ),
                                new Vector3f(x, y + sizeY, z)}, firstIndex,
                            t,
                            flip);
                }

                hn = (!checkRangeY(z - 1)) ? 0 : hMap[x][z - 1];
                h = hn - hc;
                up = Math.max(hc, hc + h);
                down = Math.min(hc, hc + h);
                flip = h < 0;
                for (y = down; y < up; y++) {
                    firstIndex = addQuad(new Vector3f[]{
                                new Vector3f(x, y, z),
                                new Vector3f(x + sizeX, y, z),
                                new Vector3f(x + sizeX, y + sizeX, z),
                                new Vector3f(x, y + sizeY, z)}, firstIndex,
                            t,
                            !flip);
                }
            }
        }
        // BUILD
        Vector3f[] vertices = new Vector3f[pointCloud.size()];
        Vector2f[] texCoord = new Vector2f[uvList.size()];
        // Convert from ArrayList to Array
        vertices = (Vector3f[]) pointCloud.toArray(vertices);
        texCoord = (Vector2f[]) uvList.toArray(texCoord);
        int[] indexes = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            Integer item = (Integer) indexList.get(i);
            indexes[i] = item.intValue();
        }
        // Set buffer

        setBuffer(VertexBuffer.Type.Position,
                3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(VertexBuffer.Type.TexCoord,
                2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(VertexBuffer.Type.Index,
                1, BufferUtils.createIntBuffer(indexes));
        // add SideFace

        updateBound();
    }

    public void setTileAt(int x, int y, int type) {
        if (!checkRangeX(x) || !checkRangeY(y)) {
            throw new IllegalArgumentException("Out of range x: " + x + " y:" + y);
        } else {
            tMap[x][y] = type;
        }
    }

    public int getTileAt(int x, int y) {
        if (!checkRangeX(x) || !checkRangeY(y)) {
            return -1;
        } else {
            return tMap[x][y];
        }
    }
    /*
     int leftH(int x, int y) {
     if (!checkRangeX(x) || !checkRangeY(y)) {
     throw new IllegalArgumentException("Out of range x: " + x + " y:" + y);
     }
     if (!checkRangeX(x - 1)) {
     return hMap[x][y];
     }
     return hMap[x][y] - hMap[x - 1][y];
     }

     int rightH(int x, int y) {
     if (!checkRangeX(x) || !checkRangeY(y)) {
     throw new IllegalArgumentException("Out of range x: " + x + " y:" + y);
     }
     if (!checkRangeX(x + 1)) {
     return hMap[x][y];
     }
     return hMap[x][y] - hMap[x + 1][y];
     }

     int upH(int x, int y) {
     if (!checkRangeX(x) || !checkRangeY(y)) {
     throw new IllegalArgumentException("Out of range x: " + x + " y:" + y);
     }
     if (!checkRangeY(x + 1)) {
     return hMap[x][y];
     }
     return hMap[x][y] - hMap[x][y + 1];
     }

     int downH(int x, int y) {
     if (!checkRangeX(x) || !checkRangeY(y)) {
     throw new IllegalArgumentException("Out of range x: " + x + " y:" + y);
     }
     if (!checkRangeY(y - 1)) {
     return hMap[x][y];
     }
     return hMap[x][y] - hMap[x][y - 1];
     }
     */

    boolean checkRangeY(int y) {
        return (y >= 0) && (y < height);
    }

    boolean checkRangeX(int x) {
        return (x >= 0) && (x < width);
    }

    public Vector2f intersectWherePlanar(Ray ray) {
        Vector2f result = new Vector2f();
        Vector3f intersectPoint = new Vector3f();
        Plane p = new Plane();
        p.setPlanePoints(new Vector3f(1, 0, 1), new Vector3f(1, 0, 2), new Vector3f(2, 0, 4));
        ray.intersectsWherePlane(p, intersectPoint);
        result.set(intersectPoint.x, intersectPoint.y);
        return result;
    }

    public void paint(int x, int y, int type) {
        Tile t = tileSheet.get(type);

        VertexBuffer buf = getBuffer(VertexBuffer.Type.TexCoord);
        int index = (y * width + x) * 4;
        buf.setElementComponent(index, 0, t.topLeft.x);
        buf.setElementComponent(index, 1, t.topLeft.y);
        buf.setElementComponent(index + 1, 0, t.topRight.x);
        buf.setElementComponent(index + 1, 1, t.topRight.y);
        buf.setElementComponent(index + 2, 0, t.bottomRight.x);
        buf.setElementComponent(index + 2, 1, t.bottomRight.y);
        buf.setElementComponent(index + 3, 0, t.bottomLeft.x);
        buf.setElementComponent(index + 3, 1, t.bottomLeft.y);

        System.out.println(" Paint at " + x + " " + y);
    }
}
