package team.vision.team1310;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class Tab2 extends Fragment {

    AppContext appContext = AppContext.getInstance();

    View      view;
    EditText tWidthMin;
    EditText tWidthMax;
    EditText tHeightMax;
    EditText tHeightMin;
    EditText tRatioMin;
    EditText tRatioMax;
    EditText tPerimeterMin;
    EditText tAreaMin;

    private OnFragmentInteractionListener mListener;

    public Tab2() {
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
        view = inflater.inflate(R.layout.fragment_tab2, container, false);

        tWidthMin = (EditText) view.findViewById(R.id.tWidthMin);
        tWidthMax = (EditText) view.findViewById(R.id.tWidthMax);
        tHeightMax = (EditText) view.findViewById(R.id.tHeightMax);
        tHeightMin = (EditText) view.findViewById(R.id.tHeightMin);
        tRatioMin = (EditText) view.findViewById(R.id.tRatioMin);
        tRatioMax = (EditText) view.findViewById(R.id.tRatioMax);
        tPerimeterMin = (EditText) view.findViewById(R.id.tPerimeterMin);
        tAreaMin = (EditText) view.findViewById(R.id.tAreaMin);

        tWidthMin.setText(String.valueOf(appContext.tWidthMin));
        tWidthMax.setText(String.valueOf(appContext.tWidthMax));
        tHeightMax.setText(String.valueOf(appContext.tHeightMax));
        tHeightMin.setText(String.valueOf(appContext.tHeightMin));
        tRatioMin.setText(String.valueOf(appContext.tRatioMin));
        tRatioMax.setText(String.valueOf(appContext.tRatioMax));
        tPerimeterMin.setText(String.valueOf(appContext.tPerimeterMin));
        tAreaMin.setText(String.valueOf(appContext.tAreaMin));

        tWidthMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tWidthMin = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });
        tWidthMax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tWidthMax = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });
        tHeightMax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tHeightMax = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });
        tHeightMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tHeightMin = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });
        tRatioMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tRatioMin = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });
        tRatioMax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tRatioMax = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });
        tPerimeterMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tPerimeterMin = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();

            }
        });
        tAreaMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                appContext.tAreaMin = Double.parseDouble(((EditText) view).getText().toString());
                appContext.saveSettings();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        tWidthMin.clearFocus();
        tWidthMax.clearFocus();
        tHeightMax.clearFocus();
        tHeightMin.clearFocus();
        tRatioMin.clearFocus();
        tRatioMax.clearFocus();
        tPerimeterMin.clearFocus();
        tAreaMin.clearFocus();
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
}
