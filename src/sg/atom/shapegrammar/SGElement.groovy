package sg.atom.shapegrammar

import com.jme3.math.*

public class SGElement{
    String name ="element";
    int id = 1;
    String type;
    List data;
    List controls;
    ColorRGBA color;

    
    public SGElement(name,type,controls,data,color){
        this.name = name;
        this.type = type;
        this.data = [];
        //this.data.addAll(data);
        this.controls = [];
        //this.controls.addAll(controls);
        this.color = color;
    }
    public String toString(){
        return name + id;
    }
}
