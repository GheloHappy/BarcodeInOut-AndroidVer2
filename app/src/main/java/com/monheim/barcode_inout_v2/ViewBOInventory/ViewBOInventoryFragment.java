package com.monheim.barcode_inout_v2.ViewBOInventory;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.Home.HomeFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import MssqlCon.PublicVars;

public class ViewBOInventoryFragment extends Fragment {

    SimpleAdapter simAd;
    ViewBOInventoryFunctions viewInvtFunc = new ViewBOInventoryFunctions();

    PublicVars pubVars = new PublicVars();

    String user = "", refNbr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_b_o_inventory, container, false);

        EditText etSearchRefNbr = rootView.findViewById(R.id.etViewInvtSearchRefNbr);
        ListView lvBarcodeTrans = rootView.findViewById(R.id.lvViewInvtBarcodeTrans);
        Button btnSearch = rootView.findViewById(R.id.btnViewInvtSearch);
        TextView tvTotCs = rootView.findViewById(R.id.tvViewInvtTotCS);
        TextView tvTotPcs = rootView.findViewById(R.id.tvViewInvtTotPcs);

        user = pubVars.GetUser();

        etSearchRefNbr.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnSearch.callOnClick();
                return true;
            }
            return false;
        });

        btnSearch.setOnClickListener(v -> {
            refNbr = etSearchRefNbr.getText().toString();
            if (viewInvtFunc.GetTotCs(tvTotCs, tvTotPcs, refNbr, user) == true) {
                ListInvtTran(lvBarcodeTrans, refNbr);
            } else {
                Toast.makeText(getActivity(), "Reference number not found.", Toast.LENGTH_SHORT).show();
            }
        });

//        lvBarcodeTrans.setOnItemClickListener((parent, view, position, id) -> {
////                TextView tvID = view.findViewById(R.id.barcode);
//            TextView tvUom = view.findViewById(R.id.uom);
//            TextView tvSolomonID = view.findViewById(R.id.tranType);
//
////            String item = tvID.getText().toString();
//            String uom = tvUom.getText().toString();
//            String solomonID = tvSolomonID.getText().toString();
//
//            String total = viewInvtFunc.GetTotItem(refNbr, solomonID, uom);
////            LinearLayout layout = new LinearLayout(getActivity());
////            layout.setOrientation(LinearLayout.VERTICAL);
////
////            TextView textView = new TextView(getActivity());
//
////            textView.setText(viewInvtFunc.GetTotItem(refNbr, user, solomonID, uom));
////
////            layout.addView(textView);
//
//            new AlertDialog.Builder(getActivity())
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("Total Qty of " + solomonID + " = " + total)
////                    .setView(layout)
////                    .setMessage("Total Qty of " + solomonID + " = " + total)
//                    .setPositiveButton("Confirm", null)
//                    .show();
//
//        });

        lvBarcodeTrans.setOnItemLongClickListener((parent, view, position, id) -> {
            TextView tvBarcode = view.findViewById(R.id.barcode);
            TextView tvSolomon = view.findViewById(R.id.tranType);
            TextView tvUom = view.findViewById(R.id.uom);
            TextView tvRemarks = view.findViewById(R.id.remarks);

            String barcode = tvBarcode.getText().toString();
            String solomonID = tvSolomon.getText().toString();
            String uom = tvUom.getText().toString();
            String remarks = tvRemarks.getText().toString();

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText etAdminpass = new EditText(getActivity());
            etAdminpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etAdminpass.setHint("Admin Password");

            layout.addView(etAdminpass);

            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Input Admin Password")
                    .setView(layout)
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("Yes",(dialog, which) -> {
                        String adminPass = etAdminpass.getText().toString();

                        if(adminPass.equals("")) {
                            Toast.makeText(getActivity(), "Please input admin password.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            if(!viewInvtFunc.CheckVoidUser(adminPass)) {
                                Toast.makeText(getActivity(), "Incorrect admin password.", Toast.LENGTH_SHORT).show();
                            } else {
                                if(viewInvtFunc.VoidRemoveItem(barcode, solomonID, uom, user, remarks)) {
                                    Toast.makeText(getActivity(), "Item Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                    ListInvtTran(lvBarcodeTrans, refNbr);
//                                    viewInvtFunc.GetTotCs(tvTotCs, tvTotPcs, refNbr, user);
                                } else {
                                    Toast.makeText(getActivity(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();

            return true;
        });

        return rootView;
    }

    private void ListInvtTran(ListView lvBarcodeTrans, String refNbr) {
        List<Map<String, String>> dataList;
        dataList = viewInvtFunc.GetInvtList(refNbr, user);

        String[] from = {"barcode", "solomonID", "description", "uom", "qty", "remarks"};
        int[] to = {R.id.barcode, R.id.tranType, R.id.description, R.id.uom, R.id.qty, R.id.remarks};
        simAd = new SimpleAdapter(getActivity(), dataList, R.layout.temp_inventory_report, from, to);
        lvBarcodeTrans.setAdapter(simAd);
    }
}