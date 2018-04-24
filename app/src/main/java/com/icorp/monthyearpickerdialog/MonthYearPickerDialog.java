package com.icorp.monthyearpickerdialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthYearPickerDialog extends DialogFragment {

    private static final String MIN_PRESENTED_YEAR_KEY = "minPresentedYear",
            MAX_PRESENTED_YEAR_KEY = "maxPresentedYear";

    private static final String[] MONTHS_LIST = new String[]
            {
                    "January", "February", "March",
                    "April", "May", "June", "July",
                    "August", "September", "October",
                    "November", "December"
            };

    private OnDatePickListener datePickListener;
    private int minPresentedYear, maxPresentedYear, pickedYear, pickedMonth;

    public static MonthYearPickerDialog createWithArguments(int minPresentedYear, int maxPresentedYear) {

        MonthYearPickerDialog monthYearPickerDialog = new MonthYearPickerDialog();

        Bundle bundle = new Bundle();
        bundle.putInt(MIN_PRESENTED_YEAR_KEY, minPresentedYear);
        bundle.putInt(MAX_PRESENTED_YEAR_KEY, maxPresentedYear);

        monthYearPickerDialog.setArguments(bundle);

        return monthYearPickerDialog;
    }

    public MonthYearPickerDialog() {
        setStyle(STYLE_NO_TITLE, R.style.MonthYearPickerDialogTheme);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        if (arguments != null) {

            this.minPresentedYear = arguments.getInt(MIN_PRESENTED_YEAR_KEY, 2013);
            this.maxPresentedYear = arguments.getInt(MAX_PRESENTED_YEAR_KEY, 2018);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        Window window = dialog.getWindow();

        if (window != null)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.month_year_picker_dialog_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NumberPickerWithColorDivider monthPicker = (NumberPickerWithColorDivider) view.findViewById(R.id.month_picker);
        NumberPickerWithColorDivider yearPicker = (NumberPickerWithColorDivider) view.findViewById(R.id.year_picker);

        TextView monthView = (TextView) view.findViewById(R.id.selected_month);
        TextView yearView = (TextView) view.findViewById(R.id.selected_year);

        View monthDivider = view.findViewById(R.id.month_selector_underline);
        View yearDivider = view.findViewById(R.id.year_selector_underline);

        Button cancelButton = view.findViewById(R.id.cancel_pick_button);
        Button pickButton = view.findViewById(R.id.pick_button);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(MONTHS_LIST.length - 1);
        monthPicker.setDisplayedValues(MONTHS_LIST);

        yearPicker.setMinValue(minPresentedYear);
        yearPicker.setMaxValue(maxPresentedYear);

        setCurrentDateIntoViews(monthPicker, yearPicker, monthView, yearView);

        setListeners(monthPicker, yearPicker, monthView, yearView, monthDivider, yearDivider, cancelButton, pickButton);
    }


    private void setCurrentDateIntoViews(NumberPickerWithColorDivider monthPicker,
                                         NumberPickerWithColorDivider yearPicker,
                                         TextView monthView,
                                         TextView yearView) {

        Calendar calendar = Calendar.getInstance();

        pickedMonth = calendar.get(Calendar.MONTH);
        pickedYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.US);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.US);

        monthView.setText(monthFormat.format(calendar.getTime()));
        yearView.setText(yearFormat.format(calendar.getTime()));

        monthPicker.setValue(pickedMonth);
        yearPicker.setValue(pickedYear);
    }

    private void setListeners(final NumberPickerWithColorDivider monthPicker,
                              final NumberPickerWithColorDivider yearPicker,
                              final TextView monthView,
                              final TextView yearView,
                              final View monthDivider,
                              final View yearDivider,
                              Button cancelButton,
                              Button pickButton) {

        monthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectYearOrMonth(yearPicker, monthPicker,
                        monthView, yearView,
                        monthDivider, yearDivider, true);

            }
        });

        yearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectYearOrMonth(yearPicker, monthPicker,
                        monthView, yearView,
                        monthDivider, yearDivider, false);

            }
        });

        monthView.callOnClick();

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (datePickListener != null)
                    datePickListener.onValuesPicked(pickedYear, pickedMonth);

                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldValue, int newValue) {

                pickedMonth = newValue;
                monthView.setText(MONTHS_LIST[newValue]);
            }
        });


        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldValue, int newValue) {

                pickedYear = newValue;
                yearView.setText(String.valueOf(pickedYear));
            }
        });

    }

    private void selectYearOrMonth(NumberPickerWithColorDivider yearPicker,
                                   NumberPickerWithColorDivider monthPicker,
                                   TextView monthView,
                                   TextView yearView,
                                   View monthDivider,
                                   View yearDivider,
                                   boolean isMonthPicked) {

        float hiddenStateAlpha = 0.4f, visibleStateAlpha = 1f;

        yearPicker.setVisibility(isMonthPicked ? View.GONE : View.VISIBLE);
        yearDivider.setVisibility(isMonthPicked ? View.GONE : View.VISIBLE);

        monthPicker.setVisibility(isMonthPicked ? View.VISIBLE : View.GONE);
        monthDivider.setVisibility(isMonthPicked ? View.VISIBLE : View.GONE);

        yearView.setSelected(!isMonthPicked);
        yearView.setAlpha(isMonthPicked ? hiddenStateAlpha : visibleStateAlpha);

        monthView.setSelected(isMonthPicked);
        monthView.setAlpha(isMonthPicked ? visibleStateAlpha : hiddenStateAlpha);

    }

    public void show(FragmentManager manager, String tag, OnDatePickListener datePickListener) {

        this.datePickListener = datePickListener;

        FragmentTransaction transaction = manager.beginTransaction();
        Fragment previousDialog = manager.findFragmentByTag(tag);

        if (previousDialog != null) {
            transaction.remove(previousDialog);
        }

        transaction.addToBackStack(null);
        show(transaction, tag);
    }

    public interface OnDatePickListener {

        void onValuesPicked(int year, int month);
    }
}
