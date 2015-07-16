package l8devs.floatingbuttonmenu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} that is used to trigger the {@link FabMenuFragment}.
 */
public class FabFragment extends Fragment {

  public FabFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_fab, container, false);
    FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Fragment newFragment = new SampleFabMenuFragment();

        getFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
            .add(R.id.fragment_container, newFragment, "fab menu")
            .addToBackStack("fab menu")
            .commit();
      }
    });

    return view;
  }

}
