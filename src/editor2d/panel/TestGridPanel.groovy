package editor2d.panel

import groovy.swing.SwingBuilder
import groovy.swing.j2d.GraphicsBuilder
import groovy.swing.j2d.GraphicsPanel
import java.awt.BorderLayout as BL
import groovy.swing.j2d.*
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.*
import sg.atom.geo.panel.*

// UI
gr = new GraphicsRenderer()
gb = gr.gb

grid = new GridPanel(100,100,200,100)

//grid.cut()

grid.elements.each(){
    print it;
}

textDraw=gb.group(id:'textDraw'){
    rect( width: 200, height: 200, borderColor: false ){
        gradientPaint()
    }
    texturePaint( id: 'p', x: 0, y: 0, file: 'src\\images\\facade.jpg' )
    texturePaint( id: 'p2', x: 60, y: 60, file: 'src\\images\\facade.jpg' )
    rect( x: 0, y: 0, width: 100, height: 100, borderColor: 'red', fill: p, )
    circle( cx: 150, cy: 50, radius: 50, borderColor: 'red', fill: p )
    rect( x: 0, y: 100, width: 200, height: 100, borderColor: 'red', fill: p2 )
}

def jFrame;
def showFrame(){
    def swing = SwingBuilder.build {
        jFrame = frame( title: 'Test Grid Panel', size: [800,640],
            locationRelativeTo: null, show: true ,defaultCloseOperation: JFrame.EXIT_ON_CLOSE,){
            panel(new GraphicsPanel(),id:"gp", graphicsOperation: textDraw )
            
        }
    }
}

showFrame()