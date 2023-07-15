package ca.uwaterloo.cs446.journeytogether.common;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class InAppNotice {

    View view;

    public InAppNotice(View view) {
        this.view = view;
    }

    public void onSuccess(String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void onError(String message) {

        Snackbar snackbar = Snackbar.make(
                view, String.format("Error: %s", message), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
