package com.monheim.barcode_inout_v2.BarcodeInOut;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.monheim.barcode_inout_v2.R;

public class BarcodeInFragment extends Fragment {
    BarcodeInOutFunctions barInOut = new BarcodeInOutFunctions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_barcode_in, container, false);

        EditText etBarcode = rootView.findViewById(R.id.etBarcode);
        EditText etBarcodeQty = rootView.findViewById(R.id.etBarcodeQty);
        TextView tvSapCode = rootView.findViewById(R.id.tvSapCode);
        TextView tvDesc = rootView.findViewById(R.id.tvDesc);
        Spinner spUom = rootView.findViewById(R.id.spUom);
        Button btnSave = rootView.findViewById(R.id.btnSave);

        etBarcode.requestFocus();

        barInOut.ClearTempTrans();

        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String barcode = etBarcode.getText().toString();
                    String uom = spUom.getSelectedItem().toString();
                    Integer qty = Integer.parseInt(etBarcodeQty.getText().toString());

                    if (barInOut.GetSapCode(barcode,tvSapCode,tvDesc) == true) {
                        barInOut.InsertIn(barcode,uom,qty,"IN");
                    }

                    etBarcode.setText("");
                    etBarcodeQty.setText("1");
                    etBarcode.requestFocus();
                    return true;
                }
                return false;
            }
        });

        BarcodeInOutSave barSaveFrag = new BarcodeInOutSave();
        btnSave.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,barSaveFrag).commit();
        });

        return rootView;
    }
}