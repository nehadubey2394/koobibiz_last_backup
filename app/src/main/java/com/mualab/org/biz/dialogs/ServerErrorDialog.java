package com.mualab.org.biz.dialogs;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.mualab.org.biz.R;

/**
 * Created by dharmraj on 28/12/17.
 **/

public class ServerErrorDialog implements View.OnClickListener{

    private Dialog dialog;
    //private Listner listner;

    public ServerErrorDialog(Context context) {
        //this.listner = listner;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_server_maintenance);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        AppCompatButton btnRetry =  dialog.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                View view = dialog.getWindow().getDecorView();
                //for enter from left
                ObjectAnimator.ofFloat(view, "translationX", -view.getWidth(), 0.0f).start();
                //for enter from bottom
                //ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0.0f).start();
            }
        });
    }


    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnRetry:
                dialog.dismiss();
               // listner.onNetworkChange(dialog, ConnectionDetector.isConnected());
                break;
        }
    }

    public interface Listner {
        void onNetworkChange(Dialog dialog, boolean isConnected);
    }
}
