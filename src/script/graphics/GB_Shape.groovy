package script.graphics

import groovy.swing.SwingBuilder  

import groovy.swing.j2d.GraphicsPanel  
import java.awt.BorderLayout as BL  
  
def gb = new GraphicsBuilder()  
  
def shapes = [  
  'Arrow': gb.group {  
     arrow( x: 20, y: 20, width: 100, height: 60, /*rise: 0.5, depth: 0.5*/  
            fill: 'red', borderColor: 'black' )  
  },  
    'Arrow2': gb.group {  
     arrow( x: 120, y: 220, width: 100, height: 60, /*rise: 0.5, depth: 0.5*/  
            fill: 'blue', borderColor: 'black' )  
  }, 
      'Arrow3': gb.group {  
     arrow( x: 220, y:320, width: 100, height: 60, /*rise: 0.5, depth: 0.5*/  
            fill: 'yellow', borderColor: 'black' )  
  }, 
        'Arrow4': gb.group {  
     arrow( x: 320, y:20, width: 100, height: 60, /*rise: 0.5, depth: 0.5*/  
            fill: 'green', borderColor: 'black' )  
  },
]  
SwingBuilder.build {  
  frame( title: 'jSilhouette Shapes (Groovy)',   
    size: [500,500],  
    show: true ) {  
    borderLayout()  
    panel( new GraphicsPanel(), id: 'canvas', constraints: BL.CENTER )  
    panel( constraints: BL.WEST) {  
      gridLayout( columns: 2, rows: shapes.size()/2 )  
      shapes.each { shape, graphicsOperation ->  
        button( shape, actionPerformed: { evt ->  
          canvas.graphicsOperation = graphicsOperation  
        })  
      }  
    }  
  }
}