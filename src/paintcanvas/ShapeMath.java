/*
 * Eric Yager
 */
package paintcanvas;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for calculating shape vertex locations.
 * @author ericyager
 */
public class ShapeMath {
    
    /**
     * Returns the coordinates of the vertices of a regular polygon given one
     * vertex and the center. Every other value in the ArrayList is the X value,
     * and vice versa for Y.
     *
     * @param x first vertex x
     * @param y first vertex y
     * @param centerX center x
     * @param centerY center y
     * @param sides number of sides
     * @return ArrayList of doubles containing the points
     */
    public static ArrayList<Double> getPolygonPoints(double x, double y, double centerX, double centerY, int sides) {

        ArrayList<Double> list = new ArrayList<>(); //list for points
        double theta; //angle in radians between the current point and the positive x axis
        if (x == centerX) {
            if (y > centerY) { //if mouse directly above center, angle is 90 degrees
                theta = Math.PI / 2;
            } else { //if mouse directly beneath center, angle is 90 degrees
                theta = 3 * (Math.PI / 2);
            }
        } else {
            theta = Math.atan((centerY - y) / (centerX - x)); //theta is the inverse tangent of y component of the radius and the x component
            if (x < centerX) { //if the user dragged left, adjust theta appropriately
                theta -= Math.PI;
            }
        }
        double radius = Math.sqrt((centerX - x) * (centerX - x) + (centerY - y) * (centerY - y));
        for (int i = 0; i < sides; i++) {
            list.add(radius * Math.cos(theta) + centerX);//add x point
            list.add(radius * Math.sin(theta) + centerY);//add y point
            theta += (2 * Math.PI) / sides; //increase theta
        }
        return list;

    }
    
    /**
     * Returns the coordinates of the vertices of a regular polygon given one
     * vertex and the center. The first array contains the x points, the
     * second array contains the y.
     *
     * @param x first vertex x
     * @param y first vertex y
     * @param centerX center x
     * @param centerY center y
     * @param sides number of sides
     * @return 2d double array of doubles containing the points
     */
    public static double[][] getPolygonPointsTwoArrays(double x, double y, double centerX, double centerY, int sides) {

        double[] xpoints = new double[sides*2];
        double[] ypoints = new double[sides*2];
        double theta; //angle in radians between the current point and the positive x axis
        if (x == centerX) {
            if (y > centerY) { //if mouse directly above center, angle is 90 degrees
                theta = Math.PI / 2;
            } else { //if mouse directly beneath center, angle is 90 degrees
                theta = 3 * (Math.PI / 2);
            }
        } else {
            theta = Math.atan((centerY - y) / (centerX - x)); //theta is the inverse tangent of y component of the radius and the x component
            if (x < centerX) { //if the user dragged left, adjust theta appropriately
                theta -= Math.PI;
            }
        }
        double radius = Math.sqrt((centerX - x) * (centerX - x) + (centerY - y) * (centerY - y));
        for (int i = 0; i < sides; i++) {
            xpoints[i] = (radius * Math.cos(theta) + centerX);//add x point
            ypoints[i] = (radius * Math.sin(theta) + centerY);//add y point
            theta += (2 * Math.PI) / sides; //increase theta
        }
        return new double[][] {xpoints, ypoints};

    }

    /**
     * Returns the coordinates of the vertices of a star given one outside
     * vertex and the center. Every other value in the ArrayList is the X value,
     * and vice versa for Y.
     *
     * @param x first vertex x
     * @param y first vertex y
     * @param centerX center x
     * @param centerY center y
     * @param points number of points on the star
     * @return ArrayList of doubles containing the points
     */
    public static ArrayList<Double> getStarPoints(double x, double y, double centerX, double centerY, int points) {

        ArrayList<Double> list = new ArrayList<>(); //list for points
        double theta; //angle in radians between the current point and the positive x axis
        if (x == centerX) {
            if (y > centerY) { //if mouse directly above center, angle is 90 degrees
                theta = Math.PI / 2;
            } else { //if mouse directly beneath center, angle is 90 degrees
                theta = 3 * (Math.PI / 2);
            }
        } else {
            theta = Math.atan((centerY - y) / (centerX - x)); //theta is the inverse tangent of y component of the radius and the x component
            if (x < centerX) { //if the user dragged left, adjust theta appropriately
                theta -= Math.PI;
            }
        }
        double radius = Math.sqrt((centerX - x) * (centerX - x) + (centerY - y) * (centerY - y));
        for (int i = 0; i < points; i++) {
            list.add(radius * Math.cos(theta) + centerX);//add convex x point
            list.add(radius * Math.sin(theta) + centerY);//add convex y point
            theta += (2 * Math.PI) / points / 2; //adjust theta
            list.add((radius / 2) * Math.cos(theta) + centerX); //add concave x point
            list.add((radius / 2) * Math.sin(theta) + centerY); //add concave y point
            theta += (2 * Math.PI) / points / 2; //adjust theta
        }
        return list;

    }
    
    /**
     * Returns the coordinates of the vertices of a star given one outside
     * vertex and the center. The first array contains the x points, the
     * second array contains the y.
     *
     * @param x first vertex x
     * @param y first vertex y
     * @param centerX center x
     * @param centerY center y
     * @param points number of points on the star
     * @return 2d double array of doubles containing the points
     */
    public static double[][] getStarPointsTwoArrays(double x, double y, double centerX, double centerY, int points) {

        double[] xpoints = new double[points*2];
        double[] ypoints = new double[points*2];
        double theta; //angle in radians between the current point and the positive x axis
        if (x == centerX) {
            if (y > centerY) { //if mouse directly above center, angle is 90 degrees
                theta = Math.PI / 2;
            } else { //if mouse directly beneath center, angle is 90 degrees
                theta = 3 * (Math.PI / 2);
            }
        } else {
            theta = Math.atan((centerY - y) / (centerX - x)); //theta is the inverse tangent of y component of the radius and the x component
            if (x < centerX) { //if the user dragged left, adjust theta appropriately
                theta -= Math.PI;
            }
        }
        double radius = Math.sqrt((centerX - x) * (centerX - x) + (centerY - y) * (centerY - y));
        for (int i = 0; i < points; i++) {
            xpoints[2*i] = (radius * Math.cos(theta) + centerX);//add convex x point
            ypoints[2*i] = (radius * Math.sin(theta) + centerY);//add convex y point
            theta += (2 * Math.PI) / points / 2; //adjust theta
            xpoints[2*i + 1] = ((radius / 2) * Math.cos(theta) + centerX); //add concave x point
            ypoints[2*i + 1] = ((radius / 2) * Math.sin(theta) + centerY); //add concave y point
            theta += (2 * Math.PI) / points / 2; //adjust theta
        }
        return new double[][] {xpoints, ypoints};

    }
    
}
