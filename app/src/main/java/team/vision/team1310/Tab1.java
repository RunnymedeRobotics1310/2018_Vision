package team.vision.team1310;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.MultiSlider;

public class Tab1 extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private OnFragmentInteractionListener mListener;
    String TAG = "TAB1";

    AppContext appContext;

    public Tab1() {
        // Required empty public constructor
    }

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    View view;

    MultiSlider hueSlider, saturationSlider, luminanceSlider;
    TextView tvHue, tvSaturation, tvLuminance;

    Mat mRgba, imgGray, imgCanny, imgThreshold;

    private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
    private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
    public JavaCameraView javaCameraView;

    public static double[] filterContoursSolidity = {0, 100};
    public static double filterContoursMaxVertices = 1000000.0;
    public static double filterContoursMinVertices = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       appContext = AppContext.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab1, container, false);


        javaCameraView = (JavaCameraView) view.findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        hueSlider = (MultiSlider) view.findViewById(R.id.hueSlider);
        saturationSlider = (MultiSlider) view.findViewById(R.id.saturationSlider);
        luminanceSlider = (MultiSlider) view.findViewById(R.id.luminanceSlider);

        hueSlider.setMax(180);
        saturationSlider.setMax(255);
        luminanceSlider.setMax(255);

        tvHue = (TextView) view.findViewById(R.id.tvHue);
        tvSaturation = (TextView) view.findViewById(R.id.tvSaturation);
        tvLuminance = (TextView) view.findViewById(R.id.tvLuminance);

        hueSlider.getThumb(0).setValue(appContext.hueValues[0]);
        hueSlider.getThumb(1).setValue(appContext.hueValues[1]);
        saturationSlider.getThumb(0).setValue(appContext.saturationValues[0]);
        saturationSlider.getThumb(1).setValue(appContext.saturationValues[1]);
        luminanceSlider.getThumb(0).setValue(appContext.luminanceValues[0]);
        luminanceSlider.getThumb(1).setValue(appContext.luminanceValues[1]);

        updateSliderText(hueSlider, appContext.hueValues, tvHue);
        updateSliderText(saturationSlider, appContext.saturationValues, tvSaturation);
        updateSliderText(luminanceSlider, appContext.luminanceValues, tvLuminance);

        hueSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                updateSliderText(multiSlider, appContext.hueValues, tvHue);
            }
        });

        saturationSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                updateSliderText(multiSlider, appContext.saturationValues, tvSaturation);
            }
        });

        luminanceSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
               updateSliderText(multiSlider, appContext.luminanceValues, tvLuminance);
            }
        });

        return view;
    }

    private void updateSliderText(MultiSlider slider, int[] value, TextView tv) {
        value[0] = slider.getThumb(0).getValue();
        value[1] = slider.getThumb(1).getValue();

        tv.setText(slider.getThumb(0).getValue() + " - " + slider.getThumb(1).getValue());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC4);
        imgCanny = new Mat(height, width, CvType.CV_8UC4);
        imgThreshold = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        hslThreshold(mRgba, appContext.hueValues, appContext.saturationValues, appContext.luminanceValues, imgThreshold);

        boolean findContoursExternalOnly = true;
        findContours(imgThreshold, findContoursExternalOnly, findContoursOutput);

//        Imgproc.drawContours(imgThreshold, findContoursOutput, -1, new Scalar(255,0,0));


        // Step Filter_Contours0:
        ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
        filterContours(filterContoursContours, appContext.tAreaMin, appContext.tPerimeterMin, appContext.tWidthMin, appContext.tWidthMax, appContext.tHeightMin, appContext.tHeightMax, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, appContext.tRatioMin, appContext.tRatioMax, filterContoursOutput);


        // Draw the contours
        Scalar green = new Scalar(81, 190, 0);
        for (MatOfPoint contour : filterContoursOutput) {

            if (!contour.empty()) {
                for (Point p : contour.toArray()) {
                    Log.i(TAG, p.x + " , " + p.y);
                }
                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                drawRotatedRect(imgThreshold, rotatedRect, green, 5);
            }
        }


        return imgThreshold;

    }

    public static void drawRotatedRect(Mat image, RotatedRect rotatedRect, Scalar color, int thickness) {
        Point[] vertices = new Point[4];
        rotatedRect.points(vertices);
        MatOfPoint points = new MatOfPoint(vertices);
        Imgproc.drawContours(image, Arrays.asList(points), -1, color, thickness);
    }


    private void hslThreshold(Mat input, int[] hue, int[] sat, int[] lum, Mat out) {
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HLS);
        Core.inRange(out, new Scalar(hue[0], lum[0], sat[0]),
                new Scalar(hue[1], lum[1], sat[1]), out);
    }

    private void findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) {
        Mat hierarchy = new Mat();
        contours.clear();
        int mode = externalOnly ? Imgproc.RETR_EXTERNAL : Imgproc.RETR_LIST;
        Imgproc.findContours(input, contours, hierarchy, mode, Imgproc.CHAIN_APPROX_SIMPLE);
    }

    private void filterContours(List<MatOfPoint> inputContours, double minArea,
                                double minPerimeter, double minWidth, double maxWidth, double minHeight, double
                                        maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
                                        minRatio, double maxRatio, List<MatOfPoint> output) {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        appContext.saveSettings();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded successfully");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);


        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }
}
