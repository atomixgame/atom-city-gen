package script.lsystem

import java.awt.*
import javax.swing.*
import java.awt.BorderLayout as BL
import java.awt.GridBagConstraints as GBC
import groovy.swing.j2d.*
import groovy.swing.SwingBuilder
import groovy.swing.impl.TableLayout
import java.beans.PropertyChangeListener

script = new LSystemScript()
script.makeLString()

def jFrame;
def showFrame(){
    swing = SwingBuilder.build {

        jFrame = frame( title: 'L System Script', pack:true,
            locationRelativeTo: null, show: true ,defaultCloseOperation: JFrame.EXIT_ON_CLOSE){
            borderLayout()
            
            panel(constraints:BL.NORTH
                ,background:Color.RED){
                button(text:"Run",actionPerformed:{
                        isStep = false;
                   
                    })
                button(text:"Step",actionPerformed:{
                        isStep = true;
                        if (canUpdate){
                            swing.gp.graphicsOperation = script.getDrawOp()
                            //swing.gp.repaint()      
                        }
                        if (list.empty){
                            canUpdate = false;
                       
                        }
                    })
                button(text:"Reset",actionPerformed:{
                        isStep = false;
                        swing.gp.graphicsOperation = script.getDrawOp()
                        //swing.gp.repaint()
                    }
                )
            }
            panel(constraints:BL.CENTER){
                panel(new GraphicsPanel(),
                    id:"gp", 
                    graphicsOperation: script.getDrawOp(),
                    background:Color.LIGHT_GRAY,
                    preferredSize:[640,480])
            }

            panel(constraints:BL.EAST
                ,preferredSize:[200,400]){
                gridLayout(rows:4,cols:1)
                panel(preferredSize:[200,30]){
                    label(id:"lblSteps",text:"Steps")
                    /*
                    s1=slider(value:bind(source:script,sourceProperty:"steps" ,target:script,targetProperty:"steps",value:3),
                        maximum:40,
                        minimum:0,
                        minorTickSpacing :2,
                        majorTickSpacing:10,
                        paintLabels:true,
                        paintTicks:true,
                        stateChanged:{updateGraphics()}
                        )
                      */
                     textField(text:bind{script.steps})
                }
                panel(preferredSize:[200,30]){
                    label(text:"Length")
                    slider(value:bind{script.startLength},
                        maximum:40,
                        minimum:5,
                        minorTickSpacing :2,
                        majorTickSpacing:10,
                        paintLabels:true,
                        paintTicks:true)
                    
                }
                panel(preferredSize:[200,30]){
                    label(text:"Angle")
                    slider(value:bind{script.angle},
                        maximum:360,
                        minimum:0,
                        minorTickSpacing :10,
                        majorTickSpacing:90,
                        paintLabels:true,
                        paintTicks:true)
                }
                panel(preferredSize:[200,30]){
                    label(text:"Axiom")
                    textField(text:bind{script.axiom})
                }
            }  
        }
    }
}
def updateGraphics(){
    println("Called update!")
    script.updateGraphics()
}
showFrame();

script.addPropertyChangeListener({ e ->
            updateGraphics()
        } as PropertyChangeListener);
        
print(script.rules);
