package l8devs.floatingbuttonmenu;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Map;


/**
 * A {@link DialogFragment} which is displays a FloatingActionButton.
 *
 * @see #getFabMenuItems()
 */
public abstract class FabMenuFragment extends DialogFragment {
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

  //<editor-fold desc="Fragment Overrides">
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
        configureEntranceAnimator(getMenuAnimators(table))
            .start();
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
  //</editor-fold>

  //<editor-fold desc="Exposed methods for defining behaviour">

  /**
   * Return a map of the menu row id to an {@link View.OnClickListener}.
   */
  protected abstract Map<Integer, View.OnClickListener> getFabMenuItems();

  protected Animator configureEntranceAnimator(Animator[] menuRowAnimators) {
    animatorEntrance.playTogether(menuRowAnimators);
    animatorEntrance.setInterpolator(
        AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.accelerate_decelerate));
    int totalDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    return animatorEntrance
        .setDuration(totalDuration);
  }

  /**
   * Given the menu container, extract and create an entrance {@link Animator} for
   * each menu row.
   * <p>
   *   The default behaviour is to animate in reverse since the assumption is that the menu is on
   *   the bottom right.
   * </p>
   */
  protected Animator[] getMenuAnimators(TableLayout menuContainer) {
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
    if (row.getId() == R.id.fab_menu_main) {
      // special case the main fab button
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

  public final AnimatorFactory<View> animatorFactoryFadeIn = new
      AnimatorFactory<View>() {
    @Nullable
    @Override
    public Animator createAnimator(View view) {
      Animator fadeInAnimator =
          AnimatorInflater.loadAnimator(getActivity(), android.R.animator.fade_in);
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
          View fab = view.findViewWithTag(getString(R.string.fab_tag_menu_icon));
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

  private void setFabMenuOnClickListener(TableRow row, View.OnClickListener listener) {
    // Add listener to children of the row
    for (int j = 0; j < row.getChildCount(); j++) {
      row.getChildAt(j).setOnClickListener(listener);
    }
  }
}
