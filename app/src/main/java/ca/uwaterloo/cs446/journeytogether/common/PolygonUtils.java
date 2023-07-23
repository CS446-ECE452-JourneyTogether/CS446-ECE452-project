package ca.uwaterloo.cs446.journeytogether.common;

import android.graphics.PointF;

public class PolygonUtils {

        public static boolean isInside(double testx, double testy, double[] polyX, double[] polyY) {
            int i;
            int j;
            boolean result = false;
            for (i = 0, j = polyX.length - 1; i < polyX.length; j = i++) {
                if ((polyY[i] > testy) != (polyY[j] > testy) &&
                        (testx < (polyX[j] - polyX[i]) * (testy - polyY[i]) / (polyY[j] - polyY[i]) + polyX[i])) {
                    result = !result;
                }
            }
            return result;
        }

        public static boolean isPointInPolygon(PointF point, PointF[] polygon) {
            double[] polyx = new double[polygon.length];
            double[] polyy = new double[polygon.length];

            for (int i = 0; i < polygon.length; i++) {
                polyx[i] = polygon[i].x;
                polyy[i] = polygon[i].y;
            }

            return isInside(point.y, point.x, polyx, polyy);
        }

}
