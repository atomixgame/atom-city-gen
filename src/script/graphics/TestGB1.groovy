package script;

import java.awt.*  
import java.awt.geom.*  
import javax.swing.*  
//import org.jdesktop.swingx.geom.*  
import groovy.swing.SwingBuilder  
import groovy.swing.j2d.*
      
def gb = new GraphicsBuilder()
def graphicsOperation = gb.group {  
    rect( x: 10, y: 10, width: 120, height: 50, arcWidth: 20, arcHeight: 20, borderColor: 'darkRed', borderWidth: 3, fill: 'red' )  
    font( face: 'Helvetica', size: 30, style: Font.BOLD )  
    text( text: 'Groovy', x: 15, y: 18, borderColor: 'white', fill: 'black' )  
}  
      
def swing = SwingBuilder.build {  
    frame( id: 'frame', title: 'SimpleDrawing', size: [600,480], locationRelativeTo: null ){  
        panel( new GraphicsPanel(), graphicsOperation: graphicsOperation )  
    }  
}  

swing.doLater { frame.visible = true }