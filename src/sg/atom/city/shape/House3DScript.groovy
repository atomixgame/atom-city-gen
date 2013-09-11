package sg.atom.city.shape

import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import editor3d.mesh.*;
import static script.GeoHelper.*;
import sg.atom.mesh.modifier.*

class House3DScript{

    Node rootNode;
    Material lightMat;
    Material unshadeMat;
    ExtrudeModifier ext;
    House3DScript(s,rootNode){
        scale3D = s
        ext = new ExtrudeModifier();
        ext.init();
        this.rootNode = rootNode;
    }
    def draw3DLine(lineList){
        lineList.each{
            def geo = new Geometry("Line",it);
            geo.setMaterial(unshadeMat)
            rootNode.attachChild(geo)
        }
    }
    public make3DLines(geoList){
        def lineList=[]
        geoList.each{poly->
            lineList += to3DLines(poly)
            draw3DLine(lineList)
        }
        //println "make3DLine"
    }
    
    void setLightMat(mat){
        this.lightMat = mat;
    }
    void setUnshadeMat(mat){
        this.unshadeMat = mat;
    }

    def make3DHouses(geoList){
        def houseList=[]
        geoList.each{poly->
            def random = new Random();
            def floor = 4 + random.nextInt(30)
            houseList<<to3DHouse(to3DPointList(poly),floor)
        }
        draw3DGeos(rootNode,houseList)
    }

    def to3DHouse(base,numOfFloor){
        ext.reset();
        ext.setInitShape(base)
        ext.setLayer(numOfFloor)
        Mesh m = ext.createExtrude(true)
        Geometry geo = new Geometry("House",m)
        geo.setMaterial(lightMat)
        return geo
    }

    def makeMouse(info){
        
    }
}