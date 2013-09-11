/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package script.graphics


import javax.swing.*
import groovy.swing.SwingBuilder
import groovy.swing.j2d.GraphicsBuilder

swingView {
   panel( x: 10, y: 10 ) {
      gridLayout( cols: 1, rows: 4 )
      ['red','blue','green'].each { color ->
         button( label: color, foreground: color, 
                 actionPerformed: {e-> p1.color2 = color} )
      }
      button( label: 'reset', actionPerformed: {e-> p1.color2 = 'white'} )
   }
}

rect( x: 90, y: 10, width: 100, height: 100 ){
   gradientPaint( id: 'p1', y2: 50 )
}

line( x1: 10, x2: 190, y1: 125, y2: 125 ){
   stroke( width: 2, dash: [12,12], dashphase: 0 )
}

swingView {
   panel( id: 'panel', x: 90, y: 140, preferredSize: [100,100], 
          opaque: true, background: 'white' )
}

def changeBackground = { shape -> 
   panel.background = ColorCache.getInstance().getColor(shape.fill) }
def borderOn = { e -> e.sourceShape.borderColor = 'black' }
def borderOff = { e -> e.sourceShape.borderColor = false }
rect( x: 10, y: 140, width: 60, height: 20, borderColor: false, fill: 'red',
      mouseClicked: {e-> changeBackground(e.sourceShape)},
      mouseEntered: borderOn, mouseExited: borderOff )
rect( x: 10, y: 165, width: 60, height: 20, borderColor: false, fill: 'blue',
      mouseClicked: {e-> changeBackground(e.sourceShape)},
      mouseEntered: borderOn, mouseExited: borderOff )
rect( x: 10, y: 190, width: 60, height: 20, borderColor: false, fill: 'green',
      mouseClicked: {e-> changeBackground(e.sourceShape)},
      mouseEntered: borderOn, mouseExited: borderOff )
rect( x: 10, y: 215, width: 60, height: 20, borderColor: false, fill: 'white',
      mouseClicked: {e-> changeBackground(e.sourceShape)},
      mouseEntered: borderOn, mouseExited: borderOff )

