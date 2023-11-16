package com.monheim.barcode_inout_v2.ItemList;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.R;
import com.monheim.barcode_inout_v2.ViewInventory.ViewInventoryFragment;
import com.monheim.barcode_inout_v2.ViewInventory.ViewInventoryFunctions;

import java.util.List;
import java.util.Map;

import MssqlCon.PublicVars;

public class ItemListFragment extends Fragment {
    ItemListFunction itemListFunction = new ItemListFunction();

    SimpleAdapter simAd;
    ListView lvItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        EditText etItems = rootView.findViewById(R.id.etViewItems);
        Button btItems = rootView.findViewById(R.id.btnViewItems);
        lvItems = rootView.findViewById(R.id.lvViewItems);

        List<Map<String, String>> dataList;
        dataList = itemListFunction.GetItemList();

        ListItems(dataList);

        btItems.setOnClickListener(v -> {
            String searchItem = etItems.getText().toString();
            List<Map<String, String>> searchDataList;
            searchDataList = itemListFunction.SearchItem(searchItem);
            ListItems(searchDataList);
        });

        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            TextView tvBarcode = view.findViewById(R.id.barcode);
            TextView tvSolomon = view.findViewById(R.id.solomonID);
            TextView tvUom = view.findViewById(R.id.uom);

            String barcode = tvBarcode.getText().toString();
            String solomonID = tvSolomon.getText().toString();
            String uom = tvUom.getText().toString();

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
                            ViewInventoryFunctions viewInvtFunc = new ViewInventoryFunctions();
                            if(!viewInvtFunc.CheckVoidUser(adminPass)) {
                                Toast.makeText(getActivity(), "Incorrect admin password.", Toast.LENGTH_SHORT).show();
                            } else {
                                if(itemListFunction.RemoveItem(barcode, solomonID, uom)) {
                                    Toast.makeText(getActivity(), "Item Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                    List<Map<String, String>> finalDataList;
                                    finalDataList = itemListFunction.GetItemList();
                                    ListItems(finalDataList);
//                                    viewInvtFunc.GetTotCs(tvTotCs, tvTotPcs, refNbr, user);
                                } else {
                                    Toast.makeText(getActivity(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
        });

        return  rootView;
    }

    private void ListItems(List<Map<String, String>> dataList) {

        String[] from = {"barcode", "solomonID", "description", "uom"};
        int[] to = {R.id.barcode, R.id.solomonID, R.id.description, R.id.uom};
        simAd = new SimpleAdapter(getActivity(), dataList, R.layout.temp_item_list, from, to);
        lvItems.setAdapter(simAd);
    }
}