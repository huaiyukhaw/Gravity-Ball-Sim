package ballsim_v2;

import java.awt.Point;

public class ShapeUtils {

    public static boolean isPtInSquare(Point ptIn, Point sqUpperLeft, int sqWidth, int sqHeight) {
        if (ptIn.x >= sqUpperLeft.x && ptIn.x <= sqUpperLeft.x + sqWidth) { //if x is in the square range
            if (ptIn.y >= sqUpperLeft.y && ptIn.y <= sqUpperLeft.y + sqHeight) { //if y is in the square range
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPtInCircle(Point ptIn, Point cirUpperLeft, int cirWidth, int cirHeight){
        Point ptCenter = squareUpperLeftToCenterPt(cirUpperLeft, cirWidth, cirHeight);
        double radius = cirWidth / 2;
        if (distanceFormulua(ptIn, ptCenter) <= radius){
            return true;
        }
        return false;
    }
    
    public static boolean isPtInCircle(Point ptIn, Point cirUpperLeft, Point cirSize){ //overload for point as size
        return isPtInCircle(ptIn, cirUpperLeft, cirSize.x, cirSize.y);
    }
    
    private static double distanceFormulua (Point pt1, Point pt2){
        return Math.sqrt(Math.pow(pt2.x-pt1.x, 2) + Math.pow(pt2.y - pt1.y, 2));
    }

    public static Point squareUpperLeftToCenterPt(Point upperLeft, int width, int height) {
        return new Point(upperLeft.x + width / 2, upperLeft.y + height / 2);
    }

    public static Point squareCenterToUpperLeftPt(Point center, int width, int height) {
        return new Point(center.x - width / 2, center.y - height / 2);
    }
}
