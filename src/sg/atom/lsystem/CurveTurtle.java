/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

/*===============================================================================
Class:     CurveTurtle
Contents:  contains turtle information for drawing a hermite curve

===============================================================================*/
public class CurveTurtle extends Object {

    public Turtle p1;   /* first tangent vector, or first control point */

    public Turtle p2;   /* start position for curve, or second control pt */

    public Turtle p3;   /* end position for curve, or third control pt */

    public Turtle p4;   /* end tangent vector, or fourth control pt */

    public boolean bDoDraw;      /* flag whether draw between points or not */


    /*------------------
    Constructor
    -------------------*/
    CurveTurtle() {
        this.p1 = new Turtle();
        this.p2 = new Turtle();
        this.p3 = new Turtle();
        this.p4 = new Turtle();

    }

    /*---------------------------------------------------------------------
    Method:   getvals
    Purpose:  copy over the values from another curve turtle into
    this one
    ---------------------------------------------------------------------*/
    public void getvals(CurveTurtle t) {
        this.p1.getvals(t.p1);
        this.p2.getvals(t.p2);
        this.p3.getvals(t.p3);
        this.p4.getvals(t.p4);
        this.bDoDraw = t.bDoDraw;
    }
}
