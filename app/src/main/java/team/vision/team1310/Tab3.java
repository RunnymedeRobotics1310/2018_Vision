package team.vision.team1310;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This is responsible for connecting to server
 */
public class Tab3 extends Fragment {

    private OnFragmentInteractionListener mListener;

    AppContext appContext = AppContext.getInstance();
    private String TAG = "Tab3";

    View view;
    Button btnConnect, btnDisconnect, btnSend;
    TextView tvPortNumber;
    Switch switchAutoConnect;

    public Tab3() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_tab3, container, false);


        btnConnect = (Button) view.findViewById(R.id.btnConnect);
        btnDisconnect = (Button) view.findViewById(R.id.btnDisconnect);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        tvPortNumber = (TextView) view.findViewById(R.id.tvPortNumber);

        tvPortNumber.setText(String.valueOf(appContext.connectionPort));

        switchAutoConnect = (Switch) view.findViewById(R.id.switchAutoConnect);

        switchAutoConnect.setChecked(appContext.autoConnect);

        switchAutoConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appContext.autoConnect = isChecked;
                appContext.saveSettings();
            }
        });


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).connect();
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).disconnect();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).send("Testing Connection");
            }
        });

        tvPortNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.connectionPort = Integer.parseInt(((TextView) view).getText().toString());
                appContext.saveSettings();
            }
        });


        return view;
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
    public void onPause() {
        super.onPause();
        tvPortNumber.clearFocus();
        ((MainActivity)getActivity()).updateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).updateView();
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

    private void showToast(final Context context, final String message) {
        new Handler(context.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }


}
