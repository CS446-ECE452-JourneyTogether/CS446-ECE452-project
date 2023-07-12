package ca.uwaterloo.cs446.journeytogether.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import java.time.LocalDateTime;

public class DateTimePickerButton extends AppCompatButton {

    DateTimePickerPopup dateTimePickerPopup;

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

    public LocalDateTime getDateTime() {
        return this.dateTimePickerPopup.getLocalDateTime();
    }

    private void onPopupClosed() {
        this.setText(getDateTime().toString());
    }

    private void initialize() {
        dateTimePickerPopup = new DateTimePickerPopup(this.getContext());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePickerPopup.show(v, (localDateTime -> { onPopupClosed(); }));
            }
        });
    }
}
