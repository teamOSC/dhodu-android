package com.dhodu.android;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by naman on 04/11/15.
 */
public class CreateOrderDialog extends android.support.v4.app.DialogFragment {

    public static CreateOrderDialog newInstance() {
        CreateOrderDialog f = new CreateOrderDialog();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_create_order, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final LinearLayout slotWashPress = (LinearLayout) rootView.findViewById(R.id.slot1);
        final LinearLayout slotDryClean = (LinearLayout) rootView.findViewById(R.id.slot2);
        ImageButton next = (ImageButton) rootView.findViewById(R.id.next);

        slotWashPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slotWashPress.isSelected())
                    slotWashPress.setSelected(false);
                else slotWashPress.setSelected(true);
            }
        });
        slotDryClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slotDryClean.isSelected())
                    slotDryClean.setSelected(false);
                else slotDryClean.setSelected(true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceType = "";
                if (slotDryClean.isSelected()) serviceType += 2;
                if (slotWashPress.isSelected()) serviceType += 1;
                if (serviceType.length() < 1) {
                    Toast.makeText(getActivity(), "Please select the type of service", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getActivity(), CreateOrderActivity.class);
                    i.putExtra("servicetype", serviceType);
                    startActivity(i);
                }
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
