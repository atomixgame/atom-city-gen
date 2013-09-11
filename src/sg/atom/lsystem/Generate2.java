/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

import java.awt.Color;
import java.awt.Graphics;

/*===============================================================================
===============================================================================*/
class Generate2 extends Generate {

    public Turtle curpos, prevpos;		/* current position, previous position */

    public Pixel startpos;			/* start position */

    public Box boxdim;
    public Production aRules[];		/* array of production rules */

    public int iNumRules;			/* length of aRules array */

    public Parameter Specs;			/* specifications on what/how to derive */

    public String sCurLvl;			/* string with currently evaluated LSystems pattern */

    public StringBuffer sNextLvl;		/* next level of LSystems pattern 
    ... used when evaluating
    pattern */

    public int iCur;			/* position in sCurLvl */

    public String Ignore;			/* characters to be ignored */

    public int nIncSz;			/* size of a step when go forward */

    public int clrIdx;   			/* index into array of colors */

    public Globals glb;                     /* globals */

    /*-----------------
    items for drawing lines of
    different width
    -----------------*/
    public int left_adj = 0;			/* left width adjustment */

    public int right_adj = 0;              	/* right width adjustment */

    int iLnWidth = 1;                           /* current line width */

    int iHalf_Wd = 0;            		/* half line width, floored */

    double RotRad = 0;                          /* radians rotated in a 
    single rotation */



    /* constructor */
    public Generate2() {
        /* instantiate object variables */
        this.curpos = new Turtle();
        this.prevpos = new Turtle();
        this.startpos = new Pixel();
        this.boxdim = new Box();


        this.aRules = new Production[0];
        this.iNumRules = 0;
        this.Specs = new Parameter();
        this.sCurLvl = "";
        this.sNextLvl = new StringBuffer();
        this.iCur = 0;
        this.Ignore = "Ff+-|;:#!";
        this.nIncSz = 0;

    }

    public static void DoGen() {
    }


    /*--------------------------------------------------------------------
    Method:   ProcessLSys
    Purpose:  generate the LSystems string from the current axiom and 
    interpret the string to create the image
    
    Returns:  true  -- success
    false -- otherwise
    ---------------------------------------------------------------------*/
    public boolean ProcessLSys(ParamPanel pnl_specs, Graphics g, double pnl_ht, double pnl_wd) {

        boolean bOk;


        startpos.horiz = 0;
        startpos.vert = 0;


        bOk = GetSpecs(pnl_specs);

        if (bOk == false) {
            return (false);
        }


        bOk = Derive();


        bOk = Interpret(g, pnl_ht, pnl_wd, pnl_specs.aColors);

        return (true);
    }


    /*------------------------------------------------------------------
    Method:   GetSpecs
    Purpose:  get the specifications such as the rules and the starting axiom
    out of the GUI widgets (listbox, etc) and place them in their 
    designated data structures
    
    Parameters:  pnl_specs - panel containing widgets with specifications
    of how to draw LSystems item
    
    Returns:  true  -- if got everything and rules had proper syntax
    false -- otherwise
    -------------------------------------------------------------------*/
    public boolean GetSpecs(ParamPanel pnl_specs) {
        int iPred, iPost, iSucc;	/* separator indexes in rule */
        String sRule;			/* holds rule being parsed */
        int i;


        i = 0;



        iNumRules = pnl_specs.lst_rules.getItemCount();
        this.aRules = new Production[iNumRules];

        if (pnl_specs.cbg_Lines.getCurrent() == pnl_specs.cb_strt) {
            Specs.nLnType = glb.LN_STRT;
        } else if (pnl_specs.cbg_Lines.getCurrent() == pnl_specs.cb_hermite) {
            Specs.nLnType = glb.LN_HERMITE;
        } else {
            Specs.nLnType = glb.LN_BSPLINE;
        }


        Specs.axiom = pnl_specs.tb_axiom.getText();
        if (Specs.axiom == "") {
            return (false);
        }
        Ignore = pnl_specs.tb_ignore.getText();
        try {
            Specs.angle = Integer.parseInt(pnl_specs.tb_angle.getText());
        } catch (NumberFormatException e) {

            System.out.println("Error!  Angle must be an integer.");
            Specs.angle = 4;
            return (false);
        }
        try {
            Specs.scale = Integer.parseInt(pnl_specs.tb_scale.getText());
        } catch (NumberFormatException e) {
            System.out.println("Error!  Scale must be an integer.");
            Specs.scale = 50;
            return (false);
        }

        try {
            Specs.depth = Integer.parseInt(pnl_specs.tb_depth.getText());
        } catch (NumberFormatException e) {
            System.out.println("Error!  Depth must be an integer.");
            Specs.depth = 3;
            return (false);
        }

        try {
            Specs.start_clr = Integer.parseInt(pnl_specs.tb_clr.getText());
        } catch (NumberFormatException e) {
            System.out.println("Error!  Color must be an integer.");
            Specs.start_clr = 0;
            return (false);
        }

        if (Specs.start_clr > glb.MAXCOLORS - 1) {
            Specs.start_clr = glb.MAXCOLORS - 1;
        } else if (Specs.start_clr < 0) {
            Specs.start_clr = 0;
        }


        for (i = 0; i < iNumRules; i++) {
            sRule = pnl_specs.lst_rules.getItem(i);


            /* parse through the rule for the separators 
            '<','>','-->'
            where the rule is of the form:
            
            leftcontext < predecessor > rightcontext --> successor
             */
            iPred = sRule.indexOf('<');
            if (iPred != -1) {
                iPost = sRule.indexOf('>', iPred);
            } else {

                System.out.println("Error!  Missing '<' in rule.");
                return (false);
            }
            if (iPost != -1) {
                iSucc = sRule.indexOf("-->", iPost);
            } else {
                System.out.println("Error!  Missing '>' in rule.");
                return (false);
            }
            if (iSucc == -1) {
                System.out.println("Error!  Missing '-->' in rule.");
                return (false);
            }


            /* put the rule into the array of rules */
            aRules[i] = new Production();

            /* left context */
            aRules[i].lCtxt = sRule.substring(0, iPred);
            aRules[i].lCtxt = FixStr(aRules[i].lCtxt);
            aRules[i].lCtxtLen = aRules[i].lCtxt.length();

            /* predecessor */
            aRules[i].pred = sRule.substring(iPred + 1, iPost);
            aRules[i].pred = FixStr(aRules[i].pred);
            aRules[i].predLen = aRules[i].pred.length();

            /* right context */
            aRules[i].rCtxt = sRule.substring(iPost + 1, iSucc);
            aRules[i].rCtxt = FixStr(aRules[i].rCtxt);
            aRules[i].rCtxtLen = aRules[i].rCtxt.length();

            /* successor */
            aRules[i].succ = sRule.substring(iSucc + 3);
            aRules[i].succ = FixStr(aRules[i].succ);
            aRules[i].succLen = aRules[i].succ.length();


        }

        return (true);

    }


    /*------------------------------------------------------------------
    Method:   FixStr
    Purpose:  adjust the string passed (assumed to be a substring portion
    of a rule such as the:
    left context,
    right context,
    predecessor, or
    successor)
    
    so that if it is simply the '*' character (which stands for
    the empty string), it is replaced by an empty string
    
    Returns:  
    the new String contents
    -------------------------------------------------------------------*/
    public String FixStr(String sStr) {
        sStr = sStr.trim();
        if (sStr.equals("*") == true) {
            sStr = new String("");
        }

        return (sStr);

    }

    /*--------------------------------------------------------------------
    Method:   Derive
    Purpose:  determine the LSystems pattern created using the current
    specifications and production rules
    
    Algorithm:	
    
    Returned:  
    true -- success in derivation
    false -- otherwise
    Globals Changed:
    
    sCurLvl - will contain final pattern created
    
    Globals Used:
    sCurLvl - contains
    
    --------------------------------------------------------------------*/
    public boolean Derive() {
        int i;

        sCurLvl = Specs.axiom;
        iCur = 0;
        sNextLvl = new StringBuffer();

        /* derive each level */
        for (i = 1; i <= Specs.depth; i++) {

            iCur = 0;
            /* keep going until done deriving current level string */
            while (iCur < sCurLvl.length()) {
                ApplyProd(FindProd());
            }
            sCurLvl = new String(sNextLvl);
            sNextLvl = new StringBuffer();

        }

        return (true);

    }


    /*-------------------------------------------------------------------
    Method:   ApplyProd
    Purpose:  replace a predecessor with its successor based on a
    specific production rule
    
    Algorithm:	If we found a production rule to match we copy the 
    successor for the current predecessor into the next
    level (thus applying the production rule).  If no
    production rule matched this predecessor then we
    copy the first character of this predecessor string
    down into the next level and shift the start of 
    the predecessor to the right one (no longer consider
    this character part of the predecessor)
    
    
    Params:   RuleIdx  -  index for production rule (-1 if no production
    rule should be applied)
    Globals Changed:
    iCur - set to point to next predecessor to be evaluated
    (skips past the current one which just had the
    production rule applied)
    sNextLvl - has the successor for the production rule
    just applied appended to the end of it
    --------------------------------------------------------------------*/
    public void ApplyProd(int RuleIdx) {
        if (RuleIdx != -1) {
            /* apply production */
            sNextLvl.append(aRules[RuleIdx].succ);	/* apply rule */
            iCur += aRules[RuleIdx].predLen;	/* skip past current predecessor to next predecessor */
        } else {
            /* move one character down to next level (out of 
            predecessor) */
            sNextLvl.append(sCurLvl.charAt(iCur));
            iCur++;   /* skip past one character */
        }

    }

    /*------------------------------------------------------------------
    Method:    FindProd
    Purpose:   find the production rule that applies to the predecessor
    at the current position (iCur) in the current level 
    (sCurLvl)
    Tests for context free, context sensitive left, context
    sensitive right, and context sensitive right and left.
    
    Notice that the FIRST rule that matches (whether context
    free or context sensitive) is applied!
    
    Returns:   index of rule if found
    otherwise... -1  (no rule found)
    -------------------------------------------------------------------*/
    public int FindProd() {

        int i;
        i = 0;

        while (i < iNumRules) {


            /* if the predecessor, left condition, and right
            condition match then the production matches so 
            return it, otherwise try the next production rule */

            if (prefix(aRules[i].pred, sCurLvl, iCur)
                    && rcondiff(aRules[i].rCtxt, sCurLvl, iCur + aRules[i].predLen)
                    && lcondiff(aRules[i].lCtxt, sCurLvl, iCur - 1)) {
                return (i);	/* a match! */
            } else {
                i++;
            }

        }

        return (-1);	/* no rule found */


    }

    /*------------------------------------------------------------------
    Method:	  doIgnore
    Purpose:  determine whether the current character is one of the
    "irrelevant" or "ignorable" characters in terms of 
    processing an LSystems pattern
    
    Parms:	  sStr - string to check in
    Idx  - index into string
    
    Returns:  true -- yes, should ignore the current char
    false -- no, should not ignore the current char
    -------------------------------------------------------------------*/
    public boolean doIgnore(String sStr, int Idx) {

        int i;

        for (i = 0; i < Ignore.length(); i++) {
            if (sStr.charAt(Idx) == Ignore.charAt(i)) {
                return (true);
            }
        }

        return (false);

    }


    /*------------------------------------------------------------------
    Method:   prefix
    Purpose:  see if the rule string is the starting portion found at the
    current position (cIdx) in the derivation string
    
    Return:   true - yes, is a prefix
    false - no, is not a prefix
    
    -------------------------------------------------------------------*/
    public boolean prefix(String sRule, String sCur, int cIdx) {
        return (sCur.regionMatches(cIdx, sRule, 0, sRule.length()));

    }

    /*-------------------------------------------------------------------
    Method:  rcondiff
    Purpose:   check for a matching right context in a rule and the
    current position in the "derivation" string
    
    Parameters:     sRule -- right context in a rule
    sCur  -- derivation string
    cIdx  -- current position in derivation string
    
    Returns:      true - yes, the rule and right context matched
    false - no match
    
    --------------------------------------------------------------------*/
    public boolean rcondiff(String sRule, String sCur, int cIdx) {

        int rIdx = 0;

        while (true) {

            if (rIdx >= sRule.length()) {
                return (true);       /* success! */
            } else if (cIdx >= sCur.length()) {
                /* we were at farthest right side of the current
                derivation level's string (sCur).. so there is
                no right context.. meaning it would only have
                matched if there no right context in rule */
                return (false);	    /* no match!! */
            } else if (doIgnore(sCur, cIdx) == true) {
                cIdx++;
            } else if (sCur.charAt(cIdx) == '[') {
                cIdx = this.skipright(cIdx, sCur);
                if (cIdx == -1) {
                    System.out.println("Error!  Missing ']' to terminate branch");
                    return (false);
                }
            } else if (!(sRule.charAt(rIdx) == sCur.charAt(cIdx))) {
                return (false);      /* no match!! */
            } else {
                /* matched on this character, so try next */
                rIdx++;
                cIdx++;
            }

        }

    }

    /*------------------------------------------------------------------
    Method:  skipright
    Purpose: skip over a branching section in a string (ie. from [ to
    ]) and also any subbranches contained in that branch
    
    Parameters:
    sIdx  - index into string (assumed to be on first '['
    sStr  - string to search in 
    
    Returns:        -1 -- error!!! didn't find closing ]
    other -- index into string after matching ]
    
    -------------------------------------------------------------------*/
    int skipright(int sIdx, String sStr) {
        int level = 0;
        sIdx++;         /* get past first [ */
        while (sIdx < sStr.length()) {
            switch (sStr.charAt(sIdx)) {
                case '[':
                    level++;
                    break;

                case ']':
                    if (level == 0) {

                        return (++sIdx);
                    } else {

                        level--;
                    }
                    break;
                default:
                    break;

            }


            sIdx++;
        }

        return ((int) -1);     /* no matching ] */
    }

    /*-------------------------------------------------------------------
    Method:  lcondiff
    Purpose:   check for a matching left context in a rule and the
    current position in the "derivation" string.
    Notice that we move through the string from left to right
    
    Parameters:     sRule -- right context in a rule
    sCur  -- derivation string
    cIdx  -- current position in derivation string
    
    Returns:      true - yes, the rule and left context matched
    false - no match
    
    --------------------------------------------------------------------*/
    public boolean lcondiff(String sRule, String sCur, int cIdx) {

        int rIdx = sRule.length();   /* start at end of rule */
        rIdx--;


        while (true) {

            if (rIdx < 0) {
                return (true);       /* success! */
            } else if (cIdx < 0) {
                /* we were at farthest left side of the current
                derivation level's string (sCur).. so there is
                no left context.. meaning it would only have
                matched if there was no left context in rule */
                return (false);	    /* no match!! */
            } else if (doIgnore(sCur, cIdx) == true) {
                cIdx--;
            } else if (sCur.charAt(cIdx) == '[') {
                cIdx--;
            } else if (sCur.charAt(cIdx) == ']') {
                cIdx = this.skipleft(cIdx, sCur);
                if (cIdx == -1) {
                    System.out.println("Error!  Missing ']' to terminate branch");
                    return (false);
                }
            } else if (!(sRule.charAt(rIdx) == sCur.charAt(cIdx))) {
                return (false);      /* no match!! */
            } else {
                /* matched on this character, so try next one */

                rIdx--;
                cIdx--;

            }


        }

    }

    /*------------------------------------------------------------------
    Method:  skipleft
    Purpose: skip over a branching section in a string (ie. from [ to
    ]) and also any subbranches contained in that branch.  Notice
    that we go through through the string from right to left
    
    Parameters:
    sIdx  - index into string (assumed to be on first '['
    sStr  - string to search in 
    
    Returns:        -1 -- error!!! didn't find closing ]
    other -- index into string after matching ]
    
    -------------------------------------------------------------------*/
    int skipleft(int sIdx, String sStr) {
        int level = 0;
        sIdx--;         /* get past first ] */
        while (sIdx > 0) {
            switch (sStr.charAt(sIdx)) {
                case ']':
                    level++;
                    break;

                case '[':
                    if (level == 0) {

                        return (--sIdx);
                    } else {

                        level--;
                    }
                    break;
                default:
                    break;

            }


            sIdx--;
        }

        return ((int) -1);     /* no matching ] */
    }

    /*----------------------------------------------------------------------
    Method:    Interpret
    Purpose:   interpret the meaning of the LSystems string
    
    Parms:	   g - graphics object to do drawing in
    pnl_ht - height of drawing area
    pnl_wd - width of drawing area
    
    ----------------------------------------------------------------------*/
    public boolean Interpret(Graphics g, double pnl_ht, double pnl_wd, Color aColors[]) {
        nIncSz = 1;
        /* determine points, but don't actually draw.. just find
        dimensions of bounding box */
        draw(sCurLvl, nIncSz, Specs.angle, false, g, aColors);

        SetDrawParam(pnl_ht, pnl_wd);

        /* draw it!! */

        draw(sCurLvl, nIncSz, Specs.angle, true, g, aColors);

        return (true);
    }

    /*----------------------------------------------------------------------
    Method:   SetDrawParam
    Purpose:  Adjusts the step size (nIncSz) as well as the starting position
    of the turtle based upon the bounding box that was required
    for a step size of one
    Assumptions:	assumes that the bounding box contains the bounding
    dimensions given a step size of one
    
    
    Globals Changed:
    nIncSz  -- adjust increment size for drawing in current
    panel dimensions
    
    startpos.horiz -- starting position
    startpos.vert  
    ------------------------------------------------------------------------*/
    public void SetDrawParam(double pnl_ht, double pnl_wd) {

        double xscale, yscale, sc;

        /* determine how relate a "step" to the width and height
        of the actual screen */
        xscale = (pnl_wd / (boxdim.xmax - boxdim.xmin));
        yscale = (pnl_ht / (boxdim.ymax - boxdim.ymin));



        /* determine whether the width or height is the tighter bound */
        if (xscale > yscale) {
            sc = yscale;
        } else {
            sc = xscale;
        }


        /* determine what percentage of the full step should be used
        based on the scale factor.. if the scale is the maximum
        scale then the full sized step will be taken */
        nIncSz = (int) (Math.floor((double) ((sc * Specs.scale) / MAXSCALE)));

        startpos.horiz = (int) ((pnl_wd - (nIncSz * (boxdim.xmin + boxdim.xmax - 1))) / 2);
        startpos.vert = (int) ((pnl_ht - (nIncSz * (boxdim.ymin + boxdim.ymax - 1))) / 2);


    }

    /*----------------------------------------------------------------------
    Method:    drawWideLine
    
    Purpose:   draw a line of a specified width
    
    
    If a line of single width is desired the normal drawline
    facility is called.
    
    If the line is of width greater than one, we must create
    a polygon (since there is no method (?) of specifying line
    width in Java, and rectangles cannot be used because they
    only permit a horizontal orientation)
    
    
    
    ----------------------------------------------------------------------*/
    void drawWideLine(Turtle startpt, Turtle endpt, Graphics g) {
        int aPolyX[] = new int[4];	/* array of X coords in polygon */
        int aPolyY[] = new int[4];	/* array of Y coords in polygon */

        Turtle ll1 = new Turtle();
        Turtle lr1 = new Turtle();
        Turtle ll2 = new Turtle();
        Turtle lr2 = new Turtle();
        Turtle ll3 = new Turtle();
        Turtle lr3 = new Turtle();
        Turtle ul = new Turtle();
        Turtle ur = new Turtle();
        double dAng;


        if (iLnWidth == 1) {

            g.drawLine((int) startpt.x, (int) startpt.y, (int) endpt.x, (int) endpt.y);

        } else {

            /*-------------------
            assume the start point is at
            the origin and that the direction is
            North.  Add a left most point and
            right most point, to give the spread
            of the line width across the x axis.
            
            Then, rotate these two outer points
            by the amount that the line is to be
            rotated.  This gives us the points 
            ll2 and lr2 which have been rotated about
            the origin.
            
            Add these new points (ll2, lr2) to
            the start point to give the lower left
            and lower right points (ll3 and lr3)
            of the polygon to be drawn
            -------------------*/

            ll1.x = left_adj;
            ll1.y = 0;
            lr1.x = right_adj;
            lr1.y = 0;

            dAng = -(endpt.dir * RotRad);
            ll2.x = (Math.cos(dAng) * ll1.x)
                    - (Math.sin(dAng) * ll1.y);
            ll2.y = (Math.sin(dAng) * ll1.x)
                    + (Math.cos(dAng) * ll1.y);
            ll3.x = ll2.x + startpt.x;
            ll3.y = ll2.y + startpt.y;

            lr2.x = (Math.cos(dAng) * lr1.x)
                    - (Math.sin(dAng) * lr1.y);
            lr2.y = (Math.sin(dAng) * lr1.x)
                    + (Math.cos(dAng) * lr1.y);
            lr3.x = lr2.x + startpt.x;
            lr3.y = lr2.y + startpt.y;



            /*-------------------------
            Add the points that were rotated
            about the origin to the coordinates
            for the endpoint of the line, giving
            us the upper left and upper right
            points of the polygon (ul and ur)
            --------------------------*/

            ul.x = ll2.x + endpt.x;
            ul.y = ll2.y + endpt.y;

            ur.x = lr2.x + endpt.x;
            ur.y = lr2.y + endpt.y;


            /*------------
            Store arrays of X and Y
            coordinates for drawing
            Polygons
            ------------*/
            aPolyX[0] = (int) ll3.x;
            aPolyX[1] = (int) ul.x;
            aPolyX[2] = (int) ur.x;
            aPolyX[3] = (int) lr3.x;

            aPolyY[0] = (int) ll3.y;
            aPolyY[1] = (int) ul.y;
            aPolyY[2] = (int) ur.y;
            aPolyY[3] = (int) lr3.y;

            g.fillPolygon(aPolyX, aPolyY, 4);
        }

    }


    /*-----------------------------------------------------------------------
    Method:    draw
    
    
    
    1) Start by drawing to the North
    
    - keep a direction specification for the turtle where 
    dir = 0 is North
    dir = 1 is 360/iAngFac degrees clockwise of north
    (ie. one rotation right)
    dir = 2 is 2*(360/iAngFac) degrees clockwise of north
    (ie. two rotations right)
    etc...
    
    so rotations right add to the dir value, 
    rotations left subtract
    
    
    -----------------------------------------------------------------------*/
    boolean draw(String sPattern, int iStepSz, int iAngFac, boolean bDoDraw, Graphics g, Color aColors[]) {

        double SI[] = new double[MAXANGLE];
        double CO[] = new double[MAXANGLE];
        Turtle PosStack[] = new Turtle[MAXSTACK];
        CurveTurtle CrvPosStack[] = new CurveTurtle[MAXSTACK];
        double dAng = -TWO_PI / 4;       /* -90=270 Degrees */
        int i;
        int iMaxDir;		/* maximum direction counter value */
        int iStkIdx = -1;		/* index into stack array */
        int iMaxClrIdx = glb.MAXCOLORS - 1;
        CurveTurtle ct = new CurveTurtle();
        int iHalfAngFac = iAngFac / 2;         /* half of angle factor 
        (for turning around) */



        /*----
        Structures for storing first 3 points in current curve
        -----*/
        Turtle aFirstPts[] = new Turtle[3];
        int iPtsIdx = 0;


        /*-------
        Structures for creating and storing 4 vertices on a rectangle
        for producing lines of different width 
        -----*/


        left_adj = 0;			/* left width adjustment */
        right_adj = 0;              	/* right width adjustment */
        iHalf_Wd = 0;        		/* half line width, floored */


        RotRad = -(TWO_PI / (double) iAngFac);

        g.setColor(aColors[1]);


        boxdim.xmin = boxdim.xmax = curpos.x = prevpos.x = (double) (startpos.horiz) + 0.5;
        boxdim.ymin = boxdim.ymax = curpos.y = prevpos.y = (double) (startpos.vert) + 0.5;
        curpos.dir = prevpos.dir = 0;
        curpos.clrIdx = Specs.start_clr;
        curpos.lnWidth = iLnWidth = 1;

        if (Specs.nLnType == glb.LN_HERMITE) {
            ct.bDoDraw = false;
            ct.p2.getvals(curpos);
        } else if (Specs.nLnType == glb.LN_BSPLINE) {
            ct.bDoDraw = true;
            ct.p1.getvals(curpos);
            ct.p2.getvals(curpos);
            ct.p3.getvals(curpos);
            ct.p4.getvals(curpos);

            /* initialize storage of first 3 points in curve */
            aFirstPts[0] = new Turtle();
            aFirstPts[1] = new Turtle();
            aFirstPts[2] = new Turtle();
            aFirstPts[0].getvals(curpos);
            iPtsIdx = 1;
        }



        /* precalculate the Sine/Cosine values for all of the possible
        angle rotations that the turtle can perform */

        for (i = 0; i < iAngFac; i++) {


            SI[i] = (double) (iStepSz) * Math.sin(dAng);
            CO[i] = (double) (iStepSz) * Math.cos(dAng);


            dAng += TWO_PI / (double) iAngFac;

        }

        iMaxDir = --iAngFac;


        g.setColor(aColors[curpos.clrIdx]);
        iLnWidth = curpos.lnWidth;

        /* move through string from left to right and 
        manipulate the turtle on the screen as the 
        pattern string specifies */

        for (i = 0; i < sPattern.length(); i++) {

            switch (sPattern.charAt(i)) {

                case '|':	/* turn around (180 degrees) */
                    /*---------
                    if current direction is more than a 180
                    degree rotation, then subtract 180
                    or if less than 180 degrees then add
                    ---------*/
                    if (curpos.dir >= iHalfAngFac) {
                        curpos.dir -= iHalfAngFac;
                    } else {
                        curpos.dir += iHalfAngFac;
                    }
                    break;

                case '#':	/* increment line width */
                    curpos.lnWidth++;
                    iHalf_Wd = (int) (Math.floor(curpos.lnWidth / 2));

                    /*------------
                    if odd width, adjust x same both
                    left and right
                    
                    if even width, adjust x one less
                    on left
                    ---------*/
                    if ((curpos.lnWidth % 2) == 1) {
                        left_adj = -iHalf_Wd;
                        right_adj = iHalf_Wd;
                    } else {
                        left_adj = -(iHalf_Wd - 1);
                        right_adj = iHalf_Wd;
                    }
                    iLnWidth = curpos.lnWidth;


                    break;

                case '!':       /* decrement line width */
                    if (curpos.lnWidth > 1) {
                        curpos.lnWidth--;

                        iHalf_Wd = (int) (Math.floor(curpos.lnWidth / 2));

                        /*------------
                        if odd width, adjust x same both
                        left and right
                        
                        if even width, adjust x one less
                        on left
                        ---------*/
                        if ((curpos.lnWidth % 2) == 1) {
                            left_adj = -iHalf_Wd;
                            right_adj = iHalf_Wd;
                        } else {
                            left_adj = -(iHalf_Wd - 1);
                            right_adj = iHalf_Wd;
                        }

                    }
                    iLnWidth = curpos.lnWidth;

                    break;

                case ';':       /* increment color */
                    if (curpos.clrIdx == iMaxClrIdx) {
                        curpos.clrIdx = 0;
                    } else {
                        curpos.clrIdx++;
                    }
                    g.setColor(aColors[curpos.clrIdx]);

                    break;
                case ':':	/* decrement color */
                    if (curpos.clrIdx == 0) {
                        curpos.clrIdx = iMaxClrIdx;
                    } else {
                        curpos.clrIdx--;
                    }
                    g.setColor(aColors[curpos.clrIdx]);

                    break;

                case '+': 	/* right */
                    if (curpos.dir < iMaxDir) {
                        curpos.dir++;
                    } else {
                        curpos.dir = 0;   /* did a full 360 */
                    }
                    break;

                case '-':    	/* left */
                    if (curpos.dir > 0) {
                        curpos.dir--;
                    } else {
                        curpos.dir = iMaxDir;
                    }
                    break;


                case '[':	/* branching */

                    iStkIdx++;
                    if (iStkIdx < MAXSTACK) {
                        /* push current position onto stack */
                        PosStack[iStkIdx] = new Turtle();
                        PosStack[iStkIdx].getvals(curpos);
                        CrvPosStack[iStkIdx] = new CurveTurtle();
                        CrvPosStack[iStkIdx].getvals(ct);

                    } else {
                        System.out.println("Error!  Too many branches.");
                        return (false);
                    }
                    break;

                case ']':	/* returning from branch */

                    if (iStkIdx < 0) {
                        System.out.println("Error!  Missing closing ']'");
                        return (false);
                    } else {
                        /* pop branching position off of stack */
                        curpos.getvals(PosStack[iStkIdx]);
                        prevpos.getvals(curpos);

                        if (bDoDraw == true) {
                            /* draw first and last curve segments of previous 
                            curve */
                            if (Specs.nLnType == glb.LN_BSPLINE) {
                            }
                        }

                        ct.getvals(CrvPosStack[iStkIdx]);
                        iStkIdx--;
                    }
                    iLnWidth = curpos.lnWidth;
                    g.setColor(aColors[curpos.clrIdx]);


                    break;



                case 'F':	/* go forward and draw line */


                    /* x' = x + cos(ang)
                    y' = y + sin(ang)
                     */

                    curpos.x += CO[curpos.dir];
                    curpos.y += SI[curpos.dir];



                    /* draw line or update box dimension settings */
                    if (bDoDraw == true) {




                        /*----------------------------
                        determine what type of line to draw
                        straight, Hermite curve or BSpline curve
                        -----------------------*/
                        switch (Specs.nLnType) {
                            case Globals.LN_HERMITE:

                                if (ct.bDoDraw == true) {

                                    ct.p4.x = curpos.x - ct.p2.x;
                                    ct.p4.y = curpos.y - ct.p2.y;

                                    DrawHermite(ct, iStepSz, g);

                                    /*ct.bDoDraw=true;*/
                                    ct.p2.getvals(ct.p3);
                                    ct.p3.getvals(curpos);
                                    ct.p1.getvals(ct.p4);

                                } else {
                                    /*------------------------------------------
                                    We have got only the start position for 
                                    drawing a segment, so we can't draw yet
                                    ---------------------------------------*/
                                    ct.bDoDraw = true;
                                    ct.p3.getvals(curpos);
                                    ct.p1.x = curpos.x - ct.p2.x;
                                    ct.p1.y = curpos.y - ct.p2.y;




                                }

                                break;

                            case Globals.LN_BSPLINE:
                                /*------------------------
                                draw BSpline curve
                                -----------------------*/
                                ct.p1.getvals(ct.p2);
                                ct.p2.getvals(ct.p3);
                                ct.p3.getvals(ct.p4);
                                ct.p4.getvals(curpos);

                                /*-------------
                                don't draw curve for first 3 points, just
                                store their locations (for generating
                                the first curve segment later)
                                -------------*/
                                if (iPtsIdx < 3) {
                                    aFirstPts[iPtsIdx].getvals(curpos);
                                    iPtsIdx++;
                                } else {

                                    DrawBSpline(ct, iStepSz, g);
                                }
                                break;

                            case Globals.LN_STRT:

                                drawWideLine(prevpos, curpos, g);


                                break;
                        }



                        prevpos.getvals(curpos);  /* copy position */

                    } else {
                        UpdateBox(curpos);

                    }


                    break;


                case 'f':	/* go forward without drawing line */

                    /* x' = x + cos(ang)
                    y' = y + sin(ang)
                     */


                    curpos.x += CO[curpos.dir];
                    curpos.y += SI[curpos.dir];


                    /* update previous position or update box dimension settings */
                    if (bDoDraw == true) {
                        /* draw first and last curve segments of previous 
                        curve */
                        if (Specs.nLnType == glb.LN_BSPLINE) {
                            DrawFirstNLast(ct, aFirstPts, g, iStepSz, iPtsIdx);
                        }


                        if (Specs.nLnType == glb.LN_HERMITE) {
                            ct.bDoDraw = false;
                            ct.p2.getvals(curpos);
                        } else if (Specs.nLnType == glb.LN_BSPLINE) {
                            ct.bDoDraw = true;
                            ct.p1.getvals(curpos);
                            ct.p2.getvals(curpos);
                            ct.p3.getvals(curpos);
                            ct.p4.getvals(curpos);

                            /* initialize storage of first 3 points in curve */
                            aFirstPts[0].getvals(curpos);
                            iPtsIdx = 1;

                        }

                        prevpos.getvals(curpos);  /* copy position*/

                    } else {
                        UpdateBox(curpos);

                    }


                    break;
            }


        }
        if (bDoDraw == true) {

            /* draw first and last curve segments of previous 
            curve */
            if (Specs.nLnType == glb.LN_BSPLINE) {
                DrawFirstNLast(ct, aFirstPts, g, iStepSz, iPtsIdx);
            }
        }

        return (true);

    }


    /*----------------------------------------------------------------------
    Method:   DrawFirstNLast
    Purpose:  Draw the first and last BSpline curve segments in a total
    curve.
    
    Algorithm:	If the start and end of a curve meet up then we can
    use the points at the start and end to control the 
    curve segment.  
    
    -----------------------------------------------------------------------*/
    void DrawFirstNLast(CurveTurtle final_ct, Turtle aFirstPts[], Graphics g, int iStepSz, int iPtsIdx) {


        if ((Math.floor(final_ct.p4.x) == Math.floor(aFirstPts[0].x)) && (Math.floor(final_ct.p4.y) == Math.floor(aFirstPts[0].y))) {
            /*---------------
            start of curve joins with end of curve
            --------------*/
            /* Draw end segment of curve */
            final_ct.p1.getvals(final_ct.p2);
            final_ct.p2.getvals(final_ct.p3);
            final_ct.p3.getvals(final_ct.p4);
            final_ct.p4.getvals(aFirstPts[1]);
            DrawBSpline(final_ct, iStepSz, g);

            /* Draw start segment of curve */
            final_ct.p1 = final_ct.p2;
            final_ct.p2 = final_ct.p3;
            final_ct.p3 = final_ct.p4;
            final_ct.p4 = aFirstPts[2];
            DrawBSpline(final_ct, iStepSz, g);


        } else {


            /*--------------
            start of curve is independent of end of curve
            -------------*/
            /* Draw end segment of curve, depending on
            last point as last TWO control points */
            final_ct.p1.getvals(final_ct.p2);
            final_ct.p2.getvals(final_ct.p3);
            final_ct.p3.getvals(final_ct.p4);
            final_ct.p4 = final_ct.p4;

            DrawBSpline(final_ct, iStepSz, g);

            /* only draw first curve segment if more than one
            forward movement ('F') was encountered */
            if (iPtsIdx > 2) {

                /* Draw start segment of curve, depending on
                first point as first TWO control points */
                final_ct.p1 = aFirstPts[0];
                final_ct.p2 = aFirstPts[0];
                final_ct.p3 = aFirstPts[1];
                final_ct.p4 = aFirstPts[2];
                DrawBSpline(final_ct, iStepSz, g);
            }


        }


    }

    /*----------------------------------------------------------------------
    Method:   UpdateBox
    
    Purpose:  Extend the current dimensions of the box if the position
    sent specifies an area outside of the box
    ----------------------------------------------------------------------*/
    void UpdateBox(Turtle boxpos) {

        boxdim.xmin = Math.min(boxpos.x, boxdim.xmin);
        boxdim.xmax = Math.max(boxpos.x, boxdim.xmax);
        boxdim.ymin = Math.min(boxpos.y, boxdim.ymin);
        boxdim.ymax = Math.max(boxpos.y, boxdim.ymax);
    }


    /*---------------------------------------------------------------------
    Method:   DrawHermite
    
    Purpose:  Draw a Hermite curve between two points, given the
    start point, end point, start tangent and end tangent
    
    Param:    ct      --  curved turtle specifying start and end points
    and tangent vectors
    p1 - start tangent vector
    p2 - start point
    p3 - end point
    p4 - end tangent vector
    
    numParts -- number of partitions for discretizing curve	
    
    ----------------------------------------------------------------------*/
    void DrawHermite(CurveTurtle ct, int numParts, Graphics g) {

        int i;
        double t;
        double t_inc;
        Turtle Qcur, Qprev;   /* positions on curve as drawn */


        t_inc = (double) (1 / (((double) numParts - 1)));

        Qcur = new Turtle();
        Qprev = new Turtle();

        Qprev.getvals(ct.p2);


        for (i = 1, t = t_inc; i < numParts - 1; i++, t += t_inc) {


            Qcur.x = ((2 * Math.pow(t, 3) - 3 * Math.pow(t, 2) + 1) * ct.p2.x)
                    + ((-2 * Math.pow(t, 3) + 3 * Math.pow(t, 2)) * ct.p3.x)
                    + ((Math.pow(t, 3) - 2 * Math.pow(t, 2) + t) * ct.p1.x)
                    + ((Math.pow(t, 3) - Math.pow(t, 2)) * ct.p4.x);

            Qcur.y = ((2 * Math.pow(t, 3) - 3 * Math.pow(t, 2) + 1) * ct.p2.y)
                    + ((-2 * Math.pow(t, 3) + 3 * Math.pow(t, 2)) * ct.p3.y)
                    + ((Math.pow(t, 3) - 2 * Math.pow(t, 2) + t) * ct.p1.y)
                    + ((Math.pow(t, 3) - Math.pow(t, 2)) * ct.p4.y);


            g.drawLine((int) Qprev.x, (int) Qprev.y, (int) Qcur.x, (int) Qcur.y);
            Qprev.getvals(Qcur);



        }

        drawWideLine(Qprev, ct.p3, g);


    }

    /*---------------------------------------------------------------------
    Method:   DrawBSpline
    
    Purpose:  Draw a BSpline curve based on 4 given control points
    
    
    Param:    ct      --  curved turtle specifying control points:
    ct.p1 = point 1
    ct.p2 = point 2
    ct.p3 = point 3
    ct.p4 = point 4
    
    numParts -- number of partitions for discretizing curve	
    
    ----------------------------------------------------------------------*/
    void DrawBSpline(CurveTurtle ct, int numParts, Graphics g) {

        int i;
        double t;
        double t_inc;
        Turtle Qcur, Qprev;   /* positions on curve as drawn */


        Turtle p0 = new Turtle();
        Turtle p1 = new Turtle();
        Turtle p2 = new Turtle();
        Turtle p3 = new Turtle();

        p0.getvals(ct.p1);
        p1.getvals(ct.p2);
        p2.getvals(ct.p3);
        p3.getvals(ct.p4);


        t_inc = (double) (1 / (((double) numParts - 1)));
        Qcur = new Turtle();
        Qprev = new Turtle();


        t = 0;
        Qprev.x = ((Math.pow(1 - t, 3)) / 6 * p0.x)
                + ((3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 4) / 6 * p1.x)
                + ((-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 3 * t + 1) / 6 * p2.x)
                + ((Math.pow(t, 3)) / 6 * p3.x);

        Qprev.y = ((Math.pow(1 - t, 3)) / 6 * p0.y)
                + ((3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 4) / 6 * p1.y)
                + ((-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 3 * t + 1) / 6 * p2.y)
                + ((Math.pow(t, 3)) / 6 * p3.y);


        /*----------------------------------------------------
        draw the curve, with a total of "numParts" different
        segments drawn in the curve
        --------------------------------------------------*/
        for (i = 1, t = t_inc; i < numParts; i++, t += t_inc) {

            Qcur.x = ((Math.pow(1 - t, 3)) / 6 * p0.x)
                    + ((3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 4) / 6 * p1.x)
                    + ((-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 3 * t + 1) / 6 * p2.x)
                    + ((Math.pow(t, 3)) / 6 * p3.x);

            Qcur.y = ((Math.pow(1 - t, 3)) / 6 * p0.y)
                    + ((3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 4) / 6 * p1.y)
                    + ((-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 3 * t + 1) / 6 * p2.y)
                    + ((Math.pow(t, 3)) / 6 * p3.y);



            drawWideLine(Qprev, Qcur, g);


            Qprev.getvals(Qcur);



        }



    }
}
