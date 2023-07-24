package ca.uwaterloo.cs446.journeytogether.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.LocalDateTime;
import java.util.Calendar;

public class DateTimePickerButton extends AppCompatButton {

    private LocalDateTime selectedDateTime;
    private FragmentManager fragmentManager;

    public DateTimePickerButton(Context context) {
        super(context);
        initialize();
    }

    public DateTimePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DateTimePickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public LocalDateTime getDateTime() {
        return selectedDateTime;
    }

    private void initialize() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
    }

    private void showDateTimePicker() {
        if (fragmentManager == null) {
            throw new IllegalStateException("FragmentManager must be set before calling showDateTimePicker.");
        }

        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDateTime = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
                    showTimePicker();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show(fragmentManager, "DatePickerDialog");
    }

    private void showTimePicker() {
        if (fragmentManager == null) {
            throw new IllegalStateException("FragmentManager must be set before calling showTimePicker.");
        }

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    selectedDateTime = selectedDateTime.withHour(hourOfDay).withMinute(minute);
                    setText(selectedDateTime.toString());
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show(fragmentManager, "TimePickerDialog");
    }
}
