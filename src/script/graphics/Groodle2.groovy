package script;

import groovy.swing.SwingBuilder  
import groovy.swing.j2d.GraphicsBuilder  
import groovy.swing.j2d.GraphicsPanel  
  
def gb = new GraphicsBuilder()  
  
def go = gb.group {  
balloon( x:20, y:20, w:100, bw:2, bc: 'black', 
   tabWidth:animate(0..20, duration:300, start:no,id:'at',startDelay:1000),  
   height: animate(0..100,duration:1000,start:no,id:'ah'),  
   arc:    animate(0..10, duration:1000,start:no,id:'aa'),  
   opacity:animate(0f..1f,duration:1000,start:no,id:'o') ){  
     // let's use a multipaint to fill the shape  
     multiPaint {  
       // base color  
       colorPaint( 'blue' ) 
       // highlights 
       linearGradient( x2: 0, y2:50 ){ 
          stop( s: 0, c: color('white').derive(a:0.75) ) 
          stop( s: 0.66, c: color('white').derive(a:0) )  
       }  
    }  
} 

// this group is the ballon popup  
group( o: 0, id: 'popup', f: 'blue', bc: no ){  
   balloon( x:140, y: 40, w: 80, h: 60, tabWidth: 16, arc: 10,
            f: 'white', bc: 'black', bw: 2 )  
   font( size: 20, style: 'bold' )  
   text( x: 158, y: 50, text: 'Click' )  
   text( x: 164, y: 70, text: 'me!' )  
} 

def normal = color('red')
def highlight = color('white')
def pressed = color('green')

// animation trigger
star( cx: 180, cy: 130, f: normal, or: 15, ir: 8, id: 'star',
      mousePressed: {e ->
         star.fill = pressed
         at.restart()
         ah.restart()
         aa.restart()
         o.restart()
      },
      mouseReleased: {e-> star.fill = highlight},
      mouseEntered: {e->
         star.fill = highlight
         popup.opacity = 1
      },
      mouseExited: {e->
         star.fill = normal
         popup.opacity = 0
      })

}  
  
def swing = SwingBuilder.build {  
   frame( title: "Groodle #2", size: [240,200],   
          locationRelativeTo: null, show: true ){  
      panel( new GraphicsPanel(), graphicsOperation: go )  
   }  
} 