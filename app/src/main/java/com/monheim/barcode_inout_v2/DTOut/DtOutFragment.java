package com.monheim.barcode_inout_v2.DTOut;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import MssqlCon.Logs;
import MssqlCon.PublicVars;

public class DtOutFragment extends Fragment {
    SimpleAdapter simAd;
    TextView tvTotCs, tvTotCaseShot, tvTotPcs, tvTotPcsShot;
    DtOutFunctions dtOutFunc = new DtOutFunctions();
    String dt = "", uom, solomonID,user ,searchDate ="";
    int qty;
    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();
    BarcodeInOutFunctions barInOut = new BarcodeInOutFunctions();
    PublicVars pubVars = new PublicVars();
    Spinner spInvtUom;
    ListView lvDTOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dt_out, container, false);

        EditText searchDtDate = rootView.findViewById(R.id.etSearchDtDate);
        Spinner spinDt = rootView.findViewById(R.id.spinDT);
        lvDTOut = rootView.findViewById(R.id.lvDTOut);
        EditText etDtOutBarcode = rootView.findViewById(R.id.etDtOutBarcode);
        EditText etDtOutQty = rootView.findViewById(R.id.etDtOutQty);
        tvTotCs = rootView.findViewById(R.id.tvTotCase);
        tvTotCaseShot = rootView.findViewById(R.id.tvTotCaseShot);
        tvTotPcs = rootView.findViewById(R.id.tvTotPcs);
        tvTotPcsShot = rootView.findViewById(R.id.tvTotPcsShot);
        spInvtUom = rootView.findViewById(R.id.spInvtUom);

        user = pubVars.GetUser();

        //Uncomment Cabrera
        //etDtOutQty.setEnabled(false);

//        spInvtUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).toString().equals("PCS")) {
//                    etDtOutQty.setEnabled(true);
//                } else {
//                    etDtOutQty.setText("1");
//                    etDtOutQty.setEnabled(false);
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        searchDtDate.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int inputLength;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting) {
                    return;
                }
                inputLength = s.length();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) {
                    return;
                }
                int newLength = s.length();
                String digits = s.toString().replaceAll("[^\\d]", "");
                if (newLength != inputLength && digits.length() < 8) {
                    isFormatting = true;
                    if (digits.length() >= 4) {
                        if (digits.length() == 4 || digits.charAt(4) != '-') {
                            searchDtDate.setText(String.format("%s-%s", digits.substring(0, 4), digits.substring(4)));
                            searchDtDate.setSelection(searchDtDate.getText().length());
                        }
                    }
                    if (digits.length() >= 7) {
                        if (digits.length() == 7 || digits.charAt(7) != '-') {
                            searchDtDate.setText(String.format("%s-%s-%s", digits.substring(0, 4), digits.substring(4, 6), digits.substring(6)));
                            searchDtDate.setSelection(searchDtDate.getText().length());
                        }
                    }
                    isFormatting = false;
                }

                searchDate = searchDtDate.getText().toString();

                if (searchDate.length() == 10) {
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, dtOutFunc.GetDt(searchDate));
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinDt.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }
                if (s.length() > 10) {
                    isFormatting = true;
                    s.delete(10, s.length());
                    isFormatting = false;
                }
            }
        });
        spinDt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dt = parent.getItemAtPosition(position).toString();
                ListBarcodeTran(lvDTOut);
                etDtOutBarcode.requestFocus();
                DisplayTotCs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etDtOutBarcode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String barcode = etDtOutBarcode.getText().toString();
                qty = Integer.parseInt(etDtOutQty.getText().toString());
                uom = spInvtUom.getSelectedItem().toString();
                solomonID = dtOutFunc.GetSolomonID(barcode, dt, uom);

                if (Objects.equals(solomonID, "NA")) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("WARNING!")
                            .setMessage("Item not found! Item barcode is added in New Barcode tab")
                            .setPositiveButton("OK",(dialog, which) -> {
                            })
                            .show();
                    newBarFunc.CheckUnknownBarcode(barcode, user);
                } else if (Objects.equals(solomonID, "NAUOM")){
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("WARNING!")
                            .setMessage("Please select correct UOM")
                            .setPositiveButton("OK",(dialog, which) -> {
                            })
                            .show();
                } else if (Objects.equals(solomonID, "NAITEM")){
                    List<Map<String, String>> dataList;
                    dataList = barInOut.GetMultiBarcode(barcode);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Detected multiple solomon ID");
                    builder.setMessage("Please select appropriate solomon ID");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.setPositiveButton("Save", (dialog, which) -> {
                        UpdateDt(barcode);
                    });

                    ListView listView = new ListView(getContext());

                    String[] from = {"barcode","description","solomonID"};
                    int[] to = {R.id.barcode,R.id.description,R.id.qty};
                    simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
                    listView.setAdapter(simAd);

                    listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    builder.setView(listView);

                    final AlertDialog dialog = builder.create();

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            TextView tvItemDesc = parent.getChildAt(i).findViewById(R.id.description);
                            TextView tvSolomonID = parent.getChildAt(i).findViewById(R.id.qty);

                            if (i == position) {
                                tvItemDesc.setTextColor(Color.RED);
                                tvSolomonID.setTextColor(Color.RED);

                                solomonID = tvSolomonID.getText().toString();
                            } else {
                                tvItemDesc.setTextColor(Color.BLACK);
                                tvSolomonID.setTextColor(Color.BLACK);
                            }
                        }
                    });

                    dialog.show();
                } else {
                    UpdateDt(barcode);
                }

                etDtOutQty.setText("1");
                etDtOutBarcode.setText("");
                etDtOutBarcode.post(() -> etDtOutBarcode.requestFocus()); //focus request

                return true;
            }
            etDtOutBarcode.post(() -> etDtOutBarcode.requestFocus()); //focus request
            return false;
        });
        etDtOutQty.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT)) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); // close keyboard
            }
            return false;
        });
        return  rootView;
    }

    private void UpdateDt(String barcode){
        if(dtOutFunc.GetLastQty(searchDate,dt,solomonID,uom)) {
            if (dtOutFunc.UpdateDtItem(qty, uom, barcode)){
                DisplayTotCs();
                ListBarcodeTran(lvDTOut);
                //log.InsertUserLog("DT-OUT","Update :" + solomonID + " : " + spInvtUom.getSelectedItem().toString() + " : " + qty + " : " + dt);
            } else {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("WARNING!")
                        .setMessage("You have reached the maximum QTY.")
                        .setPositiveButton("OK",(dialog, which) -> {
                        })
                        .show();
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("WARNING!")
                    .setMessage("Item not listed in this DT")
                    .setPositiveButton("OK",(dialog, which) -> {
                    })
                    .show();
        }
    }

    private void ListBarcodeTran(ListView lvDTOut) {
        List<Map<String, String>> dataList;
        dataList = dtOutFunc.GetDTList(dt, searchDate);
        DisplayTotCs();

        String[] from = {"timeStamp","solomonID","description","barcode","qtyOg","uomOg","qtyOut"};
        int[] to = {R.id.id,R.id.description,R.id.itemDescription,R.id.barcode,R.id.maxQty,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.dt_barcode_tran_list_template,from,to);
        lvDTOut.setAdapter(simAd);
    }

    private void DisplayTotCs(){
        dtOutFunc.GetTotCs(dt, searchDate, tvTotCs,tvTotCaseShot);
        dtOutFunc.GetTotPcs(dt, searchDate, tvTotPcs,tvTotPcsShot);
    }
}