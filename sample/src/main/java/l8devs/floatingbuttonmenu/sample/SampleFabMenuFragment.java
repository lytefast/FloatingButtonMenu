package l8devs.floatingbuttonmenu.sample;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import l8devs.floatingbuttonmenu.AnimatorFactory;
import l8devs.floatingbuttonmenu.FloatingButtonMenuInitializer;


/**
 * A Sample implementation of FabMenuFragment.
 */
public class SampleFabMenuFragment extends FabMenuFragment {

  public SampleFabMenuFragment() {
    // Required empty public constructor
  }


  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    this.floatingButtonMenuInitializer =
        new FloatingButtonMenuInitializer(activity, getFabMenuItems()) {
          @Override
          protected Animator configureEntranceAnimator(Animator[] menuRowAnimators) {
            // slow down animation duration so we can see it in action
            int duration = 1000 / menuRowAnimators.length;
            AnimatorSet finalAnimator =
                (AnimatorSet) super.configureEntranceAnimator(menuRowAnimators);

            // Uncomment to see each row played out sequentially
        //    AnimatorSet animatorSet = new AnimatorSet();
        //    animatorSet.playSequentially(finalAnimator.getChildAnimations());
        //    finalAnimator = animatorSet;

            return finalAnimator.setDuration(duration);
          }

          /**
           * Override the underlying implementation so we can add in our custom slide from right
           * animation.
           */
          @Override
          protected Animator createMenuRowAnimation(ViewGroup row) {
            if (row.getId() == R.id.fab_menu_main) {
              // special case the main menu row
              // Note this is done differently than the base (which searches for a tag)
              return animatorFactoryFadeIn.createAnimator(row);
            }
            return createMenuRowAnimation(row,
                animatorFactoryFadeIn, animatorFactoryScaleUp, animatorFactorySlideInFromRight);
          }
        };
  }

  /**
   * Custom animation
   */
  public final AnimatorFactory<ViewGroup> animatorFactorySlideInFromRight = new
      AnimatorFactory<ViewGroup>() {
    @Nullable
    @Override
    public Animator createAnimator(ViewGroup row) {
      View firstChild = row.getChildAt(0);
       AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.playTogether(
          ObjectAnimator.ofFloat(firstChild, View.TRANSLATION_X, firstChild.getWidth(), 0),
          ObjectAnimator.ofFloat(firstChild, View.TRANSLATION_Y, firstChild.getHeight() * .1f, 0));
      return animatorSet;
    }
  };
}
