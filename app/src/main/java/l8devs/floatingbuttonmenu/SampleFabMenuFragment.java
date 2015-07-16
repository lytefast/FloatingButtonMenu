package l8devs.floatingbuttonmenu;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;


/**
 * A Sample implementation of FabMenuFragment.
 */
public class SampleFabMenuFragment extends FabMenuFragment {

  public SampleFabMenuFragment() {
    // Required empty public constructor
  }

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

  @Override
  protected Animator configureEntranceAnimator(Animator[] menuRowAnimators) {
    int duration = 1000 / menuRowAnimators.length;  // slow it down so we can see it in action
    AnimatorSet finalAnimator = (AnimatorSet) super.configureEntranceAnimator(menuRowAnimators);

    // Uncomment to see each row played out sequentially
//    AnimatorSet animatorSet = new AnimatorSet();
//    animatorSet.playSequentially(finalAnimator.getChildAnimations());
//    finalAnimator = animatorSet;

    return finalAnimator.setDuration(duration);
  }


  @Override
  protected Animator createMenuRowAnimation(ViewGroup row) {
    if (row.getId() == R.id.fab_menu_main) {
      // special case the main fab button
      return animatorFactoryFadeIn.createAnimator(row);
    }
    return createMenuRowAnimation(row,
        animatorFactoryFadeIn, animatorFactoryScaleUp, animatorFactorySlideInFromRight);
  }

  public final AnimatorFactory<ViewGroup> animatorFactorySlideInFromRight = new
      AnimatorFactory<ViewGroup>() {
    @Nullable
    @Override
    public Animator createAnimator(ViewGroup row) {
      View firstChild = row.getChildAt(0);
      return ObjectAnimator.ofFloat(firstChild, View.TRANSLATION_X, firstChild.getWidth(), 0);
    }
  };
}
