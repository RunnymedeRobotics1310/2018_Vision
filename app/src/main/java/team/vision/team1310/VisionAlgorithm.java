package team.vision.team1310;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;

/**
 * The vision Algorithm class responsible for detecting cube
 *
 */
public class VisionAlgorithm {


    /**
     * Draw a rectangle over the found contours that match the filterContours criteria
     * @param image
     * @param rotatedRect
     */
    public static void drawRectangle(Mat image, RotatedRect rotatedRect) {
         Scalar color = new Scalar(81, 190, 0);
        int thickness = 5;
        Point[] vertices = new Point[4];
        rotatedRect.points(vertices);
        MatOfPoint points = new MatOfPoint(vertices);
        Imgproc.drawContours(image, Arrays.asList(points), -1, color, thickness);
    }


    /**
     * The HSL threshold to change
     * @param input
     * @param hue
     * @param sat
     * @param lum
     * @param out
     */
    public static void hslThreshold(Mat input, int[] hue, int[] sat, int[] lum, Mat out) {
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HLS);
        Core.inRange(out, new Scalar(hue[0], lum[0], sat[0]),
                new Scalar(hue[1], lum[1], sat[1]), out);
    }

    /**
     * Find the contours
     * @param input
     * @param externalOnly
     * @param contours
     */
    public static void findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) {
        Mat hierarchy = new Mat();
        contours.clear();
        int mode = externalOnly ? Imgproc.RETR_EXTERNAL : Imgproc.RETR_LIST;
        Imgproc.findContours(input, contours, hierarchy, mode, Imgproc.CHAIN_APPROX_SIMPLE);
    }

    /**
     * Filter the contours by height, width, area, etc
     * @param inputContours
     * @param minArea
     * @param minPerimeter
     * @param minWidth
     * @param maxWidth
     * @param minHeight
     * @param maxHeight
     * @param solidity
     * @param maxVertexCount
     * @param minVertexCount
     * @param minRatio
     * @param maxRatio
     * @param output
     */
    public static void filterContours(List<MatOfPoint> inputContours, double minArea, double minPerimeter, double minWidth, double maxWidth, double minHeight, double maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double minRatio, double maxRatio, List<MatOfPoint> output) {
        final MatOfInt hull = new MatOfInt();
        output.clear();
        //operation
        for (int i = 0; i < inputContours.size(); i++) {
            final MatOfPoint contour = inputContours.get(i);
            final Rect bb = Imgproc.boundingRect(contour);
            if (bb.width < minWidth || bb.width > maxWidth) continue;
            if (bb.height < minHeight || bb.height > maxHeight) continue;
            final double area = Imgproc.contourArea(contour);
            if (area < minArea) continue;
            if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter)
                continue;
            Imgproc.convexHull(contour, hull);
            MatOfPoint mopHull = new MatOfPoint();
            mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
            for (int j = 0; j < hull.size().height; j++) {
                int index = (int) hull.get(j, 0)[0];
                double[] point = new double[]{contour.get(index, 0)[0], contour.get(index, 0)[1]};
                mopHull.put(j, 0, point);
            }
            final double solid = 100 * area / Imgproc.contourArea(mopHull);
            if (solid < solidity[0] || solid > solidity[1]) continue;
            if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount) continue;
            final double ratio = bb.width / (double) bb.height;
            if (ratio < minRatio || ratio > maxRatio) continue;
            output.add(contour);
        }
    }
}
