package sg.atom.city

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode as TreeNode
import java.awt.BorderLayout;
import groovy.swing.SwingBuilder
import com.jme3.scene.BatchNode;
import sg.atom.shapegrammar.*;
import sg.atom.geobox.*
import sg.atom.city.*
import sg.atom.city.shape.*
import sg.atom.city.creator.*


/* UI */
def swing = new SwingBuilder()
JTree roadGraphTree

swing.frame(title: 'CityGen Test1', defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
    size: [1024, 768], show: true, locationRelativeTo: null) {
    lookAndFeel("system")
   
    toolbar = toolBar(rollover:true, constraints:BorderLayout.NORTH,floatable :true) {
        
        button( text:"New")
        button( text:"Open")
        button( text:"Save")
        separator(orientation:SwingConstants.VERTICAL)
    }
   
    menuBar() {
        menu(text: "File", mnemonic: 'F') {
            menuItem(text: "Exit", mnemonic: 'X', actionPerformed: {dispose() })
        }
        menu(text: "View", mnemonic: 'V') {
            menuItem(text: "Left", mnemonic: 'L', actionPerformed: { })
        }
        menu(text: "Tool", mnemonic: 'T') {
            menuItem(text: "Reset", mnemonic: 'R', actionPerformed: { })
        }
    }
    splitPane(orientation:JSplitPane.HORIZONTAL_SPLIT, dividerLocation:180) {
        splitPane(orientation:JSplitPane.VERTICAL_SPLIT, dividerLocation:350) {
            
            tabbedPane(tabPlacement: JTabbedPane.TOP) {
                label('One', title:'One', tabToolTip:'Uno!')
                label('Green', title:'Green', tabBackground:java.awt.Color.GREEN)
                scrollPane(name:"Grammar",constraints: "left", preferredSize: [160, -1]) {
                    shapeGramTree = tree(rootVisible: false)
                }
                scrollPane(name:"Road",constraints: "left", preferredSize: [160, -1]) {
                    roadGraphTree = tree(rootVisible: false)
                }
            }         

            scrollPane(constraints: "top") { propertyTable = table() }
        }
        splitPane(orientation:JSplitPane.VERTICAL_SPLIT, dividerLocation:600) {
            viewPortPanel = panel(size: [640, 480])
            scrollPane(constraints: "bottom") { 
                textArea(text:"Log") 
            }
        }
    }
    ["Name", "Value"].each { propertyTable.model.addColumn(it) }
}

// Generator stuff 

City city=new City("Hanoi");
CityGen3DApp app = new CityGen3DApp();
viewPortPanel.add(app.createAndStartCanvas(800,600));

def createMap={
    city.createMap(new GridMapCreator(app,100,100));
    BatchNode batch = new BatchNode(); 
    city.map.attachMap(batch);
    batch.batch();
    app.getRootNode().attachChild(batch);
}

app.enqueue(createMap);

roadGraphTreeData = [
    [name: "root", folders: [[name: "Node1"], [name: "Node2"]]],
    [name: "branch", folders: [[name: "Node1"], [name: "Node2"]]]
]
roadGraphTree.model.root.removeAllChildren()
roadGraphTreeData.each {mbox ->
    def node = new TreeNode(mbox.name)
    mbox.folders.each { folder -> node.add(new TreeNode(folder.name)) }
    roadGraphTree.model.root.add(node)
}
roadGraphTree.model.reload(roadGraphTree.model.root)
shapeGramTree.model.root.removeAllChildren()
//def grammar = new ShapeGrammar(app.getRootNode());


/*
boxd = new BoxPlayGround()
boxd.init()
house3DCreator = new House3DScript(0.1f,app.getRootNode())
 */
def houseMakeCall = {
    house3DCreator.setLightMat(app.getLightMat());
    house3DCreator.setUnshadeMat(app.getUnshadeMat());
    //house3D.make3DLines(boxd.bufferGeoList)
    //house3D.make3DHouses(boxd.bufferGeoList)
}
def grammarCall = {
    grammar.setLightMat(app.getLightMat());
    grammar.setUnshadeMat(app.getUnshadeMat());
    grammar.test3DStage()
    def node =  grammar.createGrammarTree();
    shapeGramTree.model.root.add(node)
    shapeGramTree.model.reload(shapeGramTree.model.root)
}
//app.enqueue(houseMakeCall);
//app.enqueue(grammarCall);
//boxd.showFrame()