/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;

/*==============================================================================
class      ClrPanel
Content:   Panel with Color Bar (for selecting start color)

===============================================================================*/
public class ClrPanel extends Panel {

    public Globals glb;         /* globals */

    Color aColors[];

    /* constructor */
    public ClrPanel(Color aClrArray[]) {

        aColors = aClrArray;
    }

    public void paint(Graphics g) {
        boolean b;
        Dimension pnldim;
        int i;
        int clrBarWd;
        int startx;

        pnldim = this.size();

        clrBarWd = (pnldim.width - (25 + 80)) / glb.MAXCOLORS;


        for (startx = 25, i = 0; i < glb.MAXCOLORS; i++, startx += (clrBarWd + 1)) {
            g.setColor(aColors[i]);
            g.drawLine(startx, 0, startx + clrBarWd, 0);
            g.drawLine(startx, 1, startx + clrBarWd, 1);
        }





    }

    public Insets insets() {
        /* create border around outside of panel */
        return new Insets(1, 1, 1, 1);
    }
}
