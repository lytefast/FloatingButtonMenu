package l8devs.floatingbuttonmenu;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;


/**
 * A fragment which is displays a FloatingActionButton menu
 */
public class FabMenuFragment extends DialogFragment {
    private FloatingActionButton mFab;

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

    protected Map<Integer, View.OnClickListener> getFabMenuItems() {
        HashMap<Integer, View.OnClickListener> listenerMap = new HashMap<>();
        listenerMap.put(R.id.fab_menu_1, createListener("fab_menu_1"));
        listenerMap.put(R.id.fab_menu_2, createListener("fab_menu_2"));
        listenerMap.put(R.id.fab_menu_last, createListener("last menu item"));
        return listenerMap;
    }

    private View.OnClickListener createListener(final String title) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                    .setMessage(title + " clicked")
                    .show();
                dismiss();
            }
        };
    }

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

        Map<Integer, View.OnClickListener> fabMenuItems = getFabMenuItems();

        TableLayout table = (TableLayout) view.findViewById(R.id.table);
        int numRows = table.getChildCount();
        for (int i = 0; i < numRows; i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            row.setOnClickListener(dismissOnclickListener);
            setFabMenuOnClicklistener(row, fabMenuItems.get(row.getId()));
        }
        return view;
    }

    private void setFabMenuOnClicklistener(TableRow row, View.OnClickListener listener) {
        // Add listener to children of the row
        for (int j = 0; j < row.getChildCount(); j++) {
            row.getChildAt(j).setOnClickListener(listener);
        }
    }

}
