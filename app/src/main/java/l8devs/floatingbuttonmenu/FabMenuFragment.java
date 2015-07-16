package l8devs.floatingbuttonmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;


/**
 * A {@link DialogFragment} which is displays a FloatingActionButton .
 */
public class FabMenuFragment extends DialogFragment {
  private final AnimatorSet animatorEntrance = new AnimatorSet();

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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View view = inflater.inflate(R.layout.fragment_fab_menu, container, false);

    final View.OnClickListener dismissOnclickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    };
    view.setOnClickListener(dismissOnclickListener);

    Map<Integer, View.OnClickListener> fabMenuItems = getFabMenuItems();
    final TableLayout table = (TableLayout) view.findViewById(R.id.table);
    int numRows = table.getChildCount();
    for (int i = 0; i < numRows; i++) {
      TableRow row = (TableRow) table.getChildAt(i);
      row.setOnClickListener(dismissOnclickListener);
      setFabMenuOnClickListener(row, fabMenuItems.get(row.getId()));
    }

    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
        .OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        //At this point the layout is complete and the
        //dimensions of myView and any child views are known.
        int numRows = table.getChildCount();
        Animator[] rowAnimations = new Animator[numRows];
        for (int i = 0; i < numRows; i++) {
          TableRow row = (TableRow) table.getChildAt(i);
          rowAnimations[i] = createFabMenuAnimation(row);
        }
        animatorEntrance.playTogether(rowAnimations);
        animatorEntrance.setDuration(
            getResources().getInteger(android.R.integer.config_shortAnimTime));
        animatorEntrance.start();

        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
    return view;
  }

  @Override
  public void onStop() {
    super.onStop();
    animatorEntrance.cancel();
  }

  private Animator createFabMenuAnimation(TableRow row) {
    AnimatorSet animSet = new AnimatorSet();
    animSet.playTogether(ObjectAnimator.ofFloat(row, View.TRANSLATION_Y, row.getHeight(), 0),
        ObjectAnimator.ofFloat(row, View.TRANSLATION_X, row.getWidth() / 5, 0));
    return animSet;
  }

  private void setFabMenuOnClickListener(TableRow row, View.OnClickListener listener) {
    // Add listener to children of the row
    for (int j = 0; j < row.getChildCount(); j++) {
      row.getChildAt(j).setOnClickListener(listener);
    }
  }
}
