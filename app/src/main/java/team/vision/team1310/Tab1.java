package team.vision.team1310;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

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
    TextView tvHue, tvSaturation, tvLuminance, tvTotalContours;

    Mat mRgba, imgGray, imgCanny, imgThreshold;

    RelativeLayout HSLSliders, relativeLayoutTab1;

    Switch rgbSwitch;

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

        rgbSwitch = (Switch) view.findViewById(R.id.rgbSwitch);

        hueSlider = (MultiSlider) view.findViewById(R.id.hueSlider);
        saturationSlider = (MultiSlider) view.findViewById(R.id.saturationSlider);
        luminanceSlider = (MultiSlider) view.findViewById(R.id.luminanceSlider);

        HSLSliders = (RelativeLayout) view.findViewById(R.id.HSLSliders);
        relativeLayoutTab1 = (RelativeLayout) view.findViewById(R.id.relativeLayoutTab1);

        hueSlider.setMax(180);
        saturationSlider.setMax(255);
        luminanceSlider.setMax(255);

        tvHue = (TextView) view.findViewById(R.id.tvHue);
        tvSaturation = (TextView) view.findViewById(R.id.tvSaturation);
        tvLuminance = (TextView) view.findViewById(R.id.tvLuminance);
        tvTotalContours = (TextView) view.findViewById(R.id.tvTotalContours);

        rgbSwitch.setChecked(appContext.showRGB);

        slidersVisibility(!rgbSwitch.isChecked());

        rgbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appContext.showRGB = isChecked;
                slidersVisibility(!isChecked);
                appContext.saveSettings();
            }
        });

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
        appContext.saveSettings();

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
        VisionAlgorithm.hslThreshold(mRgba, appContext.hueValues, appContext.saturationValues, appContext.luminanceValues, imgThreshold);

        boolean findContoursExternalOnly = true;
        VisionAlgorithm.findContours(imgThreshold, findContoursExternalOnly, findContoursOutput);

        // Step Filter_Contours0:
        ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
        VisionAlgorithm.filterContours(filterContoursContours, appContext.tAreaMin, appContext.tPerimeterMin, appContext.tWidthMin, appContext.tWidthMax, appContext.tHeightMin, appContext.tHeightMax, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, appContext.tRatioMin, appContext.tRatioMax, filterContoursOutput);

        int totalContours = filterContoursOutput.size();

        // Output the total found contours
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                tvTotalContours.setText("Contours: " + filterContoursOutput.size());
            }
        });

        for (int i = 0; i < filterContoursOutput.size(); i++) {
            Log.d(TAG, "Contour area: " + Imgproc.contourArea(filterContoursOutput.get(i)) );

            final Rect bb = Imgproc.boundingRect(filterContoursOutput.get(i));

            Log.d(TAG, "Contour height: " + bb.height);
            Log.d(TAG, "Contour width: " + bb.width );
            Log.d(TAG, String.format("x, y values: (%s,%s)", bb.x, bb.y));
        }

        // Display RGB or HSL depending on what we select
        Mat mDisplay = appContext.showRGB ? mRgba : imgThreshold;

        // If we have less than 6 contours, then we can color them with green
        // Because otherwise the phone will lag as there can be over 1000+ contours and it will try to draw a green over each one of them
        if (totalContours < 6) {
            for (MatOfPoint contour : filterContoursOutput) {
//            Log.i(TAG, "width: " + contour.width() + " height: " + contour.height());
                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                VisionAlgorithm.drawRectangle(mDisplay, rotatedRect);
            }
        }

        return mDisplay;
    }


    /**
     * Update the visibility of Sliders depending on if it's hidden or not
     *
     * @param visible
     */
    private void slidersVisibility(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        HSLSliders.setVisibility(visibility);
        relativeLayoutTab1.setBackgroundColor(visible ? Color.WHITE : Color.BLACK);
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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
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
