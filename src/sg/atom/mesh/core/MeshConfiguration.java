/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.mesh.core;

/**
 *
 * @author hungcuong
 */
class MeshConfiguration {

    private boolean useNormals = true;
    private boolean useTexCoords = false;
    private boolean useMaterial = false;
    private boolean useTangents = false;
    private boolean useBinormals = false;

    boolean isUseNormals() {
        return useNormals;
    }

    public boolean isUseBinormals() {
        return useBinormals;
    }

    public boolean isUseMaterial() {
        return useMaterial;
    }

    public boolean isUseTexCoords() {
        return useTexCoords;
    }

    public boolean isUseTangents() {
        return useTangents;
    }
    
}
