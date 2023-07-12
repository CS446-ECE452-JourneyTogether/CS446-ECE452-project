package ca.uwaterloo.cs446.journeytogether.component;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ca.uwaterloo.cs446.journeytogether.R;

public class DateTimePickerPopup {

    private Context context;
    private PopupWindow popupWindow;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnConfirm;
    private Button btnCancel;
    private LocalDateTime localDateTime;
    private ClosedCallBack lastClosedCallback;

    public DateTimePickerPopup(Context context) {
        this.context = context;
        initPopup();
    }

    private void initPopup() {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_datetime_picker, null);
        datePicker = popupView.findViewById(R.id.datePicker);
        timePicker = popupView.findViewById(R.id.timePicker);
        btnConfirm = popupView.findViewById(R.id.btnConfirm);
        btnCancel = popupView.findViewById(R.id.btnCancel);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                DateTimePickerPopup.this.localDateTime = LocalDateTime.of(year, month, day, hour, minute);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    interface ClosedCallBack {
        void onClosed(LocalDateTime localDateTime);
    }

    public void show(View anchorView, ClosedCallBack closedCallBack) {
        this.lastClosedCallback = closedCallBack;
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        popupWindow.dismiss();
        if (this.lastClosedCallback != null) {
            this.lastClosedCallback.onClosed(this.localDateTime);
        }
    }
}
