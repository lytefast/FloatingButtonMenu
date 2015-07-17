package l8devs.floatingbuttonmenu;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Map;


/**
 * Helps initialize a floating button menu.
 * <p>
 * Register events to {@link View}s via the constructor parameter {@link #onClickListenerMap}.
 * </p>
 *
 * Use the {@link R.string#fab_tag_menu_icon} and {@link R.string#fab_tag_main_row} view tags
 * to specify the button section of a menu row and to specify the main menu row respectively.
 *
 * @see l8devs.floatingbuttonmenu.R.string#fab_tag_menu_icon
 * @see l8devs.floatingbuttonmenu.R.string#fab_tag_main_row
 */
public class FloatingButtonMenuInitializer {
  private final Context context;

  /**
   * Map of the menu row id to an {@link View.OnClickListener}.
   */
  private final Map<Integer, View.OnClickListener> onClickListenerMap;

  public FloatingButtonMenuInitializer(Context context,
                                       Map<Integer, View.OnClickListener> onClickListenerMap) {
    this.context = context;
    this.onClickListenerMap = onClickListenerMap;
  }

  /**
   * Initializes the menu animations on start. This method should be called from
   * {@link android.support.v4.app.Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
   *
   * @return the current instance
   */
  public FloatingButtonMenuInitializer initialize(final ViewGroup menuContainer,
                                                  View.OnClickListener defaultRowOnClickListener) {
    int numRows = menuContainer.getChildCount();
    for (int i = 0; i < numRows; i++) {
      TableRow row = (TableRow) menuContainer.getChildAt(i);
      row.setOnClickListener(defaultRowOnClickListener);
      setFabMenuOnClickListener(row, onClickListenerMap.get(row.getId()));
    }

    /*
     * Wait for layout to complete before trying to calculate animations.
     * Otherwise we get invalid dimensions.
     */
    menuContainer.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        configureEntranceAnimator(getMenuAnimators(menuContainer)).start();
        menuContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
    return this;
  }

  //<editor-fold desc="Exposed methods for defining behaviour">

  protected Animator configureEntranceAnimator(Animator[] menuRowAnimators) {
    AnimatorSet animatorEntrance = new AnimatorSet();
    animatorEntrance.playTogether(menuRowAnimators);
    animatorEntrance.setInterpolator(
        AnimationUtils.loadInterpolator(context, android.R.interpolator.accelerate_decelerate));

    int totalDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    return animatorEntrance.setDuration(totalDuration);
  }

  /**
   * Given the menu container, extract and create an entrance {@link Animator} for
   * each menu row.
   * <p>
   *   The default behaviour is to animate in reverse since the assumption is that the menu is on
   *   the bottom right.
   * </p>
   */
  protected Animator[] getMenuAnimators(ViewGroup menuContainer) {
    int numRows = menuContainer.getChildCount();
    Animator[] rowAnimations = new Animator[numRows];
    for (int i = 0; i < numRows; i++) {
      final ViewGroup row = (ViewGroup) menuContainer.getChildAt(i);
      row.setVisibility(View.INVISIBLE);  // we want to hide this until we are ready to animate
      Animator fabMenuAnimator = createMenuRowAnimation(row);
      fabMenuAnimator.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
          row.setVisibility(View.VISIBLE);
        }
      });
      // animate in reverse since the assumption is that the menu is on the bottom right
      rowAnimations[numRows - 1 - i] = fabMenuAnimator;
    }
    return rowAnimations;
  }

  /**
   * Override this function to provide your own animation for the row.
   *
   * @param row the menu row container to apply the animators
   *
   * @see #createMenuRowAnimation(ViewGroup, AnimatorFactory[])
   */
  protected Animator createMenuRowAnimation(ViewGroup row) {
    if (row.getTag() == context.getString(R.string.fab_tag_main_row)) {
      // special case the main menu row
      return animatorFactoryFadeIn.createAnimator(row);
    }

    return createMenuRowAnimation(row,
        animatorFactoryFadeIn,
        animatorFactoryScaleUp,
        animatorFactorySlideUp);
  }

  /**
   * Creates the animation for the row given a set of {@link AnimatorFactory}.
   * <p>It is recommended to not override this method as this is just a helper method.
   * Instead override {@link #createMenuRowAnimation(ViewGroup)} and pass in a different list of
   * {@link AnimatorFactory}.
   * </p>
   *
   * @param row the menu row container to apply the animators
   * @param factories set of factories that know how to create animators for the row
   * @return {@link Animator} that can be used for the row.
   *
   * @see #createMenuRowAnimation(ViewGroup)
   */
  protected Animator createMenuRowAnimation(ViewGroup row,
                                            AnimatorFactory<? super ViewGroup>... factories) {
    ArrayList<Animator> animatorList = new ArrayList<>(factories.length);
    for (AnimatorFactory<? super ViewGroup> factory : factories) {
      Animator animator = factory.createAnimator(row);
      if (animator != null) {
        animatorList.add(animator);
      }
    }

    AnimatorSet finalAnimation = new AnimatorSet();
    finalAnimation.playTogether(animatorList);
    return finalAnimation;
  }

  //</editor-fold>

  //<editor-fold desc="AnimatorFactory implementations">

  public final AnimatorFactory<View> animatorFactoryFadeIn = new
      AnimatorFactory<View>() {
    @Nullable
    @Override
    public Animator createAnimator(View view) {
      Animator fadeInAnimator =
          AnimatorInflater.loadAnimator(context, android.R.animator.fade_in);
      fadeInAnimator.setTarget(view);
      return fadeInAnimator;
    }
  };

  public final AnimatorFactory<View> animatorFactorySlideUp = new
      AnimatorFactory<View>() {
    @Nullable
    @Override
    public Animator createAnimator(View view) {
      return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getHeight(), 0);
    }
  };

  public final AnimatorFactory<ViewGroup> animatorFactoryScaleUp =
      new AnimatorFactory<ViewGroup>
          () {
        @Nullable
        @Override
        public Animator createAnimator(ViewGroup view) {
          View fab = view.findViewWithTag(context.getString(R.string.fab_tag_menu_icon));
          if (fab == null) {
            return null;
          }

          AnimatorSet animatorSet = new AnimatorSet();
          animatorSet.playTogether(
              ObjectAnimator.ofFloat(fab, View.SCALE_X, 0, 1),
              ObjectAnimator.ofFloat(fab, View.SCALE_Y, 0, 1));
          return animatorSet;
        }
      };

  //</editor-fold>

  private void setFabMenuOnClickListener(TableRow row, View.OnClickListener listener) {
    // Add listener to children of the row
    for (int j = 0; j < row.getChildCount(); j++) {
      row.getChildAt(j).setOnClickListener(listener);
    }
  }
}
