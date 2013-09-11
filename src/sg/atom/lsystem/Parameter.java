/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

/*==============================================================================
Class Parameter

Contents:
contains the input parameters given by the user
==============================================================================*/
class Parameter extends Object {

    public String axiom;		    /* axiom */

    public int depth;                   /* depth of derivation */

    public int angle;                   /* angle factor (number of divisions in 360 degrees) */

    public int scale;                   /* scaling factor (how many pixels a "step" is) */

    public int nLnType;		    /* type of line to be drawn 
    (straight, 
    Hermite curve,
    B-Spline) */

    public int start_clr;		    /* starting color */

    /* constructor - initialize */

    Parameter() {
    }
}
