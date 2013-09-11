/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;
/*===============================================================================
Class:    Turtle
Contents:  the current orientation of the turtle in the drawing

x - x coordinate
y - y coordinate
dir - direction of turtle (ie. the number of rotations right from the
start position)
clrIdx - current color index of lines
lnWidth - width of lines
===============================================================================*/

public class Turtle extends Object {

    public double x;
    public double y;
    public int dir;
    public int clrIdx;
    public int lnWidth;

    /*---------------------------------------------------------------------
    Method:   getvals
    Purpose:  copy over the values from another turtle structure into
    this one
    ---------------------------------------------------------------------*/
    public void getvals(Turtle t) {
        this.x = t.x;
        this.y = t.y;
        this.dir = t.dir;
        this.clrIdx = t.clrIdx;
        this.lnWidth = t.lnWidth;
    }
}
