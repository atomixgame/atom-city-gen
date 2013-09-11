/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;

/*==============================================================================
class ParamPanel

Purpose:   This is the panel containing all of the parameters, and allows
them to be adjusted through control widgets contained within it
================================================================================*/
public class ParamPanel extends Panel {

    public Button b_add, b_del, b_updt, b_apply, b_new;
    public Checkbox cb_strt, cb_hermite, cb_bspline;   /* choices for line shape */

    public CheckboxGroup cbg_Lines;         /* checkbox group of line types */

    public List ch_ptrnspecs;		/* choices for pattern specifications */

    public List lst_rules;
    public TextField tb_axiom, tb_depth, tb_scale, tb_angle,
            tb_rule, tb_ignore, tb_clr;
    public int curIdx;	/* index of item in list currently being edited */

    public boolean b_doApply; 	/* flag whether Apply button has been pressed */

    public boolean b_gotImagePnl = false;	/* flag whether have been given 
    associated drawing panel */

    public LSysPanel pnl_image;
    public Color aColors[];     /* array of colors */

    public Globals glb;         /* globals */


    /* constructor */
    public ParamPanel() {
        Insets lft_spc = new Insets(0, 5, 0, 0);

        /*----------------------------------------------
        Constructor:  ParamPanel
        
        
        Purpose:      Sets up the control panel containing the
        user specified parameters for performing
        the LSystems image
        
        ---------------------------------------------*/

        initColors();

        b_doApply = false;
        b_gotImagePnl = false;


        Panel lPanel = new Panel();
        Panel rPanel = new Panel();
        Panel mPanel = new Panel();
        ClrPanel bPanel = new ClrPanel(aColors);


        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.fill = GridBagConstraints.HORIZONTAL;	/* fill horizontally*/
        c.weightx = 1;  			        /* each of the 3 panels
        get same horiz space */
        c.insets = new Insets(0, 0, 0, 10); 	/* pad left and right */

        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTH;

        gridbag.setConstraints(lPanel, c);
        gridbag.setConstraints(rPanel, c);

        gridbag.setConstraints(mPanel, c);

        add(lPanel);			/* add panels left to right */
        add(mPanel);
        add(rPanel);



        /*------------
        color panel
        ------------*/



        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(15, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(bPanel, c);
        add(bPanel);

        gridbag = new GridBagLayout();
        bPanel.setLayout(gridbag);


        GridBagConstraints c_num = new GridBagConstraints();

        c_num.gridwidth = 1;    /* first in row */
        c_num.fill = GridBagConstraints.HORIZONTAL;
        c_num.weightx = 1;


        c_num.insets = new Insets(5, 20, 5, 0);
        Label label8 = new Label("0");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        c_num.insets = new Insets(5, 59, 5, 0);
        label8 = new Label("5");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        label8 = new Label("10");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        label8 = new Label("15");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        label8 = new Label("20");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        label8 = new Label("25");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);


        label8 = new Label("30");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        label8 = new Label("35");
        gridbag.setConstraints(label8, c_num);
        bPanel.add(label8);

        c_num.insets = new Insets(5, 10, 5, 5);
        tb_clr = new TextField(3);
        gridbag.setConstraints(tb_clr, c_num);
        bPanel.add(tb_clr);



        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        lPanel.setLayout(gridbag);

        c.fill = GridBagConstraints.NONE;  /* stretch components left to right */



        c.fill = GridBagConstraints.HORIZONTAL;  /* stretch components left to right */

        /*----------------------
        LSystems Samples
        ---------------------*/
        GridBagConstraints c_smpl = new GridBagConstraints();
        c_smpl.gridx = 0;
        c_smpl.gridy = 0;
        c_smpl.gridwidth = GridBagConstraints.REMAINDER;


        /* Rules Label */
        c_smpl.anchor = GridBagConstraints.WEST;
        Label label7 = new Label("Sample LSystems:");
        gridbag.setConstraints(label7, c_smpl);
        lPanel.add(label7);


        GridBagConstraints c_btn = new GridBagConstraints();
        c_btn.gridwidth = 1;
        c_btn.fill = GridBagConstraints.HORIZONTAL;
        c_btn.weightx = 1;

        c_btn.gridx = 0;
        c_btn.gridy = 1;
        TextField tb_placeholder = new TextField(25);
        gridbag.setConstraints(tb_placeholder, c_btn);
        tb_placeholder.hide();
        lPanel.add(tb_placeholder);
        tb_placeholder.show();
        tb_placeholder.hide();

        /* List of Samples LSystem Patterns */
        c_smpl.fill = GridBagConstraints.HORIZONTAL;
        c_smpl.anchor = GridBagConstraints.WEST;
        c_smpl.gridy = 1;
        ch_ptrnspecs = new List(6, false);
        ch_ptrnspecs.clear();
        gridbag.setConstraints(ch_ptrnspecs, c_smpl);
        lPanel.add(ch_ptrnspecs);
        fillSpecChoices(ch_ptrnspecs);



        c_btn = new GridBagConstraints();
        c_btn.gridwidth = 1;
        c_btn.fill = GridBagConstraints.HORIZONTAL;
        c_btn.weightx = 1;
        c_btn.insets = new Insets(30, 0, 0, 0);


        /* Apply button */
        b_apply = new Button("Apply");
        gridbag.setConstraints(b_apply, c_btn);
        lPanel.add(b_apply);






        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        mPanel.setLayout(gridbag);
        c.fill = GridBagConstraints.NONE;  /* stretch components left to right */

        /*----------------------
        Rule Controls
        ---------------------*/
        GridBagConstraints c_rule = new GridBagConstraints();
        c_rule.gridx = 0;
        c_rule.insets = lft_spc;
        c_rule.gridwidth = GridBagConstraints.REMAINDER;

        /* Rules Label */
        c_rule.anchor = GridBagConstraints.WEST;
        Label label5 = new Label("Rules:");
        gridbag.setConstraints(label5, c_rule);
        mPanel.add(label5);


        /* List of Rules */
        c_rule.fill = GridBagConstraints.HORIZONTAL;
        c_rule.anchor = GridBagConstraints.WEST;
        lst_rules = new List(4, false);
        gridbag.setConstraints(lst_rules, c_rule);
        mPanel.add(lst_rules);
        lst_rules.clear();

        /* rule edit box */
        c_rule.insets = new Insets(35, 5, 0, 0);
        c_rule.anchor = GridBagConstraints.WEST;
        c_rule.fill = GridBagConstraints.NONE;
        tb_rule = new TextField(27);
        gridbag.setConstraints(tb_rule, c_rule);
        mPanel.add(tb_rule);





        /*------------------
        Rule Editing Buttons
        (Add, Update, Delete, New)
        ------------------*/

        c_btn = new GridBagConstraints();
        c_btn.insets = lft_spc;
        c_btn.gridwidth = 1;
        c_btn.fill = GridBagConstraints.HORIZONTAL;
        c_btn.weightx = 1;

        /* rule editing buttons */
        b_add = new Button("Add");
        b_updt = new Button("Update");
        b_del = new Button("Delete");
        b_new = new Button("Clear");


        gridbag.setConstraints(b_add, c_btn);

        gridbag.setConstraints(b_updt, c_btn);

        gridbag.setConstraints(b_del, c_btn);

        c_btn.gridwidth = GridBagConstraints.REMAINDER;  /* last-in-row */
        gridbag.setConstraints(b_new, c_btn);


        mPanel.add(b_add);
        mPanel.add(b_updt);
        mPanel.add(b_del);
        mPanel.add(b_new);


        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        rPanel.setLayout(gridbag);

        c.fill = GridBagConstraints.NONE;  /* don't stretch components */


        cbg_Lines = new CheckboxGroup();


        /*-------------------
        Line Checkboxes
        ------------------*/
        GridBagConstraints c_line = new GridBagConstraints();
        c_line.gridx = 0;
        c_line.anchor = GridBagConstraints.WEST;
        c_line.gridwidth = GridBagConstraints.REMAINDER; /* last-in-row */



        /* Straight Line Checkbox*/

        c_line.insets = new Insets(20, 50, 0, 0);
        cb_strt = new Checkbox("Straight Lines", cbg_Lines, true);
        gridbag.setConstraints(cb_strt, c_line);
        rPanel.add(cb_strt);


        /* Hermite Curve Checkbox*/

        c_line.insets = new Insets(0, 50, 0, 0);
        cb_hermite = new Checkbox("Hermite Curved Lines", cbg_Lines, false);
        gridbag.setConstraints(cb_hermite, c_line);
        rPanel.add(cb_hermite);


        /* BSpline Curve Checkbox*/
        cb_bspline = new Checkbox("B-Spline Curved Lines", cbg_Lines, false);
        c_line.insets = new Insets(0, 50, 12, 0);
        gridbag.setConstraints(cb_bspline, c_line);
        rPanel.add(cb_bspline);




        /*-------------------
        Parameter Text Boxes 
        
        Axiom, Ignore, Depth, Angle, Scale
        ------------------*/

        GridBagConstraints c_textb = new GridBagConstraints();

        /* Axiom */
        c_textb.gridwidth = 1;    /* first in row */
        c_textb.anchor = GridBagConstraints.WEST;
        Label label1 = new Label("Axiom: ");
        gridbag.setConstraints(label1, c_textb);
        rPanel.add(label1);


        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        c_textb.gridwidth = GridBagConstraints.REMAINDER;/* last-in-row */
        tb_axiom = new TextField(26);
        gridbag.setConstraints(tb_axiom, c_textb);
        rPanel.add(tb_axiom);


        /* Ignore */
        c_textb.gridwidth = 1;    /* first in row */
        c_textb.anchor = GridBagConstraints.WEST;
        Label label6 = new Label("Ignore: ");
        gridbag.setConstraints(label6, c_textb);
        rPanel.add(label6);

        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        c_textb.gridwidth = GridBagConstraints.REMAINDER;/* last-in-row */
        tb_ignore = new TextField(26);
        gridbag.setConstraints(tb_ignore, c_textb);
        rPanel.add(tb_ignore);


        /* Depth */
        c_textb.gridwidth = 1;         /* first in row */
        c_textb.anchor = GridBagConstraints.WEST;
        Label label2 = new Label("Depth:");
        gridbag.setConstraints(label2, c_textb);
        rPanel.add(label2);


        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        tb_depth = new TextField(1);
        gridbag.setConstraints(tb_depth, c_textb);
        rPanel.add(tb_depth);


        /* Angle */
        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        Label label3 = new Label("Angle:");
        gridbag.setConstraints(label3, c_textb);
        rPanel.add(label3);


        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        tb_angle = new TextField(3);
        gridbag.setConstraints(tb_angle, c_textb);
        rPanel.add(tb_angle);


        /* Scale */
        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        Label label4 = new Label("Scale:");
        gridbag.setConstraints(label4, c_textb);
        rPanel.add(label4);


        c_textb.anchor = GridBagConstraints.WEST;  /* reset */
        tb_scale = new TextField(3);
        gridbag.setConstraints(tb_scale, c_textb);
        rPanel.add(tb_scale);



        lPanel.show();
        rPanel.show();
        mPanel.show();
        bPanel.show();

        tb_placeholder.hide();


        ch_ptrnspecs.select(0);
        changeSpecs(ch_ptrnspecs.getSelectedIndex());


    }

    public void setImagePanel(LSysPanel pnl_lsys) {
        pnl_image = pnl_lsys;
        b_gotImagePnl = true;
    }


    /*-----------------------------------------------------------
    Method:  handleEvent
    Purpose: handle button presses and other user interaction 
    performed in the control panel
    ---------------------------------------------------------*/
    @Override
    public boolean handleEvent(Event e) {

        if (e.target instanceof Button) {
            if (e.target == b_add) {
                lst_rules.add(tb_rule.getText());
                tb_rule.setText("");
            } else if (e.target == b_del) {
                lst_rules.delItem(curIdx);
                tb_rule.setText("");
            } else if (e.target == b_updt) {
                lst_rules.replaceItem(tb_rule.getText(), curIdx);
                tb_rule.setText("");
            } else if (e.target == b_apply) {
                b_doApply = true;
                /* apply the most recent control panel settings
                to redraw the image in the related image 
                panel */
                if (b_gotImagePnl == true) {
                    pnl_image.repaint();
                }

            } else if (e.target == b_new) {
                /* clear fields for new LSystems specification */
                lst_rules.clear();
                tb_rule.setText("");
                tb_axiom.setText("");
                tb_depth.setText("");
                tb_scale.setText("");
                tb_angle.setText("");
                tb_ignore.setText("");
                tb_clr.setText("");
                b_doApply = false;
                /* clear the image panel */
                if (b_gotImagePnl == true) {
                    pnl_image.repaint();
                }


            }
            return (true);
        } else if (e.target instanceof List) {
            if (e.target == lst_rules) {
                tb_rule.setText(lst_rules.getSelectedItem());
                curIdx = lst_rules.getSelectedIndex();
                return (true);
            } else if (e.target == ch_ptrnspecs) {
                changeSpecs(ch_ptrnspecs.getSelectedIndex());

            }
        }


        return (false);  /* return false for unprocessed events */


    }

    public void paint(Graphics g) {
    }


    /*======================================================================
    Method:  fillSpecChoices
    Purpose: fills in the LSystem Sample list box with the names of some
    predefined LSystems pattern choices
    
    
    Params:  ChoiceMenu -- the listbox object to fill
    ======================================================================*/
    public void fillSpecChoices(List ChoiceMenu) {

        ChoiceMenu.add("Quadratic Koch Island 1 pg. 13"); /* pg. 13 */
        ChoiceMenu.add("Quadratic Koch Island 2 pg. 14"); /* pg. 14 */
        ChoiceMenu.add("Island & Lake Combo. pg. 15");    /* pg.15 */
        ChoiceMenu.add("Koch Curve A pg. 16");
        ChoiceMenu.add("Koch Curve B pg. 16");
        ChoiceMenu.add("Koch Curve C pg. 16");
        ChoiceMenu.add("Koch Curve D pg. 16");
        ChoiceMenu.add("Koch Curve E pg. 16");
        ChoiceMenu.add("Koch Curve F pg. 16");
        ChoiceMenu.add("Mod of Snowflake pg. 14");
        ChoiceMenu.add("Dragon Curve pg. 17");
        ChoiceMenu.add("Hexagonal Gosper Curve pg. 19");
        ChoiceMenu.add("Sierpinski Arrowhead pg. 19");
        ChoiceMenu.add("Peano Curve pg. 18");
        ChoiceMenu.add("Hilbert Curve pg. 18");
        ChoiceMenu.add("Approx of Sierpinski pg. 18");
        ChoiceMenu.add("Tree A pg. 25");
        ChoiceMenu.add("Tree B pg. 25");
        ChoiceMenu.add("Tree C pg. 25");
        ChoiceMenu.add("Tree D pg. 25");
        ChoiceMenu.add("Tree E pg. 25");
        ChoiceMenu.add("Tree B pg. 43");
        ChoiceMenu.add("Tree C pg. 43");
        ChoiceMenu.add("Spiral Tiling pg. 70");
        ChoiceMenu.add("BSpline Triangle pg. 20");
        ChoiceMenu.add("Snake Kolam pg. 72");
        ChoiceMenu.add("Anklets of Krishna pg. 73");

        /*-----------
        Color examples 
        -----------*/
        ChoiceMenu.add("Color1, Koch Curve B");
        ChoiceMenu.add("Color2, Koch Curve B");
        ChoiceMenu.add("Color X, Spiral Tiling");
        ChoiceMenu.add("Color Center, Spiral Tiling");
        ChoiceMenu.add("Color Spokes, Spiral Tiling");
        ChoiceMenu.add("Color, Quad Koch Island 1");
        ChoiceMenu.add("Color, Tree E");
        ChoiceMenu.add("Color, Mod of Snowflake");
        ChoiceMenu.add("Color, Anklets of Krishna");
        ChoiceMenu.add("Color, Snake Kolam");

        ChoiceMenu.add("Simple Branch");


    }

    /*=====================================================================
    Method:   initColors
    Purpose:  Initialize Array of Colors
    
    =====================================================================*/
    public void initColors() {

        int i;
        float j;

        int start;
        int numShades = 5;
        float shadeInc = 1 / (float) numShades;


        aColors = new Color[glb.MAXCOLORS];  /* set array size */


        /* White to Black */
        start = 0;
        for (i = start, j = 1; i < start + 6; j -= shadeInc, i++) {
            aColors[i] = new Color(j, j, j);
        }

        start = 6;
        /* Red to almost Magenta */
        for (i = start, j = 0; i < start + 5; j += shadeInc, i++) {
            aColors[i] = new Color((float) 1, (float) 0, j);
        }


        /* Magenta to almost Blue */
        start += 5;
        for (i = start, j = 1; i < start + 5; j -= shadeInc, i++) {
            aColors[i] = new Color(j, (float) 0, (float) 1);
        }


        /* Blue to almost Cyan */
        start += 5;
        for (i = start, j = 0; i < start + 5; j += shadeInc, i++) {
            aColors[i] = new Color((float) 0, j, (float) 1);
        }

        /* Cyan to almost Green */
        start += 5;
        for (i = start, j = 1; i < start + 5; j -= shadeInc, i++) {
            aColors[i] = new Color((float) 0, (float) 1, j);
        }



        /* Green to almost Yellow */
        start += 5;
        for (i = start, j = 0; i < start + 5; j += shadeInc, i++) {
            aColors[i] = new Color(j, (float) 1, (float) 0);
        }

        /* Yellow to almost Red */
        start += 5;
        for (i = start, j = 1; i < start + 5; j -= shadeInc, i++) {
            aColors[i] = new Color((float) 1, j, (float) 0);
        }

    }


    /*======================================================================
    Method:   changeSpecs
    Purpose:  based on the current choice specification, alter the values
    of the widgets in the control panel
    ======================================================================*/
    public void changeSpecs(int Idx) {

        tb_rule.setText("");
        b_doApply = false;
        /* clear the image panel */
        if (b_gotImagePnl == true) {
            pnl_image.repaint();
        }


        switch (Idx) {
            case 0:
                /* quadratic Koch Island 1 pg. 13 */
                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("2");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * -->  F+F-F-FF+F+F-F");

                break;


            case 1:   /* Quadratic Koch Island 2 pg. 14 */

                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("2");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F-FF+FF+F+F-F-FF+F+F-F-FF-FF+F");
                break;

            case 2:   /* Island & Lake Combo. pg. 15 */


                tb_clr.setText("16");
                tb_axiom.setText("F-F-F-F");
                tb_ignore.setText("Ff+-");
                tb_depth.setText("2");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F-f+FF-F-FF-Ff-FF+f-FF+F+FF+Ff+FFF");
                lst_rules.add("* <f> * --> ffffff");
                break;

            case 3:	  /* Koch Curve A pg. 16 */


                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> FF+F+F+F+F+F-F");
                break;

            case 4:	  /* Koch Curve B pg. 16 */

                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> FF+F+F+F+FF");


                break;

            case 5:	  /* Koch Curve C pg. 16 */


                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("3");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> FF+F-F+F+FF");
                break;


            case 6:   /* Koch Curve D pg. 16 */


                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> FF+F++F+F");
                break;

            case 7:	  /* Koch Curve E pg. 16 */


                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+");
                tb_depth.setText("5");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F+FF++F+F");
                break;

            case 8:   /* Koch Curve F pg. 16 */


                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F+F-F+F+F");
                break;

            case 9:  /* Mod of snowflake pg. 14*/


                tb_clr.setText("16");
                tb_axiom.setText("+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F-F+F+F-F");

                break;

            case 10:  /* Dragon Curve pg. 17 */


                tb_clr.setText("16");
                tb_axiom.setText("Fl");
                tb_ignore.setText("F+-");
                tb_depth.setText("14");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <l> * --> l+rF+");
                lst_rules.add("* <r> * --> -Fl-r");
                break;

            case 11:  /* Hexagonal Gosper Curve pg. 19 */


                tb_clr.setText("16");
                tb_axiom.setText("XF");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("90");
                tb_angle.setText("6");
                lst_rules.removeAll();
                lst_rules.add("* <XF> * --> XF+YF++YF-XF--XFXF-YF+");
                lst_rules.add("* <YF> * --> -XF+YFYF++YF+XF--XF-YF");
                break;

            case 12:  /* Sierpinski Arrowhead pg. 19 */


                tb_clr.setText("16");
                tb_axiom.setText("YF");
                tb_ignore.setText("F+-");
                tb_depth.setText("6");
                tb_scale.setText("100");
                tb_angle.setText("6");
                lst_rules.removeAll();
                lst_rules.add("* <XF> * --> YF+XF+YF");
                lst_rules.add("* <YF> * --> XF-YF-XF");
                break;

            case 13:  /* Peano Curve pg. 18 */


                tb_clr.setText("16");
                tb_axiom.setText("X");
                tb_ignore.setText("F+-");
                tb_depth.setText("3");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> XFYFX+F+YFXFY-F-XFYFX");
                lst_rules.add("* <Y> * --> YFXFY-F-XFYFX+F+YFXFY");
                break;

            case 14:  /* Hilbert Curve pg. 18 */


                tb_clr.setText("16");
                tb_axiom.setText("X");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> -YF+XFX+FY-");
                lst_rules.add("* <Y> * --> +XF-YFY-FX+");
                break;

            case 15:  /* Approx of Sierpinski pg. 18 */

                tb_clr.setText("16");
                tb_axiom.setText("F+XF+F+XF");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> XF-F+F-XF+F+XF-F+F-X");
                break;

            case 16:  /* Tree A pg. 25 */


                tb_clr.setText("16");
                tb_axiom.setText("F");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("14");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F[+F]F[-F]F");
                break;

            case 17:  /* Tree B pg. 25 */


                tb_clr.setText("16");
                tb_axiom.setText("X");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("16");
                lst_rules.removeAll();
                lst_rules.add(" * <X> * --> F-[[X]+X]+F[+FX]-X");
                lst_rules.add(" * <F> * --> FF");
                break;


            case 18:  /* Tree C pg. 25 */


                tb_clr.setText("16");
                tb_axiom.setText("Y");
                tb_ignore.setText("F+-");
                tb_depth.setText("6");
                tb_scale.setText("100");
                tb_angle.setText("14");
                lst_rules.removeAll();
                lst_rules.add(" * <Y> * --> YFX[+Y][-Y]");
                lst_rules.add(" * <X> * --> X[-FFF][+FFF]FX");
                break;

            case 19:  /* Tree D pg. 25 */


                tb_clr.setText("16");
                tb_axiom.setText("F");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("16");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> FF+[+F-F-F]-[-F+F+F]");
                break;

            case 20:  /* Tree E pg. 25 */


                tb_clr.setText("16");
                tb_axiom.setText("X");
                tb_ignore.setText("F+-");
                tb_depth.setText("7");
                tb_scale.setText("100");
                tb_angle.setText("18");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> F[+X]F[-X]+X");
                lst_rules.add("* <F> * --> FF");

                break;

            case 21:  /* Tree B pg. 43 */
                tb_clr.setText("16");
                tb_axiom.setText("F1F1F1");
                tb_ignore.setText("F+-");
                tb_depth.setText("30");
                tb_scale.setText("100");
                tb_angle.setText("16");
                lst_rules.clear();
                lst_rules.add("0 <0> 0 --> 1");
                lst_rules.add("0 <0> 1 --> 1[-F1F1]");
                lst_rules.add("0 <1> 0 --> 1");
                lst_rules.add("0 <1> 1 --> 1");
                lst_rules.add("1 <0> 0 --> 0");
                lst_rules.add("1 <0> 1 --> 1F1");
                lst_rules.add("1 <1> 0 --> 1");
                lst_rules.add("1 <1> 1 --> 0");
                lst_rules.add("* <-> * --> +");
                lst_rules.add("* <+> * --> -");
                break;


            case 22:  /* Tree C pg. 43 */
                tb_clr.setText("16");
                tb_axiom.setText("F1F1F1");
                tb_ignore.setText("F+-");
                tb_depth.setText("26");
                tb_scale.setText("100");
                tb_angle.setText("14");
                lst_rules.clear();
                lst_rules.add("0 <0> 0  --> 0");
                lst_rules.add("0 <0> 1 --> 1");
                lst_rules.add("0 <1> 0 --> 0");
                lst_rules.add("0 <1> 1 --> 1[+F1F1]");
                lst_rules.add("1 <0> 0 --> 0");
                lst_rules.add("1 <0> 1 --> 1F1");
                lst_rules.add("1 <1> 0 --> 0");
                lst_rules.add("1 <1> 1--> 0");
                lst_rules.add("* <-> * --> +");
                lst_rules.add("* <+> * --> -");
                break;


            case 23:  /* Spiral Tiling pg. 70 */
                tb_clr.setText("16");
                tb_axiom.setText("AAAA");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("24");
                lst_rules.removeAll();
                lst_rules.add("* <A> * --> X+X+X+X+X+X+");
                lst_rules.add("* <X> * --> [F+F+F+F[---X-Y]+++++F++++++++F-F-F-F]");
                lst_rules.add("* <Y> * --> [F+F+F+F[---Y]+++++F++++++++F-F-F-F]");

                break;


            case 24:  /* BSpline Triangle pg. 20 */
                tb_clr.setText("16");
                tb_axiom.setText("F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("3");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> F-F+F");

                break;

            case 25:  /* Snake Kolam pg. 72 */
                tb_clr.setText("16");
                tb_axiom.setText("F+XF+F+XF");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> XF-F-F+XF+F+XF-F-F+X");

                break;


            case 26:  /* Anklets of Krishna pg. 73 */
                tb_clr.setText("16");
                tb_axiom.setText("-X--X");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("8");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> XFX--XFX");

                break;

            case 27: /* Color, Koch Curve B */

                tb_clr.setText("16");
                tb_axiom.setText("F+F+F+F");
                tb_ignore.setText("F+");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> FF+F+;;;;;F:::::+F+FF");

                break;

            case 28: /* Color, Koch Curve B */

                tb_clr.setText("16");
                tb_axiom.setText("###F+F+F+F");
                tb_ignore.setText("F+");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * -->;FF+F+F+F+FF");

                break;


            case 29: /* Color X, Spiral Tiling */
                tb_clr.setText("16");
                tb_axiom.setText("AAAA");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("24");
                lst_rules.removeAll();
                lst_rules.add("* <A> * --> ;;;;;;;;X::::::::+X+X+X+X+X+");
                lst_rules.add("* <X> * --> [F+F+F+F[---X-Y]+++++F++++++++F-F-F-F]");
                lst_rules.add("* <Y> * --> [F+F+F+F[---Y]+++++F++++++++F-F-F-F]");
                break;

            case 30: /* Color Center, Spiral Tiling */
                tb_clr.setText("16");
                tb_axiom.setText("AAAA");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("24");
                lst_rules.removeAll();
                lst_rules.add("* <A> * --> X+X+X+X+X+X+");
                lst_rules.add("* <X> * --> [;;;;;F+F+F+F:::::[---X-Y]+++++F++++++++F-F-F-F]");
                lst_rules.add("* <Y> * --> [F+F+F+F[---Y]+++++F++++++++F-F-F-F]");
                break;

            case 31: /* Color Spokes, Spiral Tiling */
                tb_clr.setText("16");
                tb_axiom.setText("AAAA");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("24");
                lst_rules.removeAll();
                lst_rules.add("* <A> * --> X+X+X+X+X+X+");
                lst_rules.add("* <X> * --> [F+F+F+F[---X-Y]+++++F++++++++;;;;F-F-F-F::::]");
                lst_rules.add("* <Y> * --> [F+F+F+F[---Y]+++++F++++++++F-F-F-F]");
                break;

            case 32: /* Color, Quad Koch Island 1 */
                /* quadratic Koch Island 1 pg. 13 */
                tb_clr.setText("16");
                tb_axiom.setText("###F+F+F+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("2");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * -->  ;F+F-F-FF+F+F-F");

                break;

            case 33: /* Color, Tree E */
                tb_clr.setText("16");
                tb_axiom.setText("X");
                tb_ignore.setText("F+-");
                tb_depth.setText("7");
                tb_scale.setText("100");
                tb_angle.setText("18");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> F[+X]F[-X]+X");
                lst_rules.add("* <F> * --> ;;FF::");

                break;


            case 34: /* Color, Mod of Snowflake */
                tb_clr.setText("16");
                tb_axiom.setText("###+F");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("100");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <F> * --> ;F-F+F+F-F");

                break;

            case 35: /* Color, Anklets of Krishna */
                tb_clr.setText("16");
                tb_axiom.setText("-X--X");
                tb_ignore.setText("F+-");
                tb_depth.setText("5");
                tb_scale.setText("100");
                tb_angle.setText("8");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> XFX--X;;;;;;F::::::X");
                break;

            case 36: /* Color, Snake Kolam */
                tb_clr.setText("16");
                tb_axiom.setText("F+XF+F+;;;XF:::");
                tb_ignore.setText("F+-");
                tb_depth.setText("4");
                tb_scale.setText("90");
                tb_angle.setText("4");
                lst_rules.removeAll();
                lst_rules.add("* <X> * --> XF-F-F+XF+F+XF-F-F+X");

                break;

            case 37: /* Simple Branch */
                tb_clr.setText("16");
                tb_axiom.setText("FFF[-FFF][--FFF][FFF][+FFF][++FFF]");
                tb_ignore.setText("F+-");
                tb_depth.setText("1");
                tb_scale.setText("90");
                tb_angle.setText("8");
                lst_rules.removeAll();

                break;




            default:
                tb_axiom.setText("");
                tb_ignore.setText("");
                tb_depth.setText("");
                tb_scale.setText("");
                tb_angle.setText("");
                lst_rules.removeAll();
                break;

        }

    }
}
