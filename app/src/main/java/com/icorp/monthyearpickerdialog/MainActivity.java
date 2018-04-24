package com.icorp.monthyearpickerdialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MonthYearPickerDialog.createWithArguments(2013, 2018).show(getSupportFragmentManager(), getClass().getName(), new MonthYearPickerDialog.OnDatePickListener() {
            @Override
            public void onValuesPicked(int year, int month) {

                Calendar pickedCalendar = Calendar.getInstance();

                pickedCalendar.set(Calendar.YEAR, year);
                pickedCalendar.set(Calendar.MONTH, month);

                int actualMaximum = pickedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                Log.d(TAG, "year = " + year + ", month = " + month + ", actualMaximum = " + actualMaximum);

                int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                DateFormat dateFormat = new SimpleDateFormat(year == currentYear ? "MMMM" : "MMMM yyyy", Locale.US);

                String monthName = dateFormat.format(pickedCalendar.getTime());

                ((TextView) findViewById(R.id.date_text_view)).setText(monthName);

            }
        });
    }
}
