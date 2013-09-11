package sg.atom.geo.panel

import java.awt.Rectangle;
import java.awt.Color;

class GridPanel{
    float x;
    float y;
    float height;
    float width;
    
    List hcuts;
    List vcuts;
    
    List elements;
    List colorTheme;
    List rows;
    List cols;
    
    public GridPanel(x,y,width,height){
        this.x = x;
        this.y =y;
        this.height = height;
        this.width = width;
        this.elements = []
        this.elements << new GridElement([x:x,y:y,width:width,height:height]);
    }
    public def cut(aCut){
        /*
        if (aCut){
        throw new IllegalArgumentException("This cut is invalid !")
        } else {
            
        }
         */
       
        if (aCut.type ==aCut.CutType.Horizontal){
            hcuts<<aCut;
            // get the affected rows?
            
            // insert a new Row
            
            
        } else {
            // get the affected col?
            
            // insert a new Col
            
        }
    }
    public def cut(type,value){
        this.cut(new GridCut(type,value))
    }
    public join(){
        
    }
    
    public hole(){
        
    }
    def outSide(){
        return false;
    }
    public def rotate(){
        
    }
    
    public def inRange(){
        
    }
    def generateColorTheme(red,green,blue){
        
    }
    
    def getDrawBoxesOp(gb){
        def drawBoxes = gb.group (id:'lego'){
       
            //rect( x: pBox.x, y:  pBox.y, width:  pBox.width, height:  pBox.height, borderColor: 'darkRed', borderWidth: 1 )
            elements.each(){ box->
                def color=randomColor();
                /*
                if (isBorderBox(pBox,box)){
                    color = randomColor()
                } else {
                    color = Color.BLACK;
                }
                */
                rect( x: box.x, y:  box.y, width:  box.width, height:  box.height, borderColor: 'darkRed', borderWidth: 2, fill: color )
            }
            /*
            list.each(){ box->
                rect( x: box.x, y:  box.y, width:  box.width, height:  box.height, borderColor: 'darkGray', borderWidth: 1){
                    colorPaint( color('gray').derive(alpha:0.5) )
                }
            }
            */
        }
        return drawBoxes
    }
    def randomColor(){
        Random random = new Random()
        return new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
  
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
}
