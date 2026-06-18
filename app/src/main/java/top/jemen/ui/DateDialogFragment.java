package top.jemen.ui;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.DatePickerDialog;
public class DateDialogFragment extends DialogFragment{
	private Calendar calendar;
	private OnDateSetListener listener;
	
	public DateDialogFragment(Calendar calendar,OnDateSetListener listener) {
		super();
		if(null!=calendar) {
			this.calendar=calendar;
		}else {
			this.calendar=Calendar.getInstance();
		}
		this.listener=listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new DatePickerDialog(getActivity(), listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}
}
