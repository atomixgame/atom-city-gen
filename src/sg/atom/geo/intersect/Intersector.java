/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.atom.geo.intersect;

import com.jme3.math.Vector2f;

/**
 * Intersector computes the intersection between two lines defined by four vectors given in parameters.
 * It works in two times : 
 *  - first, it find a result of the intersection and gives some boolean methods to check it,
 *  - then it computes the single intersection point or intersection zone if (and only if) it's requested.
 *  
 * Intersector makes no difference between lines and segments in its intersection calculation, so you must
 * check the booleans before asking for the intersection.
 *  
 * For example, if you gives two segments and ask for the single intersection point, it will return the
 * intersection point of the lines defined by the segments. you must check the intersectSegmentToSegment()
 * method to be sure.
 *  
 * Please note that the intersection calculation is computed with division and is not absolutely precise.
 *  
 * For information, the Angle.getTurn(a, b, c) method uses determinant to check the turning sense. 
 *  
 */
public class Intersector {

    public final static int PARALLEL = 1;
    public final static int INTERSECT = 2;
    public final static int COLLINEAR = 3;
    private Vector2f p0, p1, q0, q1;
    private Vector2f intersection, intersectionStart, intersectionEnd;
    private int result;
    private boolean inPLimits;
    private boolean inQLimits;

    /**
     * Constructs a new Intersector with four line's vectors.
     *  
     * @param p0 P segment's start 
     * @param p1 P segment's end
     * @param q0 Q segment's start
     * @param q1 Q segment's end
     */
    public Intersector(Vector2f p0, Vector2f p1, Vector2f q0, Vector2f q1) {
        intersection = null;
        intersectionStart = null;
        intersectionEnd = null;
        inPLimits = false;
        inQLimits = false;
        this.p0 = p0;
        this.p1 = p1;
        this.q0 = q0;
        this.q1 = q1;


        result = computeIntersectionResult();
    }

    public boolean hasLineToLineIntersection() {
        return result == INTERSECT || result == COLLINEAR;
    }

    public boolean hasUniqueLineToLineIntersection() {
        return result == INTERSECT;
    }

    public boolean hasSegmentToLineIntersection() {
        return result == INTERSECT && inPLimits
                || result == COLLINEAR && inPLimits;
    }

    public boolean hasUniqueSegmentToLineIntersection() {
        return result == INTERSECT && inPLimits;
    }

    public boolean hasSegmentToSegmentIntersection() {
        return result == INTERSECT && inPLimits && inQLimits
                || result == COLLINEAR && inPLimits && inQLimits;
    }

    public boolean hasUniqueSegmentToSegmentIntersection() {
        return result == INTERSECT && inPLimits && inQLimits;
    }

    public boolean isCollinear() {
        return result == COLLINEAR;
    }

    /**
     * This method returns an intersection point only if there is only one.
     * 
     * @return single intersection point,
     * or null if none or more than one exists.
     */
    public Vector2f getUniqueIntersection() {
        if (result == COLLINEAR || result == PARALLEL) {
            return null;
        }

        if (intersection == null) {
            computeIntersectionPoint();
        }
        return intersection;
    }

    /**
     * This method returns any intersection point found.
     * 
     * @return a single intersection point,
     * or the middle point of the intersection zone,
     * or null if none exists.
     */
    public Vector2f getAnyIntersection() {
        if (intersection == null) {
            computeIntersectionPoint();
        }
        return intersection;
    }

    /**
     * This method returns the start of the intersection zone.
     * If the lines are intersecting on a single point, it returns this point.
     * 
     * @return the end point of the intersection zone,
     * or the single intersection point,
     * or null if none exists.
     */
    public Vector2f getIntersectionZoneStart() {
        if (intersection == null) {
            computeIntersectionPoint();
        }
        if (intersectionStart == null) {
            return intersection;
        }
        return intersectionStart;
    }

    /**
     * This method returns the end of the intersection zone.
     * If the lines are intersecting on a single point, it returns this point.
     * 
     * @return the end point of the intersection zone,
     * or the single intersection point,
     * or null if none exists.
     */
    public Vector2f getIntersectionZoneEnd() {
        if (intersection == null) {
            computeIntersectionPoint();
        }
        if (intersectionEnd == null) {
            return intersection;
        }
        return intersectionEnd;
    }

    private int computeIntersectionResult() {
        // for each end point, find the side of the other line.
        // if two end points lie on opposite sides of the other line, then the lines are crossing.
        // if all end points lie on opposite sides, then the segments are crossing.

        float Pq0 = Angle.getTurn(p0, p1, q0);
        float Pq1 = Angle.getTurn(p0, p1, q1);

        float Qp0 = Angle.getTurn(q0, q1, p0);
        float Qp1 = Angle.getTurn(q0, q1, p1);

        // check if all turn have none angle. In this case, lines are collinear.
        if (Pq0 == Angle.NONE
                && Pq1 == Angle.NONE
                && Qp0 == Angle.NONE
                && Qp1 == Angle.NONE) {
            // at this point, we know that lines are collinear.
            // we must check if they overlap for segments intersection
            if (q0.distance(p0) <= p0.distance(p1) && q0.distance(p1) <= p0.distance(p1)) {
                // then q0 is in P limits and p0 or p1 is in Q limits
                inPLimits = true;
                inQLimits = true;
            }
            return COLLINEAR;
        } // check if q0 and q1 lie around P AND p0 and p1 lie around Q.
        // in this case, the two segments intersect
        else if (Pq0 * Pq1 <= 0 && Qp0 * Qp1 <= 0) {
            inPLimits = true;
            inQLimits = true;
            return INTERSECT;
        } // At this point, we know that segments are not crossing
        // check if q0 and q1 lie around P OR p0 and p1 lie around Q.
        // in this case, a segment cross a line
        else if (Pq0 * Pq1 <= 0) {
            inQLimits = true;
            return INTERSECT;
        } else if (Qp0 * Qp1 <= 0) {
            inPLimits = true;
            return INTERSECT;
        }


        // At this point, we know that each segment lie on one side of the other
        // We now check the slope to know if lines are parallel
        double pSlope = slope(p0, p1);
        double qSlope = slope(q0, q1);
        if (pSlope == qSlope) // this test works even if the slopes are "Double.infinity" due to the verticality of the lines and division by 0
        {
            return PARALLEL;
        } else {
            return INTERSECT;
        }
    }

    private void computeIntersectionPoint() {
        if (result == INTERSECT) {
            /*
             * Single point intersection
             * 
             * This calculation method needs divisions, which may cause approximation problems.
             * The intersection point, once rounded to float precision, may be out the line bounding.
             * If "on-the-line" intersection point is needed, you will have to use a more robust method.
             */
            intersection = new Vector2f();

            double pSlope = slope(p0, p1);
            double qSlope = slope(q0, q1);
            double pOrdinate = p0.getY() - pSlope * p0.getX();
            double qOrdinate = q0.getY() - qSlope * q0.getX();

            // At this point, we already know that pSlope != qSlope (checked in previously launched method)
            // So the divide by 0 case should never happen.
            // We must check if the lines are verticals (infinite slope)
            if (Double.isInfinite(pSlope)) {
                intersection.setX(p0.getX());
                intersection.setY((float) (qSlope * intersection.getX() + qOrdinate));
            } else if (Double.isInfinite(qSlope)) {
                intersection.setX(q0.getX());
                intersection.setY((float) (pSlope * intersection.getX() + pOrdinate));
            } else {
                intersection.setX((float) ((pOrdinate - qOrdinate) / (qSlope - pSlope)));
                intersection.setY((float) (pSlope * intersection.getX() + pOrdinate));
            }

        } else if (result == COLLINEAR) {
            /*
             * Collinear intersection zone
             * 
             * At this point, we have to find the two points that enclose the intersection zone.
             * We use distances to check if a segment's end is inside the other segment.
             * The single intersection point is set to the middle of the intersection zone. 
             * 
             * Note that if P and Q are not overlapping, then there is no intersection zone
             * and all intersection points are set to "null".
             * For segments, it means that there is no intersection.
             * For lines, it means that intersection zone is infinite. 
             */
            if (q0.distance(p0) <= p0.distance(p1) && q0.distance(p1) <= p0.distance(p1)) {
                // then q0 is in P
                intersectionStart = q0;
                if (q1.distance(p0) <= p0.distance(p1) && q1.distance(p1) <= p0.distance(p1)) {
                    // then q0 and q1 are both in P
                    System.out.println("(collinear) Q is in P");
                    intersectionEnd = q1;
                } else // then q0 is in P but q1 is out of P
                if (p0.distance(q0) <= q0.distance(q1) && q0.distance(p1) <= q0.distance(q1)) // then q0 is in P and p0 is in Q
                {
                    intersectionEnd = p0;
                } else {
                    intersectionEnd = p1;
                }
            } else // then q0 is out of p
            if (q1.distance(p0) <= p0.distance(p1) && q1.distance(p1) <= p0.distance(p1)) {
                // then q0 is out of P and q1 is in P
                intersectionStart = q1;
                if (p0.distance(q0) <= q0.distance(q1) && q0.distance(p1) <= q0.distance(q1)) // then q1 is in P and p0 is in Q
                {
                    intersectionEnd = p0;
                } else {
                    intersectionEnd = p1;
                }
            } else { // then q0 and q1 are both out of P
                if (p0.distance(q0) <= q0.distance(q1) && q0.distance(p1) <= q0.distance(q1)) {
                    // then P is in Q
                    System.out.println("(collinear) P is in Q");
                    intersectionStart = p0;
                    intersectionEnd = p1;
                } else {
                    System.out.println("(collinear) Q and P are not touching ");
                    intersectionStart = null;
                    intersectionEnd = null;
                }
            }

            intersection = intersectionEnd.subtract(intersectionStart).divide(2);
        }
    }

    static class Angle {

        public final static int COUNTERCLOCKWISE = 1;
        public final static int CLOCKWISE = 2;
        public final static int NONE = 3;

        /**
         * Returns the determinant between the two edges.
         *
         * @return negative value equals to turning clockwise,
         * positive value equals to turning counter clockwise,
         * zero value equals to collinear.
         */
        public static int getTurn(Vector2f A, Vector2f B, Vector2f p) {
            float turn = B.subtract(A).determinant(p.subtract(A));

            if (turn > 0) {
                return COUNTERCLOCKWISE;
            } else if (turn < 0) {
                return CLOCKWISE;
            } else {
                return NONE;
            }
        }
    }

    public float slope(Vector2f A, Vector2f B) {
        // add exception throwing
        if (B.getX() - A.getX() == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (B.getY() - A.getY()) / (B.getX() - A.getX());
    }
}