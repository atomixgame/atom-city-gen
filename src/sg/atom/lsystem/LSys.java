package sg.atom.lsystem;

/*==============================================================================
Application:  LSystems in Java
Author:       Paula A. Cooper
Purpose:      University of Calgary CPSC 553 course project

Source:  Please note that much of this code has been converted from a C program
into Java.  The initial version of this code (Plant and Fractal Generator, or
pfg) was written by Przemyslaw Prusinkiewicz (Copyright (C) 1988) and is 
found in the "Lindenmayer Systems, Fractals and Plants" SIGGRAPH '88 course 
notes written by Prusinkiewicz and Hanan.

==============================================================================*/
import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.JFrame;

/*===============================================================================
Applet  LSys
===============================================================================*/
public class LSys extends JFrame {

    public LSysPanel dispPanel;
    public ParamPanel specPanel;
    public Graphics drawarea;

    public void init() {
        this.setSize(800, 600);
        setLayout(new BorderLayout());
        specPanel = new ParamPanel();
        add("North", specPanel);
        dispPanel = new LSysPanel(specPanel);
        add("Center", dispPanel);
        drawarea = dispPanel.getGraphics();

        //dispPanel.show();
        //specPanel.show();

    }

    public void start() {
        //dispPanel.enable();
    }

    public static void main(String args[]) {
        LSys frame = new LSys();
        frame.init();
        frame.setVisible(true);

    }
}
