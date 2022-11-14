package com.monheim.barcode_inout_v2.BarcodeIn;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.R;

public class BarcodeInFragment extends Fragment {
    BarcodeIN barIn = new BarcodeIN();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_barcode_in, container, false);

        EditText etBarcode = rootView.findViewById(R.id.etBarcode);
        TextView tvSapCode = rootView.findViewById(R.id.tvSapCode);
        TextView tvDesc = rootView.findViewById(R.id.tvDesc);

        etBarcode.requestFocus();

        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String barcode = etBarcode.getText().toString();

                    barIn.GetSapCode(barcode,tvSapCode,tvDesc);


                    etBarcode.setText("");
                    etBarcode.requestFocus();
                    return true;
                }
                return false;
            }
        });


        return rootView;
    }
}