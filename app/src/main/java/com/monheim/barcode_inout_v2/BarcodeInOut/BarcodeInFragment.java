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

import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;
import com.monheim.barcode_inout_v2.R;

import MssqlCon.PublicVars;

public class BarcodeInFragment extends Fragment {
    BarcodeInOutFunctions barInOut = new BarcodeInOutFunctions();
    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();

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

        if(barInOut.CheckTempBarTranData() == true) {
            btnSave.setEnabled(true);
        }else {
            btnSave.setEnabled(false);
        }

        etBarcode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String barcode = etBarcode.getText().toString();
                String uom = spUom.getSelectedItem().toString();
                Integer qty = Integer.parseInt(etBarcodeQty.getText().toString());

                if (barInOut.GetSapCode(barcode,tvSapCode,tvDesc) == true) {
                    barInOut.InsertIn(barcode,uom,qty,"IN");
                } else {
                    newBarFunc.CheckUnknownBarcode(barcode);
                }

                if(barInOut.CheckTempBarTranData() == true) {
                    PublicVars.GetNav().getMenu().findItem(R.id.barcodeOut).setEnabled(false); //disable Barcode Out in Menu if tempbarcode has data of IN
                    btnSave.setEnabled(true);
                }

                etBarcode.setText("");
                etBarcodeQty.setText("1");
                etBarcode.post(() -> etBarcode.requestFocus()); //focus request
                return true;
            }

            return false;
        });

        etBarcodeQty.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) || keyCode == KeyEvent.KEYCODE_ENTER)) {

                etBarcode.post(() -> etBarcode.requestFocus()); //focus request
                return true;
            }
            return false;
        });

        BarcodeInOutSaveFragment barSaveFrag = new BarcodeInOutSaveFragment();
        btnSave.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,barSaveFrag).commit();
        });

        return rootView;
    }
}