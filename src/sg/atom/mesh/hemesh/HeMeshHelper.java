package sg.atom.mesh.hemesh;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import java.util.Iterator;
import javolution.util.FastMap;
import sg.atom.mesh.core.SurfaceMesh;
import sg.atom.mesh.core.Vertex;
import wblut.geom.WB_Normal3d;
import wblut.hemesh.HEC_Box;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HEM_Lattice;
import wblut.hemesh.HES_Smooth;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;

/**
 *
 * @author hungcuong
 */
public class HeMeshHelper {
    public HeMeshHelper(){
        
    }
    public HE_Mesh createCustomLatice() {
        //Custom mesh creation is easiest by creating an array of vertices and the corresponding faces
        //and calling the faceList creator. Writing a full-blown implementation of a HEC_Creator
        //is best done in Eclipse with full access to the code repository.

        //Array of all vertices
        float[][] vertices = new float[121][3];
        int index = 0;
        for (int j = 0; j < 11; j++) {
            for (int i = 0; i < 11; i++) {
                vertices[index][0] = -20 + i * 4 + (((i != 0) && (i != 10)) ? FastMath.nextRandomInt(-20, 20) : 0);
                vertices[index][1] = -20 + j * 4 + (((j != 0) && (j != 10)) ? FastMath.nextRandomInt(-20, 20) : 0);
                vertices[index][2] = FastMath.sin(FastMath.TWO_PI / 20f * i) * 4 + FastMath.cos(FastMath.TWO_PI / 10 * j) * 4;
                index++;
            }
        }
        //Array of faces. Each face is an arry of vertex indices;
        index = 0;
        int[][] faces = new int[100][];
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 10; i++) {
                faces[index] = new int[4];
                faces[index][0] = i + 11 * j;
                faces[index][1] = i + 1 + 11 * j;
                faces[index][2] = i + 1 + 11 * (j + 1);
                faces[index][3] = i + 11 * (j + 1);
                index++;
            }
        }

//HEC_Facelist uses the vertices and the indexed faces to create a mesh with all connectivity.
        HEC_FromFacelist facelistCreator = new HEC_FromFacelist().setVertices(vertices).setFaces(faces).setDuplicate(false);
        HE_Mesh mesh = new HE_Mesh(facelistCreator);

        //check mesh validity, surfaces meshes will have "Null reference (face)" for the outer halfedges.
        //other messages could refer to inconsistent face orientation, missing faces or meshes not representable by
        //the hemesh datastructure
        mesh.validate(true, true);

        //This should work
        mesh.modify(new HEM_Lattice().setDepth(5).setWidth(5));
        mesh.subdivide(new HES_Smooth());

        return mesh;
    }

    public HE_Mesh createHEBox() {
        // All meshes are built with separate objects called creators
        HEC_Box boxCreator = new HEC_Box();

        //Set parameters one by one ... (See docs for parameters)
        boxCreator.setWidth(2);
        boxCreator.setWidthSegments(4);

        //... or string them together
        boxCreator.setHeight(2).setHeightSegments(5).setDepth(2).setDepthSegments(3);

        // exceptions: setCenter, setZAngle and setZAxis cannot be combined with other setParameters or should be put last
        // these three can be applied to all creators.
        boxCreator.setCenter(0, 0, 0).setZAxis(1, 1, 1).setZAngle(FastMath.PI / 4);
        //The actual mesh is created by calling the mesh creator in the HE_Mesh constructor
        HE_Mesh box = new HE_Mesh(boxCreator);
        return box;
    }

    public HE_Mesh testModifier() {
        float HALF_PI = FastMath.HALF_PI;
        //Create the box
        HEC_Box boxCreator = new HEC_Box().setWidth(4).setWidthSegments(20)
                .setHeight(2).setHeightSegments(10)
                .setDepth(2).setDepthSegments(10);
        HE_Mesh box = new HE_Mesh(boxCreator);

        //The easiest way to create a simple modifier is by exporting all vertex coordinates, change them and
        //recreate the mesh with the new coordinates. Writing a full-blown implementation of a HEM_Modifier
        //is best done in Eclipse with full access to the code repository.

        //Export the faces and vertices
        float[][] vertices = box.getVerticesAsFloat(); // first index = vertex index, second index = 0..2, x,y,z coordinate
        int[][] faces = box.getFacesAsInt();// first index = face index, second index = index of vertex belonging to face

        //Do something with the vertices
        for (int i = 0; i < box.numberOfVertices(); i++) {
            vertices[i][0] *= 1.2 + .2 * FastMath.sin(HALF_PI / 10 * i + HALF_PI);
            vertices[i][1] *= 1.2 + .2 * FastMath.sin(HALF_PI / 17 * i);
            vertices[i][2] *= 1.2 + .2 * FastMath.cos(HALF_PI / 25 * i);
        }

        //Use the exported faces and vertices as source for a HEC_FaceList
        HEC_FromFacelist faceList = new HEC_FromFacelist().setFaces(faces).setVertices(vertices);
        HE_Mesh modifiedBox = new HE_Mesh(faceList);
        return modifiedBox;
    }

    public static Mesh toJMesh(final HE_Mesh mesh) {
        SurfaceMesh sMesh = new SurfaceMesh();
        final HE_Mesh triMesh = mesh.get();
        triMesh.triangulate();
        // vertices
        final FastMap<Integer, Integer> keyToIndex = new FastMap<Integer, Integer>(mesh.numberOfVertices());
        Iterator<HE_Vertex> vItr = triMesh.vItr();
        HE_Vertex v;
        int i = 0;
        while (vItr.hasNext()) {
            v = vItr.next();
            keyToIndex.put(v.key(), i);
            Vector3f pos = toJVec3f(v);
            Vector3f normal = toJVec3f(v.getVertexNormal());
            //Vector2f uv = toJVec2f(v)
            System.out.println(i + ": " + pos + " - " + normal);
            sMesh.addVertex(toCVertex(pos, normal));
            i++;
        }

        // faces
        final Iterator<HE_Face> fItr = triMesh.fItr();
        HE_Face f;
        while (fItr.hasNext()) {
            f = fItr.next();
            sMesh.addTriangle(
                    keyToIndex.get(f.getHalfedge().getVertex().key()),
                    keyToIndex.get(f.getHalfedge().getNextInFace().getVertex().key()),
                    keyToIndex.get(f.getHalfedge().getPrevInFace().getVertex().key()));
        }

        return sMesh.createMesh();
    }

    private static Vector3f toJVec3f(WB_Normal3d vertexNormal) {
        return new Vector3f(vertexNormal.xf(), vertexNormal.yf(), vertexNormal.zf());
    }

    private static Vector3f toJVec3f(HE_Vertex hv) {
        return new Vector3f(hv.xf(), hv.yf(), hv.zf());
    }

    private static Vertex toCVertex(Vector3f position, Vector3f normal) {
        Vertex newVertex = new Vertex();
        newVertex.position = position;
        newVertex.normal = normal;
        return newVertex;
    }
}
