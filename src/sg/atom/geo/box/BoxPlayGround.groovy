package sg.atom.geo.box

import groovy.swing.j2d.GraphicsBuilder
import groovy.swing.j2d.GraphicsPanel
import groovy.swing.j2d.*
import java.awt.Rectangle;
import java.awt.Color;
import geoscript.geom.*
import static geoscript.render.Plot.plot;


class BoxPlayGround{
    // UI
    def gr = new GraphicsRenderer()
    def gb = gr.gb

    // Algorimth

    def minArea = 80;
    def int maxDepth = 5;
    def list=[];
    def resultBoxes = [];
    def isStep = false;
    def canUpdate = true;
    def minSeg = 40
    def count = 0
    def pBox = null;
    def geoList=[]
    def bufferGeoList=[]

    def init(){
        randomInitBox()
        divBox(list)
        toJTSGeos(resultBoxes)
        bufferGeos()
    }
    
    def randomInitBox(){
        def random = new Random()
        count=0
        list.clear()
        resultBoxes.clear()
        canUpdate= true;
        (1..1).each{
            def x = random.nextInt(100)
            def y= random.nextInt(100)
            def w = 200+random.nextInt(300)
            def h = 150+random.nextInt(200)

            pBox = new Rectangle(x,y,w,h);
            list<< pBox;
        }
    }

    def getDrawBoxesOp(){
        def drawBoxes = gb.group (id:'lego'){
       
            rect( x: pBox.x, y:  pBox.y, width:  pBox.width, height:  pBox.height, borderColor: 'darkRed', borderWidth: 1 )
            resultBoxes.each(){ box->
                def color;
                if (isBorderBox(pBox,box)){
                    color = randomColor()
                } else {
                    color = Color.BLACK;
                }
                rect( x: box.x, y:  box.y, width:  box.width, height:  box.height, borderColor: 'darkRed', borderWidth: 2, fill: color )
            }
            list.each(){ box->
                rect( x: box.x, y:  box.y, width:  box.width, height:  box.height, borderColor: 'darkGray', borderWidth: 1){
                    colorPaint( color('gray').derive(alpha:0.5) )
                }
            }
        }
        return drawBoxes
    }
    def divBox(queue){
        if (queue.isEmpty()) {
            println "Empty !"
            return;
        }
        /*
        queue.each{box->
        if ((box.height * box.width) < minArea){
        println("No match box found!")
        JOptionPane.showMessageDialog(jFrame,"No match box found!");
        return;
        }
        }
         */
        Rectangle box = queue.pop()
   
        def twoRect = splitRect(box);
        if (twoRect!=null){
            def rectA = twoRect.get(0)
            def rectB = twoRect.get(1)
       
            queue << rectA
            queue << rectB
        } else {
       
            resultBoxes << box;
        }
        println (count++);
        if (!isStep){
            divBox(queue);
        }
    }

    def isBorderBox(pRect,cRect){
        return isBorderBox(pRect.x,pRect.y,pRect.width,pRect.height,cRect.x,cRect.y,cRect.width,cRect.height)
    }
    boolean isBorderBox(px,py,pw,ph,cx,cy,cw,ch){
        def px1 = px;
        def px2= px + pw;
        def py1= py;
        def py2= py + ph;
   
        def cx1 = cx;
        def cx2= cx + cw;
        def cy1= cy;
        def cy2= cy + ch;
   
        if ((px1==cx1)||(px2==cx2)||(py1==cy1)||(py2==cy2)){
            return true;
        } else {
            return false;
        }  
    }
    
    List<Rectangle> splitRect(Rectangle rect){
   
   
        def result = null;
        // Split rect
        def x=rect.x
        def y=rect.y
        def w=rect.width
        def h=rect.height
        def nv
        if (!isRectOK(x,y,w,h)){
            return null;
        }
        Random random = new Random()
        def div = 0.7 - random.nextFloat() * 0.3
        if (w<h){
            //split by height
            // Check area
            nv=h* div
            if ((!isRectOK(x,y,w,nv)) || (!isRectOK(x,y,w,h-nv))){
                return null;
            }
            result = [];
            result << newRect(x,y,w ,nv);
            result << newRect(x,y + nv,w ,h - nv);
        } else {
            nv=w* div
            if ((!isRectOK(x,y,nv,h)) || (!isRectOK(x,y,w-nv,h))){
                return null;
            }
            result = [];
            // split by Width  
            result << newRect(x,y,nv,h);
            result << newRect(x+ nv,y,w -nv,h);
        } 


   
   
        return result;
    }

    boolean isRectOK(x,y,w,h){
        def area= w * h
        if (w<minSeg){
            return false;
        }
        if (h<minSeg){
            return false;
        }
        if (area < minArea){
            return false;
        }
        return true;
    }
    def newRect(x,y,w,h){
        def xi=Math.round((float)x)
        def yi=Math.round((float)y)
        def wi=Math.round((float)w)
        def hi=Math.round((float)h)
   
        return new Rectangle(xi,yi,wi,hi)
    }
    def randomColor(){
        Random random = new Random()
        return new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
  
    }
    def rectToGeo(rect){
        def p1 = new Point(rect.x ,rect.y )
        def p2 = new Point(rect.x + rect.width,rect.y )
        def p3 = new Point(rect.x + rect.width,rect.y + rect.height)
        def p4 = new Point(rect.x ,rect.y + rect.height)
        def geo = new Polygon([p1.x,p1.y],[p2.x,p2.y],[p3.x,p3.y],[p4.x,p4.y],[p1.x,p1.y])
        return geo
    }
    def toJTSGeos(list){
        list.each{
            if (isBorderBox(pBox,it)){
                geoList<<rectToGeo(it)
            }
        }  
    }
    def bufferGeos(){
        geoList.each{
            def poly = it.buffer(-5)
            bufferGeoList << poly
        }  
    }

}
