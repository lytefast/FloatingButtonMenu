package l8devs.floatingbuttonmenu;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TableLayout;

import java.util.HashMap;
import java.util.Map;


/**
 * A {@link DialogFragment} which is displays a FloatingActionButton.
 */
public class FabMenuFragment extends DialogFragment {

  protected FloatingButtonMenuInitializer floatingButtonMenuInitializer;
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

  //<editor-fold desc="Fragment Overrides">

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    this.floatingButtonMenuInitializer =
        new FloatingButtonMenuInitializer(activity, getFabMenuItems());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View view = inflater.inflate(R.layout.fragment_fab_menu, container, false);

    final View.OnClickListener dismissOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    };
    view.setOnClickListener(dismissOnClickListener);

    final TableLayout table = (TableLayout) view.findViewById(R.id.table);
    floatingButtonMenuInitializer.initialize(table, dismissOnClickListener);
    return view;
  }
  //</editor-fold>


  protected Map<Integer, View.OnClickListener> getFabMenuItems() {
    HashMap<Integer, View.OnClickListener> listenerMap = new HashMap<>();
    listenerMap.put(R.id.fab_menu_1, createListener("fab_menu_1"));
    listenerMap.put(R.id.fab_menu_2, createListener("fab_menu_2"));
    listenerMap.put(R.id.fab_menu_main, createListener("last menu item"));
    return listenerMap;
  }


  private View.OnClickListener createListener(final String title) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        v.animate().alpha(0.5f).scaleX(2).scaleY(2)
            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                new AlertDialog.Builder(getActivity())
                    .setMessage(title + " clicked")
                    .show();
                dismiss();
              }
            });
      }
    };
  }
}
