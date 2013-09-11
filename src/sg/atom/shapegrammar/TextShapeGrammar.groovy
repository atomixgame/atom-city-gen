/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sg.atom.shapegrammar
import javax.swing.tree.DefaultMutableTreeNode as TreeNode
import groovy.util.Node

class TextShapeGrammar{
    private TreeNode gramRootNode = new TreeNode("Root")
    
    public TreeNode createGrammarTree(){
        //stageInit()
        //printTree(uiNode)
        return uiNode;
    }
    def stageInit(){
        gramRootNode.add(new TreeNode("a"))
        applyRule(gramRootNode,[text:"a->bbb",maxDepth:1])
        applyRule(gramRootNode,[text:"b->cd",maxDepth:1])
        applyRule(gramRootNode,[text:"c->g",maxDepth:1])
        applyRule(gramRootNode,[text:"d->ef",maxDepth:1])
    }
    
    def applyRule(tree,rule){
        
        def ruleSplit=rule.text.split("->");
        def LHS = ruleSplit[0]
        def RHS = ruleSplit[1]
        
        tree.breadthFirstEnumeration().each(){subNode->
            if (subNode.userObject == LHS){
                for (char ch: RHS.toCharArray()) {
                    subNode.add(new TreeNode(ch))
                }
            }
        }
    }
}