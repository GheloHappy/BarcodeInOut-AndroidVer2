package com.monheim.barcode_inout_v2.Inventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    SimpleAdapter simAd;
    InventoryFunctions invtFunc = new InventoryFunctions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        ListView lvInventoryList = rootView.findViewById(R.id.lvInventoryList);
        EditText etInvtBarcode = rootView.findViewById(R.id.etInvtBarcode);
        EditText etInvtQty = rootView.findViewById(R.id.etInvtQty);
        Spinner spInvtUom = rootView.findViewById(R.id.spInvtUom);

        InitDatePicker();
        BarcodeList(lvInventoryList);

        etInvtBarcode.post(() -> etInvtBarcode.requestFocus());
        etInvtQty.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) || keyCode == KeyEvent.KEYCODE_ENTER)) {

                etInvtBarcode.post(() -> etInvtBarcode.requestFocus()); //focus request
                return true;
            }
            return false;
        });
        etInvtBarcode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String barcode = etInvtBarcode.getText().toString();
                String uom = spInvtUom.getSelectedItem().toString();
                int qty = Integer.parseInt(etInvtQty.getText().toString());


                if(barcode.matches("")) {
                    Toast.makeText(getActivity(), "Please scan barcode", Toast.LENGTH_SHORT).show();
                } else {
                    if(invtFunc.CheckBarcode(barcode,GetTodaysDate())) {
                        Toast.makeText(getActivity(), barcode + " is existing!", Toast.LENGTH_SHORT).show();
                    } else {
                        invtFunc.InsertBarcode(barcode, uom, qty, GetTodaysDate());
                    }
                }


                etInvtQty.setText("1");
                BarcodeList(lvInventoryList);
                etInvtBarcode.setText("");
                etInvtBarcode.post(() -> etInvtBarcode.requestFocus());

                return true;
            }

            etInvtBarcode.post(() -> etInvtBarcode.requestFocus());
            return false;
        });
        lvInventoryList.setOnItemLongClickListener((parent, view, position, id) -> { //delete item long tap
            TextView tvID = view.findViewById(R.id.invtBarcode);
            String item = tvID.getText().toString();

            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Are you sure ?")
                    .setMessage("Do you want to delete this item")
                    .setPositiveButton("Yes",(dialog, which) -> {
                        if (invtFunc.DeleteItem(item, GetTodaysDate())) {
                            Toast.makeText(getActivity(), item + " - Successfully Deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
                        }
                        BarcodeList(lvInventoryList);
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        lvInventoryList.setOnItemClickListener((parent, view, position, id) -> { //update item qty click
            TextView tvBar = view.findViewById(R.id.invtBarcode);
            String itemBar = tvBar.getText().toString();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Update " + itemBar + " quantity?");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            dialogBuilder.setView(input);

            dialogBuilder.setPositiveButton("UPDATE", (dialog, which) -> {
                if (input.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please input qty.", Toast.LENGTH_SHORT).show();
                } else {
                    if (invtFunc.UpdateItem(itemBar, Integer.parseInt(input.getText().toString()),GetTodaysDate())) {
                        Toast.makeText(getActivity(), "Item Updated!", Toast.LENGTH_SHORT).show();
                        BarcodeList(lvInventoryList);
                    } else {
                        Toast.makeText(getActivity(), "Failed to update item!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialogBuilder.setNegativeButton("CANCEL", (dialog, which) -> {
               dialog.cancel();
            });
            dialogBuilder.show();
        });

        return rootView;
    }
    private void BarcodeList(ListView lv) {
        List<Map<String, String>> dataList;
        dataList = invtFunc.GetInventoryBarList(GetTodaysDate());

        String[] from = {"barcode","uom","qty"};
        int[] to = {R.id.invtBarcode,R.id.invtUom,R.id.invtQty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.inventory_barcode_template,from,to);
        lv.setAdapter(simAd);
    }
    private String GetTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(month,day,year);
    }
    private void InitDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(month, day, year);
                dateButton.setText(date);
                //Toast.makeText(getActivity(),dateButton.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }
    private String makeDateString(int month, int day, int year) {
        return  month + "-" + day + "-" + year;
    }
}