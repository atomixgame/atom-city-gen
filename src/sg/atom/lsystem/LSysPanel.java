/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;

/*==============================================================================
class      LSysPanel
Content:   main drawing area (area where LSystems image appears)

===============================================================================*/
public class LSysPanel extends Panel {

    public Generate2 pixgen;
    public ParamPanel pnl_Specs;

    /* constructor */
    public LSysPanel(ParamPanel specpanel) {
        pnl_Specs = specpanel;
        setLayout(new BorderLayout());
        pixgen = new Generate2();

        pnl_Specs.setImagePanel(this); /* tell spec panel to draw pictures
        in this panel */


    }

    public void paint(Graphics g) {
        boolean b;
        Dimension pnldim;
        double height, width;

        pnldim = this.size();


        /* draw a border */
        g.setColor(Color.black);
        g.drawRect(0, 0, pnldim.width - 1, pnldim.height - 1);


        /* only draw if the Apply button has been pressed at least once */
        if (pnl_Specs.b_doApply == true) {

            height = (double) pnldim.height;
            width = (double) pnldim.width;

            /* run the program */
            b = pixgen.ProcessLSys(pnl_Specs, g, height, width);

        }


    }

    public Insets insets() {
        /* create border around outside of panel */
        return new Insets(1, 1, 1, 1);
    }
}