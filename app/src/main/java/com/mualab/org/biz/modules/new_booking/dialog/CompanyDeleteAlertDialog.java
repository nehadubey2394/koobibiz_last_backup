package com.mualab.org.biz.modules.new_booking.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.base.BaseDialog;


public class CompanyDeleteAlertDialog extends BaseDialog implements View.OnClickListener {

    private static final String TAG = CompanyDeleteAlertDialog.class.getSimpleName();

    private CompanyDeleteCallback callback;
    private String businessName = "";


    public CompanyDeleteAlertDialog() {

    }

    public static CompanyDeleteAlertDialog newInstance(String businessName, CompanyDeleteCallback callback) {

        Bundle args = new Bundle();

        CompanyDeleteAlertDialog fragment = new CompanyDeleteAlertDialog();
        fragment.setOnClickListener(businessName, callback);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnClickListener(String businessName, CompanyDeleteCallback callback) {
        this.callback = callback;
        this.businessName = businessName;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvMsg = view.findViewById(R.id.tvMsg);
        tvMsg.setText(getString(R.string.alertCompanyDelStartMsg).concat(" ").concat(businessName == null ? "" : businessName).concat(" ").concat(getString(R.string.alertCompanyDelLastMsg)));
        Button btnYes = view.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(this);
        btnYes.setText(getString(R.string.ok));
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYes:
                dismissDialog(TAG);
                callback.onDone();
                break;
        }
    }

}
