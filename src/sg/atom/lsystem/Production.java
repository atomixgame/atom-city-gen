/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.lsystem;

/*==============================================================================
Class Production

Contents:
contains the the four portions of a single production rule:
left context
predecessor
right context
successor

where a rule is written:

left context <predecessor> right context --> successor

and in essence the predecessor translates into the successor, and
the left and right context provide further information to choose
the proper production rule based on what appears before and after
the predecessor

==============================================================================*/
public class Production extends Object {

    public String lCtxt;      /* left context string */

    public int lCtxtLen;            /* left context string length */

    public String pred;       /* predecessor string */

    public int predLen;             /* predecessor string length */

    public String rCtxt;      /* right context string */

    public int rCtxtLen;            /* right context string length */

    public String succ;       /* successor string */

    public int succLen;             /* successor string length */

}
