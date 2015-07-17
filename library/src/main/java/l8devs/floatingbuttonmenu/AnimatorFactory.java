package l8devs.floatingbuttonmenu;


import android.animation.Animator;
import android.support.annotation.Nullable;
import android.view.View;


/**
 * Implementators of this interface should know how to create an animator given a view.
 */
public interface AnimatorFactory<T extends View> {
  @Nullable
  Animator createAnimator(T view);
}
