package sg.atom.shapegrammar

import javax.swing.tree.DefaultMutableTreeNode as TreeNode
import groovy.util.Node
import com.jme3.scene.Node as JMENode
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.math.*;

import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import static script.GeoHelper.*;
import sg.atom.shapegrammar.production.*;


public class ShapeGrammar{
    JMENode rootNode;
    private TreeNode gramRootNode = new TreeNode("Root")
    //private Node dataNode = new Node("Root")
    Material unshadeMat;
    Material lightMat;
    Material wireMat;
    def onlyLeaf=false;
    def drawControlPoint = true;
    def drawShapeElement= true;
    
    ShapeGrammar(rootNode){
        this.rootNode = rootNode;
    }
    public TreeNode createGrammarTree(){
        //gramRootNode = copyTogramRootNode(dataNode)
        //stageInit()
        //printTree(gramRootNode)    
        
        return gramRootNode;
    }
    
    public test3DStage(){
        stageInit()
        stage0()
    }
    
    def stageInit(){

        def seg = new SGElement('line','pl',[],[],null)
        seg.controls << new Vector3f(0f,0f,0f)
        seg.controls<< new Vector3f(0f,0f,4f)
        seg.controls<< new Vector3f(4f,0f,4f)
        seg.controls<< new Vector3f(4f,0f,0f)
        
        gramRootNode.add(new TreeNode(seg))
    }
    
    def applyRule(tree,rule){

    }

    def stage0(){
        rule1(gramRootNode)
        rule2(gramRootNode,3)
        rule3(gramRootNode,0.1f)
        rule4(gramRootNode,4f)
        // Display tree
        draw3D(gramRootNode)
    }
    def rule1(tree){
        // Rule : Divide line string into N Seg
        //travel tree breadFirst
        tree.breadthFirstEnumeration().each(){node->
            // Criteria 
            
            if (node.getChildCount()==0){
                
                // Node is leaf
                def element=node.userObject
                if (element.type =="pl"){
                    // Node type pl
                    def num = element.controls.size
                    (0..num-1).each{index->
                        def start = element.controls[index]
                        def end = (index!=num-1)?element.controls[index+1]:element.controls[0]
                        
                        def color = new ColorRGBA(ColorRGBA.Yellow)
                        def colorMat = getColorMat(color)


                        def geoList = []
                        def geo = LineBuilder.build("Line",[start,end]);
                        geo.setMaterial(colorMat)
                        geoList << geo
                        
                        def seg = new SGElement('seg','p2',[start,end],geoList,color)
                        def newNode = new TreeNode(seg)
                        node.add(newNode)
                    }
                }
            }
        
        }
    }
    def rule2(tree,num){
        // Rule : Divide seg to N Seg
        //travel tree breadFirst
        tree.breadthFirstEnumeration().each(){node->
            // Criteria 
            
            if (node.getChildCount()==0){
                
                // Node is leaf
                def element=node.userObject
                if (element.type =="p2"){
                    // Node type p2
                    Vector3f pStart = element.controls[0]
                    Vector3f pEnd = element.controls[1]
                    
                    (0..num-1).each{index->
                        float step  = 1f/ num
                        float v1 = index * step 
                        float v2 = (index+1) * step
                        def start = pStart.clone().interpolate(pEnd,v1)
                        def end = pStart.clone().interpolate(pEnd,v2)
                        //println v1 + " " + v2
                        //println start.toString() + " " + end.toString();
                        
                     
                        def color = new ColorRGBA(ColorRGBA.Green)
                        def colorMat = getColorMat(color)
                        
                        def geoList = []
                        def geo = LineBuilder.build("Line",[pStart,pEnd]);
                        geo.setMaterial(colorMat)
                        geoList<< geo
                        def seg = new SGElement('seg','p2',[start,end],geoList,color)
                        def newNode = new TreeNode(seg)
                        node.add(newNode)
                        node.add(new TreeNode(seg))
                    }
                }
            }
        
        }
    }
    
    def rule3(tree,width){
        // Rule : Expand seg width
        //travel tree breadFirst
        tree.breadthFirstEnumeration().each(){node->
            // Criteria 
            
            if (node.getChildCount()==0){
                
                // Node is leaf
                def element=node.userObject
                if (element.type =="p2"){
                    def controls = element.controls
                    // Node type p2
                    Vector3f pStart = controls[0]
                    Vector3f pEnd = controls[1]
                    
                    Vector3f dir = pStart.subtract(pEnd)
                    def clist = []
                    Vector3f left = dir.cross(Vector3f.UNIT_Y).normalizeLocal().mult(width)
                    Vector3f right = left.negate()
                    
                    clist << pStart.add(left)
                    clist << pStart.add(right)
                    clist << pEnd.add(right)
                    clist << pEnd.add(left)
                    
                    def color = new ColorRGBA(ColorRGBA.Red)
                    def colorMat = getColorMat(color)
                    
                    def geoList = []
                    
                    def l1 = LineBuilder.build("Line1",clist[0],clist[1]);
                    def l2 = LineBuilder.build("Line2",clist[1],clist[2]);
                    def l3 = LineBuilder.build("Line2",clist[2],clist[3]);
                    def l4 = LineBuilder.build("Line2",clist[3],clist[0]);
                    l1.setMaterial(colorMat)
                    l2.setMaterial(colorMat)
                    l3.setMaterial(colorMat)
                    l4.setMaterial(colorMat)
                    geoList<< l1 << l2 << l3 << l4
                    
                    def seg = new SGElement('segEx','p4',clist,geoList,color)
                    def newNode = new TreeNode(seg)
                    node.add(newNode)
                }
            }
        
        }
    }
    
    def rule4(tree,height){
        // Rule : Expand seg to Box
        //travel tree breadFirst
        tree.breadthFirstEnumeration().each(){node->
            // Criteria 
            
            if (node.getChildCount()==0){
                
                // Node is leaf
                def element=node.userObject
                if (element.type =="p4"){
                    def controls = element.controls
                    // Node type p4
                    def clist = []
                    controls.each{point->
                        clist<<point.clone()
                    }
                    controls.each{point->                       
                        clist<<point.add(Vector3f.UNIT_Y.mult(height))
                    }
                    
                    def color = new ColorRGBA(ColorRGBA.Blue)
                    def colorMat = getColorMat(color)
                    def geoList = []
                    colorMat.getAdditionalRenderState().setWireframe(true);
                    def box = BoxBuilder.build("Box",clist[0],clist[1],clist[2],clist[5]);
                    //box.setMaterial(colorMat)
                    box.setMaterial(lightMat)
                    geoList<< box
                    
                    
                    def seg = new SGElement('Box','box',clist,geoList,color)
                    def newNode = new TreeNode(seg)
                    node.add(newNode)
                }
            }
        
        }
    }
    def getColorMat(color){
        def colorMat
        if (color ==null){
            colorMat = unshadeMat
        } else {
            colorMat = unshadeMat.clone()
            colorMat.setColor("Color",color);
        }
        return colorMat
    }
    def draw3D(tree){
        def geoList = []


        tree.breadthFirstEnumeration().each{node->
            def element=node.userObject
            if (element instanceof SGElement){
                println "draw an element"
                def colorMat = getColorMat(element.color)
                if (drawControlPoint){
                    // drawControlPoint
                    element.controls.each(){ control->
                        if (control instanceof Vector3f){
                            Vector3f pos = control.clone()
                            Sphere s = new Sphere(6,6,0.02f)
                            //Box c = new Box(0.1f,0.1f,0.1f)
                            def geo = new Geometry("Point",s);
                            geo.setMaterial(colorMat)
                            geo.setLocalTranslation(pos)
                            geoList<< geo
                        }
                    }
                }                  
                
                if (drawShapeElement){
                    if ((onlyLeaf&&node.getChildCount()==0)||!onlyLeaf){
                        switch (element.type){
                            case "p2":case "p4":case "box":
                            geoList.addAll(element.data)
                            break;
                        }  
                    }
                }
            }
        }
        
        draw3DGeos(rootNode,geoList)
    }

}





