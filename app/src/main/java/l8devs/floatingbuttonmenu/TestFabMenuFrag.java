package l8devs.floatingbuttonmenu;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 7/14/15.
 */
public class TestFabMenuFrag extends FabMenuFragment {
    @Override
    protected List<FabMenuItem> getFabMenuItems() {
        List<FabMenuItem> list = new ArrayList<>(4);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_edit);
        list.add(new FabMenuItem(android.R.string.search_go, drawable, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                    .setMessage("1 clicked")
                    .show();
                dismiss();
            }
        }));
        drawable = getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
        list.add(new FabMenuItem(android.R.string.cancel, drawable, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                    .setMessage("2 clicked")
                    .show();
                dismiss();
            }
        }));
        drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        list.add(new FabMenuItem(R.string.hello_world, drawable, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        }));
        return list;
    }
}
