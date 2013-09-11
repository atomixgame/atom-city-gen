/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.mesh.modifier;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;

/**
 *
 * @author cuong.nguyenmanh2
 */
public class BaseModifier {

    AssetManager assetManager;
    String name;
    String applyTo;
    boolean retopo;
    ArrayList<Vector3f> pointCloud;
    ArrayList<Vector2f> uvList;
    ArrayList<Integer> indexList;

    public void init(AssetManager assetManager) {
        this.assetManager = assetManager;
        pointCloud = new ArrayList();
        uvList = new ArrayList();
        indexList = new ArrayList();
    }

    protected Mesh makeMeshFrom(ArrayList<Vector3f> pointCloud, ArrayList<Vector2f> uvList, ArrayList<Integer> indexList) {
        Mesh m = new Mesh();

        Vector3f[] vertices = new Vector3f[pointCloud.size()];

        Vector2f[] texCoord = new Vector2f[uvList.size()];

        vertices = (Vector3f[]) pointCloud.toArray(vertices);
        texCoord = (Vector2f[]) uvList.toArray(texCoord);

        int[] indexes = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            Integer item = (Integer) indexList.get(i);
            indexes[i] = item.intValue();
        }

        m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        m.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        m.updateBound();

        return m;
    }
}
