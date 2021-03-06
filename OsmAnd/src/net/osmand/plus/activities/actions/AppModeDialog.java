package net.osmand.plus.activities.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.osmand.plus.ApplicationMode;
import net.osmand.plus.ContextMenuAdapter;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;
import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class AppModeDialog {
	private static final int TOGGLE_SIZE = 48;

	public static View prepareAppModeView(Activity a, final Set<ApplicationMode> selected, boolean showDefault,
			ViewGroup parent, final boolean singleSelection, final View.OnClickListener onClickListener) {
		OsmandSettings settings = ((OsmandApplication) a.getApplication()).getSettings();
		final List<ApplicationMode> values = new ArrayList<ApplicationMode>(ApplicationMode.values(settings));
		if(!showDefault) {
			values.remove(ApplicationMode.DEFAULT);
		}
		if (showDefault || settings.getApplicationMode() != ApplicationMode.DEFAULT) {
			selected.add(settings.getApplicationMode());
		}
		return prepareAppModeView(a, values, selected, parent, singleSelection, false, onClickListener);
	}

	//special method for drawer menu
	//needed because if there's more than 4 items  - the don't fit in drawer
	public static View prepareAppModeDrawerView(Activity a, List<ApplicationMode> visible, final Set<ApplicationMode> selected, ContextMenuAdapter.BooleanResult allModes,
			final View.OnClickListener onClickListener) {
		OsmandSettings settings = ((OsmandApplication) a.getApplication()).getSettings();
		final List<ApplicationMode> values = new ArrayList<ApplicationMode>(ApplicationMode.values(settings));
		selected.add(settings.getApplicationMode());
		return prepareAppModeView(a, values, selected, null, true, true, onClickListener);
	}
	
	public static View prepareAppModeView(Activity a, final List<ApplicationMode> values , final Set<ApplicationMode> selected, 
				ViewGroup parent, final boolean singleSelection, boolean drawer, final View.OnClickListener onClickListener) {
		View ll = a.getLayoutInflater().inflate(R.layout.mode_toggles, parent);
		final View[] buttons = new View[values.size()];
		int k = 0;
		for(ApplicationMode ma : values) {
			buttons[k++] = createToggle(a.getLayoutInflater(), (OsmandApplication) a.getApplication(), (LinearLayout) ll.findViewById(R.id.app_modes_content), ma);
		}
		for (int i = 0; i < buttons.length; i++) {
			updateButtonState((OsmandApplication) a.getApplication(), values, selected, onClickListener, buttons, i,
					singleSelection);
		}
		return ll;
	}


	private static void updateButtonState(final OsmandApplication ctx, final List<ApplicationMode> visible,
			final Set<ApplicationMode> selected, final View.OnClickListener onClickListener, final View[] buttons,
			int i, final boolean singleChoice) {
		if (buttons[i] != null) {
			View tb = buttons[i];
			final ApplicationMode mode = visible.get(i);
			final boolean checked = selected.contains(mode);
			ImageView iv = (ImageView) tb.findViewById(R.id.app_mode_icon);
			if (checked) {
				iv.setImageDrawable(ctx.getIconsCache().getIcon(mode.getSmallIconDark(), R.color.osmand_orange));
				tb.findViewById(R.id.selection).setVisibility(View.VISIBLE);
			} else {
				iv.setImageDrawable(ctx.getIconsCache().getContentIcon(mode.getSmallIconDark()));
				tb.findViewById(R.id.selection).setVisibility(View.INVISIBLE);
			}
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean isChecked = !checked;
					if (singleChoice) {
						if (isChecked) {
							selected.clear();
							selected.add(mode);
						}
					} else {
						if (isChecked) {
							selected.add(mode);
						} else {
							selected.remove(mode);
						}
					}
					if (onClickListener != null) {
						onClickListener.onClick(null);
					}
					for(int i = 0; i < visible.size(); i++) {
						updateButtonState(ctx, visible, selected, onClickListener, buttons, i, singleChoice);
					}
				}
			});
		}
	}


	static private View createToggle(LayoutInflater layoutInflater, OsmandApplication ctx, LinearLayout layout, ApplicationMode mode){
		int metrics = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TOGGLE_SIZE, ctx.getResources().getDisplayMetrics());
		View tb = layoutInflater.inflate(R.layout.mode_view, null);		
		tb.findViewById(R.id.app_mode_icon).setContentDescription(mode.toHumanString(ctx));
		ImageView iv = (ImageView) tb.findViewById(R.id.app_mode_icon);
		iv.setImageDrawable(ctx.getIconsCache().getIcon(mode.getSmallIconDark(), R.color.osmand_orange));
//		tb.setCompoundDrawablesWithIntrinsicBounds(null, ctx.getIconsCache().getIcon(mode.getIconId(), R.color.app_mode_icon_color), null, null);
		LayoutParams lp = new LinearLayout.LayoutParams(metrics, metrics);
//		lp.setMargins(left, 0, 0, 0);
		layout.addView(tb, lp);
		return tb;
	}
}
