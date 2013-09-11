package sg.atom.mesh.core;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurfaceMesh {

    private List<Integer> index = new ArrayList<Integer>();
    private List<Vertex> vertex = new ArrayList<Vertex>();
    private Map<Vector3f, Integer> vertexMap = new HashMap<Vector3f, Integer>();
    private boolean reuseVertex = false;

    public SurfaceMesh() {
    }

    public void clear() {
        index.clear();
        vertex.clear();
        vertexMap.clear();
    }

    public void addTriangle(int v1, int v2, int v3) {
        index.add(v1);
        index.add(v2);
        index.add(v3);
    }

    public int addVertex(Vertex vertex) {
        int index = 0;
        if (reuseVertex) {
            Integer _index = vertexMap.get(vertex.position);
            if (_index != null) {
                index = _index.intValue();
            } else {
                index = this.vertex.size();
                this.vertex.add(vertex);
                vertexMap.put(vertex.position, Integer.valueOf(index));
            }
        } else {
            index = this.vertex.size();
            this.vertex.add(vertex);
        }

        return index;
    }

    public Mesh createMesh() {
        System.out.println("vertices: " + vertex.size());
        System.out.println("triangles: " + index.size() / 3);
        Mesh mesh = new Mesh();
        float[] vertices = null;
        float[] normals = null;
        float[] texCoords = null;
        float[] binormals = null;
        float[] tangents = null;
        int[] indices = null;
        MeshConfiguration config = new MeshConfiguration();
        vertices = new float[vertex.size() * 3];
        indices = new int[index.size()];
        if (config.isUseNormals()) {
            normals = new float[vertex.size() * 3];
        }
        if (config.isUseTexCoords()) {
            if (config.isUseMaterial()) {
                texCoords = new float[vertex.size() * 3];
            } else {
                texCoords = new float[vertex.size() * 2];
            }
        }

        if (config.isUseTangents()) {
            tangents = new float[vertex.size() * 3];
        }

        if (config.isUseBinormals()) {
            binormals = new float[vertex.size() * 3];
        }

        for (int i = 0; i < vertex.size(); i++) {
            vertices[i * 3 + 0] = vertex.get(i).position.x;
            vertices[i * 3 + 1] = vertex.get(i).position.y;
            vertices[i * 3 + 2] = vertex.get(i).position.z;

        }

        for (int i = 0; i < index.size(); i++) {
            indices[i] = index.get(i);
        }

        if (config.isUseNormals()) {
            for (int i = 0; i < vertex.size(); i++) {
                normals[i * 3 + 0] = vertex.get(i).normal.x;
                normals[i * 3 + 1] = vertex.get(i).normal.y;
                normals[i * 3 + 2] = vertex.get(i).normal.z;
            }
        }

        if (config.isUseBinormals()) {
            for (int i = 0; i < vertex.size(); i++) {
                binormals[i * 3 + 0] = vertex.get(i).binormal.x;
                binormals[i * 3 + 1] = vertex.get(i).binormal.y;
                binormals[i * 3 + 2] = vertex.get(i).binormal.z;
            }
        }

        if (config.isUseTangents()) {
            for (int i = 0; i < vertex.size(); i++) {
                tangents[i * 3 + 0] = vertex.get(i).tangent.x;
                tangents[i * 3 + 1] = vertex.get(i).tangent.y;
                tangents[i * 3 + 2] = vertex.get(i).tangent.z;
            }
        }

        if (config.isUseTexCoords()) {
            for (int i = 0; i < vertex.size(); i++) {
                if (config.isUseMaterial()) {
                    texCoords[i * 3 + 0] = vertex.get(i).texCoord.x;
                    texCoords[i * 3 + 1] = vertex.get(i).texCoord.y;
                    texCoords[i * 3 + 2] = vertex.get(i).material;
                } else {
                    texCoords[i * 2 + 0] = vertex.get(i).texCoord.x;
                    texCoords[i * 2 + 1] = vertex.get(i).texCoord.y;
                }
            }
        }

        mesh.setBuffer(Type.Position, 3, vertices);
        mesh.setBuffer(Type.Index, 1, indices);

        if (config.isUseNormals()) {
            mesh.setBuffer(Type.Normal, 3, normals);
        }

        if (config.isUseTangents()) {
            mesh.setBuffer(Type.Tangent, 3, tangents);
        }

        if (config.isUseBinormals()) {
            mesh.setBuffer(Type.Binormal, 3, binormals);
        }

        if (config.isUseNormals()) {
            mesh.setBuffer(Type.Normal, 3, normals);
        }

        if (config.isUseTexCoords()) {
            if (config.isUseMaterial()) {
                mesh.setBuffer(Type.TexCoord, 3, texCoords);
            } else {
                mesh.setBuffer(Type.TexCoord, 2, texCoords);
            }
        }

        mesh.updateBound();
        mesh.updateCounts();
        mesh.setStatic();

        return mesh;
    }

    public void setReuseVertex(boolean reuseVertex) {
        this.reuseVertex = reuseVertex;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public List<Vertex> getVertex() {
        return vertex;
    }
}
