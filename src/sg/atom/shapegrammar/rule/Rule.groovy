/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sg.atom.shapegrammar.rule

public class Rule{
    def name
    Rule(String name){
        this.name = name
    }
    
    def applyRule(a,b,c){
        println name + " : "
        c(a,b)
    }
}