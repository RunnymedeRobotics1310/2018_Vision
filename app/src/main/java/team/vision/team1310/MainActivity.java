package team.vision.team1310;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.apptik.widget.MultiSlider;

public class MainActivity extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener, Tab3.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private AppContext appContext = AppContext.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appContext.mainActivity = this;
        appContext.loadSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTabs();

    }


    protected void setupTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("HSL"));
        tabLayout.addTab(tabLayout.newTab().setText("Contours"));
        tabLayout.addTab(tabLayout.newTab().setText("Connection"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * FOR mSOCKET CONNECTION
     */

    private Socket mSocket;
    private String mHost;
    private int mPort;
    private PrintWriter mOut;
    public boolean mConnected;
    private Context mContext = this;


    private class ConnectTask extends AsyncTask<String, Void, Void> {

        private Context context;

        public ConnectTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            updateStatus("Connecting", StatusColors.INFO);

//            showToast(context, "Connecting..");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mConnected) {
                updateStatus("Connected", StatusColors.INFO);
                showToast(context, "Connection successful");
                updateView();
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                mSocket = new Socket(mHost, mPort);
                mOut = new PrintWriter(mSocket.getOutputStream(), true);
                mConnected = true;
            } catch (UnknownHostException e) {
                updateStatus("mHost issue", StatusColors.INFO);
                showToast(context, "Don't know about mHost: " + mHost + ":" + mPort);
                Log.e("SocketClient", e.getMessage());
            } catch (IOException e) {
                updateStatus("No I/O", StatusColors.INFO);
                showToast(context, "Couldn't get I/O for the connection to: " + mHost + ":" + mPort);
                Log.e("SocketClient", e.getMessage());
            }

            return null;
        }
    }


    public void connect()
    {
        mHost = appContext.connectionHost;
        mPort = appContext.connectionPort;
        new ConnectTask(mContext).execute();
    }


    public void disconnect()
    {
        if (mConnected)
        {
            try {
                mOut.close();
                mSocket.close();
                mConnected = false;
                updateView();
                showToast(mContext, "Disconnected successfully");
            } catch (IOException e) {
                showToast(mContext, "Couldn't get I/O for the connection");
                Log.e("SocketClient", e.getMessage());
            }
        }

    }


    public void send(final String command)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mConnected) mOut.println(command);;
            }
        };

        new Thread(runnable).start(); // start a new thread
    }


    private void showToast(final Context context, final String message) {
        new Handler(context.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateView() {

        TextView tvPortNumber = (TextView) findViewById(R.id.tvPortNumber);
        TextView tConnectionStatus = (TextView) findViewById(R.id.tConnectionStatus);
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        Button btnSend = (Button) findViewById(R.id.btnSend);


        if (mConnected) {
            updateStatus("Connected (" + mPort + ")", StatusColors.SUCCESS);


            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);

            // If it's connected to socket, we don't let them touch it...
            tvPortNumber.setEnabled(false);
        } else {

            updateStatus("Disconnected", StatusColors.ERROR);

            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            btnSend.setEnabled(false);

            tvPortNumber.setEnabled(true);

        }
    }

    private enum StatusColors {
         SUCCESS, INFO, ERROR
    }

    private void updateStatus(String message, StatusColors color) {
        TextView tConnectionStatus = (TextView) findViewById(R.id.tConnectionStatus);
        tConnectionStatus.setText(message);
        switch (color) {
            case ERROR:
                tConnectionStatus.setTextColor(Color.rgb(160, 16, 11));
                break;
            case SUCCESS:
                tConnectionStatus.setTextColor(Color.rgb(16, 178, 51));
                break;
            case INFO:
                tConnectionStatus.setTextColor(Color.rgb(50, 89, 153));
                break;
        }
    }

}
