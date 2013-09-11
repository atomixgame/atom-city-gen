package editor2d.box

import groovy.swing.SwingBuilder
import groovy.swing.j2d.GraphicsBuilder
import groovy.swing.j2d.GraphicsPanel
import javax.swing.*
import java.awt.BorderLayout as BL
import sg.atom.geobox.*
import sg.atom.geo.box.BoxPlayGround

box = new BoxPlayGround()
box.init()

def jFrame;
def showFrame(){
    def swing = SwingBuilder.build {
        jFrame = frame( title: 'Box Play Ground', size: [800,640],
            locationRelativeTo: null, show: true ,defaultCloseOperation: JFrame.EXIT_ON_CLOSE,){
            panel(new GraphicsPanel(),id:"gp", graphicsOperation: box.getDrawBoxesOp() )
            panel(constraints:BL.NORTH ){
                button(text:"Run",actionPerformed:{
                        box.isStep = false;
                        box.divBox(list)
                   
                    })
                button(text:"Step",actionPerformed:{
                        box.isStep = true;
                        divBox(list)
                        if (canUpdate){
                            swing.gp.graphicsOperation = box.getDrawBoxesOp()
                            swing.gp.repaint()      
                        }
                        if (list.empty){
                            box.canUpdate = false;
                       
                        }
                    })
                button(text:"Reset",actionPerformed:{
                        box.isStep = false;
                        box.randomInitBox();
                        swing.gp.graphicsOperation = box.getDrawBoxesOp()
                        swing.gp.repaint()
                    })
                button(text:"Offset",actionPerformed:{
                        box.toJTSGeos(resultBoxes)
                        box.bufferGeos()
                        box.plot(geoList)
                        box.plot(bufferGeoList)
                    })
            }
        }
    }
}
showFrame()