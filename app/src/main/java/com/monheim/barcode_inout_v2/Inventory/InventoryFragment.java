package com.monheim.barcode_inout_v2.Inventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import LocalDb.InventoryTransDbHelper;
import LocalDb.ProductsDbHelper;
import MssqlCon.Logs;
import MssqlCon.PublicVars;

public class InventoryFragment extends Fragment {
    private Button btnSave;

    private TextView tvTotCs, tvTotPcs;
    String currentDate, solomonID;
    SimpleAdapter simAd;
    InventoryFunctions invtFunc = new InventoryFunctions();
    BarcodeInOutFunctions barInOut = new BarcodeInOutFunctions();
    Logs log = new Logs();
    PublicVars pubVars = new PublicVars();
    InventoryTransDbHelper inventoryTransDbHelper;
    ProductsDbHelper productsDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        ListView lvInventoryList = rootView.findViewById(R.id.lvInventoryList);
        EditText etInvtBarcode = rootView.findViewById(R.id.etInvtBarcode);
        EditText etInvtQty = rootView.findViewById(R.id.etInvtQty);
        Spinner spInvtUom = rootView.findViewById(R.id.spInvtUom);
        tvTotCs = rootView.findViewById(R.id.tvTotCs);
        tvTotPcs = rootView.findViewById(R.id.tvTotPcs);
        btnSave = rootView.findViewById(R.id.btnSave);

        String user = pubVars.GetUser();

        inventoryTransDbHelper = new InventoryTransDbHelper(rootView.getContext());
        productsDbHelper = new ProductsDbHelper(rootView.getContext());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        currentDate = dfDate.format(c);

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


                if (barcode.matches("")) {
                    Toast.makeText(getActivity(), "Please scan barcode", Toast.LENGTH_SHORT).show();
                } else {
                    if(PublicVars.GetOfflineMode()) {
                        String solomonID = productsDbHelper.getSolomonID(barcode);

                        System.out.println(solomonID);
                    } else {
                        solomonID = invtFunc.GetSolomonID(barcode);

                        if (Objects.equals(solomonID, "multi")) {
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

                                if (Objects.equals(solomonID, "multi") || Objects.equals(solomonID, "")) {
                                    Toast.makeText(getActivity(), "Please select an item", Toast.LENGTH_SHORT).show();
                                } else {
                                    invtFunc.InsertTempBarcode(barcode, uom, qty, solomonID);

                                    invtFunc.GetToTQtyCs(tvTotCs);
                                    invtFunc.GetToTQtyPcs(tvTotPcs);

                                    etInvtQty.setText("1");
                                    BarcodeList(lvInventoryList);
                                    etInvtBarcode.setText("");
                                    spInvtUom.setSelection(0, true);
                                    etInvtBarcode.post(() -> etInvtBarcode.requestFocus());
                                }
                            });

                            ListView listView = new ListView(getContext());

                            String[] from = {"barcode", "description", "solomonID"};
                            int[] to = {R.id.barcode, R.id.description, R.id.qty};
                            simAd = new SimpleAdapter(getActivity(), dataList, R.layout.temp_barcode_tran_list_template, from, to);
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
                            invtFunc.InsertTempBarcode(barcode, uom, qty, solomonID);

                            invtFunc.GetToTQtyCs(tvTotCs);
                            invtFunc.GetToTQtyPcs(tvTotPcs);

                            etInvtQty.setText("1");
                            BarcodeList(lvInventoryList);
                            spInvtUom.setSelection(0, true);
                            etInvtBarcode.setText("");
                            etInvtBarcode.post(() -> etInvtBarcode.requestFocus());
                        }
                    }
                }

                return true;
            }

            etInvtBarcode.post(() -> etInvtBarcode.requestFocus());
            return false;
        });

        lvInventoryList.setOnItemLongClickListener((parent, view, position, id) -> { //delete item long tap
            TextView tvID = view.findViewById(R.id.invtBarcode);
            TextView tvUom = view.findViewById(R.id.invtUom);

            String item = tvID.getText().toString();
            String uom = tvUom.getText().toString();

            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Are you sure ?")
                    .setMessage("Do you want to delete this item")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (invtFunc.DeleteItem(item, uom, user)) {
                            Toast.makeText(getActivity(), item + " - Successfully Deleted.", Toast.LENGTH_SHORT).show();
                            invtFunc.GetToTQtyCs(tvTotCs);
                            invtFunc.GetToTQtyPcs(tvTotPcs);
                            log.InsertUserLog("Inventory", "Delete item: " + item + uom);
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
                        }
                        BarcodeList(lvInventoryList);
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

//        lvInventoryList.setOnItemClickListener((parent, view, position, id) -> { //update item qty click
//            TextView tvBar = view.findViewById(R.id.invtBarcode);
//            String itemBar = tvBar.getText().toString();
//
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//            dialogBuilder.setTitle("Update " + itemBar + " quantity?");
//
//            final EditText input = new EditText(getActivity());
//            input.setInputType(InputType.TYPE_CLASS_NUMBER);
//            input.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
//            dialogBuilder.setView(input);
//
//            dialogBuilder.setPositiveButton("UPDATE", (dialog, which) -> {
//                if (input.getText().toString().matches("")) {
//                    Toast.makeText(getActivity(), "Please input qty.", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (invtFunc.UpdateItem(itemBar, Integer.parseInt(input.getText().toString()),currentDate)) {
//                        Toast.makeText(getActivity(), "Item Updated!", Toast.LENGTH_SHORT).show();
//                        BarcodeList(lvInventoryList);
//                    } else {
//                        Toast.makeText(getActivity(), "Failed to update item!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            dialogBuilder.setNegativeButton("CANCEL", (dialog, which) -> {
//               dialog.cancel();
//            });
//            dialogBuilder.show();
//        });

        btnSave.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText etRefNbr = new EditText(getActivity());
            etRefNbr.setHint("Reference number");

            EditText etRemarks = new EditText(getActivity());
            etRemarks.setHint("Remarks");

            layout.addView(etRefNbr);
            layout.addView(etRemarks);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy", Locale.getDefault());
            String currentDateRef = dateFormat.format(c);

            etRefNbr.setText(currentDateRef);
            etRefNbr.setEnabled(false);
            etRemarks.setText(pubVars.GetInvtRemarks());

            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Input reference number")
                    .setView(layout)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String refNbr = currentDateRef;
                        String remarks = etRemarks.getText().toString();

                        pubVars.SetInvtRemarks(remarks);

                        if (invtFunc.InsertInventory(refNbr, remarks)) {
                            log.InsertUserLog("Inventory", refNbr);
                            invtFunc.ClearTempInventory();
                            BarcodeList(lvInventoryList);
                            invtFunc.GetToTQtyCs(tvTotCs);
                            invtFunc.GetToTQtyPcs(tvTotPcs);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Failed to save inventory", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
        });

        return rootView;
    }

    private void BarcodeList(ListView lv) {
        List<Map<String, String>> dataList;
        dataList = invtFunc.GetInventoryBarList();

        String[] from = {"barcode", "solomonID", "description", "uom", "qty"};
        int[] to = {R.id.invtBarcode, R.id.tvSolomonId, R.id.tvDesc, R.id.invtUom, R.id.invtQty};
        simAd = new SimpleAdapter(getActivity(), dataList, R.layout.inventory_barcode_template, from, to);
        lv.setAdapter(simAd);
    }
}