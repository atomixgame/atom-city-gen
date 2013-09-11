package script.graphics;

import groovy.swing.SwingBuilder
import groovy.swing.j2d.*

def gr = new GraphicsRenderer()
def gb = gr.gb 

def go = gb.group {
def width = 150  
def height = 150
def cx = width/2 + 10
def cy = height/2 + 10

antialias on
group( name: "frogface" ){
  circle( cx: cx, cy: cx, r: width/2, bc: no ){  
     multiPaint {  
        colorPaint( color('green') )
        radialGradient( cx: cx, cy: cy, r: width/2 ) {  
           stop( s: 0, c: color(r: 6, g: 160, b: 76, a: 127) )  
           stop( s: 1, c: color(a: 204) )  
        } 
        radialGradient( cx: cx, cy: cy,   
                        fx: 55, fy: 35, r: width/1.4 ){  
           stop( s: 0,   c: color('white').derive(a:0.5) )  
           stop( s: 0.5, c: color('white').derive(a:0) )  
        }  
     }  
  }
  
  // left eye
  circle( cx: 40, cy: 50, r: 20, bc: 'black', f: 'white' )
  circle( cx: 43, cy: 53, r: 12,  bc: 'none', f: 'black' )
  
  // right eye
  circle( cx: 130, cy: 50, r: 20, bc: 'black', f: 'white' )
  circle( cx: 127, cy: 53, r: 12,  bc: 'none',  f: 'black' )
  
  // nostrils
  circle( cx: 75, cy: 80, r: 4, bc: 'black', f: 'black' )
  circle( cx: 95, cy: 80, r: 4, bc: 'black', f: 'black' )
  
  // mouth
  ellipse( cx: 85, cy: 120, rx: 30, ry: 10, bc: 'black', f: 'black' ){
    transformations { rotate( x: 85, y: 120, angle: -10 ) }
  }

  transformations {
     translate( x: 250, y: 60 )
  }
}

// eye patch
add( bc: 'black', f: 'black', id: 'eyepatch' ) {
   rect( x: 10, y: 55, w: 150, h: 12 )
   circle( cx: 40, cy: 80, r: 20 )
}

// goatee
triangle( x: 60, y: 210, w: 40, angle: 180, f: 'brown', id: 'goatee' )

// moustache
add( f: 'brown', id: 'moustache' ){
   ellipse( cx: 65, cy: 140, rx: 25, ry: 8 ){
      transformations { rotate( x: 65, y: 140, angle: -12 ) }
   }
   ellipse( cx: 105, cy: 140, rx: 25, ry: 8 ){
      transformations { rotate( x: 105, y: 140, angle: 12 ) }
   } 
}

// hat
add( f: 'orange', id: 'hat' ){
   triangle( x: 70, y: 230, w: 140, h: 40 )
   rect( x: 100, y: 180, w: 80, h: 40 )
   filters {
      shapeBurst( merge: true, type: 'up' ){
         linearColormap( color1: color('orange').darker(), color2: 'orange' )
      }
      lights()
   }
}

swingView {
   button( 'Reset', x: 5, y: 5, actionPerformed: {e ->
      ['eyepatch','goatee','moustache','hat'].each {
         gb."$it".txs.clear()
      }
   })
}

}

def dragShape = { e ->
   def shape = e.target
   def bounds = shape.area.bounds
   def location = shape.props.location
   def dx = e.event.x - shape.props.dragPoint.x
   def dy = e.event.y - shape.props.dragPoint.y

   if( !shape.props.dragging ){
      if( !shape.txs['location'] ){
         shape.txs << gb.translate( name: 'location',
                      x: location.x, y: location.y )
      }else{
         shape.txs['location'].x = location.x
         shape.txs['location'].y = location.y
      }
      shape.props.dragging = true
   }

   if( !shape.txs['drag'] ){
      shape.txs << gb.translate( name: 'drag', x: dx, y: dy )
   }else{
      shape.txs['drag'].x = dx
      shape.txs['drag'].y = dy
   }
}

def startDrag = { e -> 
   def shape = e.target
   def bounds = shape.area.bounds
   if( !shape.props.anchor ){ 
      shape.props.anchor = [x:bounds.x,y:bounds.y]
   }
   if( !shape.props.location ){
      shape.props.location = [x:0,y:0]
   }else{
      shape.props.location = [
         x: bounds.x - shape.props.anchor.x,
         y: bounds.y - shape.props.anchor.y
      ]
   }
   shape.props.dragPoint = [x:e.event.x,y:e.event.y]
   shape.borderWidth = 3 
   shape.borderColor = 'red'
}
def endDrag = { e ->
   def shape = e.target
   shape.props.dragging = false
   shape.borderWidth = 1
   shape.borderColor = 'black'
}

['eyepatch','goatee','moustache','hat'].each {
   def shape = gb."$it"
   shape.mouseDragged = dragShape
   shape.mousePressed = startDrag
   shape.mouseReleased = endDrag
   shape.mouseExited = endDrag
}

def swing = SwingBuilder.build {
   frame( title: 'Groodle #3', size: [500,320],
          locationRelativeTo: null, show: true ){
      panel( new GraphicsPanel(), graphicsOperation: go )
   }
}