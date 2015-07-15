package l8devs.floatingbuttonmenu;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TableLayout;

import java.util.List;


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link FabMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FabMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class FabMenuFragment extends DialogFragment {
    private FloatingActionButton mFab;

    private OnFragmentInteractionListener mListener;

    public FabMenuFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    protected static class FabMenuItem {
        private final int labelStrId;
        private final Drawable drawable;
        private final View.OnClickListener onClickListener;

        public FabMenuItem(int labelStrId, Drawable drawable, View.OnClickListener onClickListener) {
            this.labelStrId = labelStrId;
            this.drawable = drawable;
            this.onClickListener = onClickListener;
        }
    }

    protected abstract List<FabMenuItem> getFabMenuItems();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fab_menu, container, false);

        View.OnClickListener dismissOnclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        view.setOnClickListener(dismissOnclickListener);

        List<FabMenuItem> fabMenuItems = getFabMenuItems();

        TableLayout table = (TableLayout) view.findViewById(R.id.table);
        int numRows = table.getChildCount();
        for (int i=0;i<numRows;i++) {
            FabMenuItem menuItem = fabMenuItems.get(i);
            View row = table.getChildAt(i);
            row.setOnClickListener(dismissOnclickListener);

            AppCompatTextView labelView = (AppCompatTextView) row.findViewById(R.id.label);
            labelView.setText(menuItem.labelStrId);
            labelView.setOnClickListener(menuItem.onClickListener);

            FloatingActionButton fltngActBtn = (FloatingActionButton) row.findViewById(R.id.fab);
            fltngActBtn.setOnClickListener(menuItem.onClickListener);
            fltngActBtn.setImageDrawable(menuItem.drawable);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
//        mFab.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
