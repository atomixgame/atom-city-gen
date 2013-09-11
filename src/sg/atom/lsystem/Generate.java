/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

/*=========================================================================
Class Generate

This class is basically the common constants for the generations of
the Lsystems string
===========================================================================*/

public abstract class Generate extends Object {

    /* constants */
    static final int MAXPROD = 50;   /* max depth of productions */

    static final int MAXAXIOM = 100; /* max length of axiom */

    static final int MAXSTR = 30000; /* max size of final generation string */

    static final int MAXCHARS = 256; /* max number of ASCII character codes */

    static final int MAXIGNORE = 50; /* max number of ignored symbols */

    static final int MAXANGLE = 40;  /* max number of rotations in 360 degrees */

    static final int MAXSCALE = 100; /* if scale is 100, full screen, 0 none*/

    static final double TWO_PI = 6.2831853;
    static final int MAXSTACK = 40;  /* max number of branches in pattern */

    static final int LEFT = 1;       /* dimensions of canvas */

    static final int RIGHT = 510;
    static final int TOP = 1;
    static final int BOTTOM = 510;
}

