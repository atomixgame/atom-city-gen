package script.lsystem
import java.awt.*
import groovy.swing.j2d.*
import groovy.beans.Bindable

class LSystemScript{

    @Bindable def axiom = "0"
    def genStr = ""
    def rules = "1->11;0->1[0]0"
    def startLength = 16
    @Bindable def angle = 90
    @Bindable def steps = 4
    def currentStep = 0

    def savedPos = []
    def startPos  = [:]
    LSystemScript(){
        startPos= [x:300,y:500,angle:angle,l:startLength]
    }

    def makeLString(){
        genStr = axiom
        //steps++
        (0..steps).each{index->
            def ruleSplit=rules.split(";");
            ruleSplit.each{
                genStr = applyRule(genStr,it)
            }
            //println index + " "+genStr
        }
    }
    def applyRule(str,rule){
        def ruleSplit=rule.split("->");
        def LHS = ruleSplit[0]
        def RHS = ruleSplit[1]
    
        def newStr = str.replaceAll(LHS,RHS)
        return newStr
    }


    def turtleMove(t){
        t.x += Math.cos( Math.toRadians(t.angle)) * t.l
        t.y -= Math.sin( Math.toRadians(t.angle)) * t.l
        //t.l /= 2
    }
    def turtleCopy(t1,t2){
        t1.x = t2.x
        t1.y = t2.y
        t1.angle = t2.angle
        t1.l = t2.l
    }
    def getDrawOp(){
        gb.group (id:'root'){
            drawLTree();
        }
    }
    def drawLTree(){
        def currentPos = startPos;
        //savedPos.clear()
        gb.group (id:'lsystem'){
            for (int i=0;i<genStr.length();i++){
                
                def newPos = currentPos.clone()
                turtleMove(newPos)
                def x1=currentPos.x
                def y1=currentPos.y
                def x2=newPos.x
                def y2=newPos.y
                
                switch (genStr[i]){
                    case '0': 
                        // draw line and a leaf
                        line( x1: x1, y1: y1, x2: x2, y2: y2, borderColor: 'green', borderWidth: 3 )
                        turtleCopy(currentPos,newPos)
                    break
                    case '1':
                        // draw line
                        line( x1: x1, y1: y1, x2: x2, y2: y2, borderColor: 'black', borderWidth: 2 )
                        turtleCopy(currentPos,newPos)
                    break
                    case '[':
                        savedPos.push(currentPos.clone())
                        currentPos.angle+=45
                    break
                    case ']':
                        save=savedPos.pop()
                        turtleCopy(currentPos,save)
                        currentPos.angle-=45
                    break
                }
                
                //println i + " "+newPos
            }
        }
    }

    def updateGraphics(){
        startPos= [x:300,y:500,angle:angle,l:startLength]
        makeLString()
        println "startPos" + startPos
        println "steps" + steps
        gb."root".getOps().clear()
        gb."root"<<drawLTree()
    }
    def gr = new GraphicsRenderer()
    def gb = gr.gb


   
}
