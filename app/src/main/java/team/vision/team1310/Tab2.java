package team.vision.team1310;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class Tab2 extends Fragment {


    View view;
    EditText tWidthMin;
    EditText tWidthMax;
    EditText tHeightMax;
    EditText tHeightMin;
    EditText tRatioMin;
    EditText tRatioMax;
    EditText tPerimeterMin;
    EditText tAreaMin;
    Button btnSave, btnCancel;

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

        btnSave = (Button) view.findViewById(R.id.btnContourSave);
        btnCancel = (Button) view.findViewById(R.id.btnContourCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

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
