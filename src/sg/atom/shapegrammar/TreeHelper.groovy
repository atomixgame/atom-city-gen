/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sg.atom.shapegrammar

import javax.swing.tree.DefaultMutableTreeNode as TreeNode
import groovy.util.Node
import com.jme3.scene.Node as JMENode

class TreeHelper{
    // Print tree as Text
    def printTree(tree){
        def treeList = tree.depthFirstEnumeration().toList()
        treeList.reverseEach(){subNode->
            def line =""
            (0..subNode.level).each{
                line += "|_"
            } 
            line +=  subNode.userObject
            println line
        }
    }
    
    // Tree conversion
    // Groovy Node to Swing TreeNode
    def copyToUINode (Node dataNode,TreeNode uiNode){
        uiNode.name= subNode.name
        dataNode.children.each(){subNode->
            def newUINode = new TreeNode(subNode.name);
            uiNode.add(newUINode)
            if (subNode.children().size>0){
                copyToUINode(subNode,newUINode)
            } else {

            }
        }
    }
    // JME Node to GroovyNode
    
    // Tree to JUNG Graph
}
