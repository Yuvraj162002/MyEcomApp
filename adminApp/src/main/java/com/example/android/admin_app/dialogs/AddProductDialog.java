package com.example.android.admin_app.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.admin_app.R;
import com.example.android.admin_app.databinding.ProductInfoDialogBinding;
import com.example.android.admin_app.models.Product;
import com.example.android.admin_app.models.ProductType;

import java.util.regex.Pattern;

public class AddProductDialog {
    ProductInfoDialogBinding b;
    public Product product;
    public static final int PRODUCT_ADD = 0, PRODUCT_EDIT = 1;
    int productType;

    public AddProductDialog(int Type) {
        productType = Type;
    }

    public void showDialog(Context context, OnProductAddListener listener) {
        b = ProductInfoDialogBinding.inflate(LayoutInflater.from(context));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(productType == PRODUCT_ADD ? "Add Product" : "Edit Product")
                .setView(b.getRoot())
                .setPositiveButton(productType == PRODUCT_ADD ? "ADD" : "EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkValidity(productType)) {
                            listener.onProductAddedOrEdit(product);
                        } else {
                            Toast.makeText(context, "Invalid Details!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.OnCancelled();
                    }
                })
                .show();
        eventHandlerRadioBtn();
        if (productType == PRODUCT_EDIT) {
            preFillPreviousDetails();
        }

    }

    private void preFillPreviousDetails() {
        b.name.setText(product.name);
        b.radioGroupItemBased.check(product.type == ProductType.TYPE_WB ? R.id.radio_button_weight_based : R.id.radio_button_varient_based);
        if (product.type == ProductType.TYPE_WB) {
            b.pricePerKg.setText(product.pricePerKg + "");
            b.minQuantity.setText("" + product.convertMinQtyToWeight(product.minQuantity));
        } else {
            b.variants.setText(product.variantsString());
        }
    }

    public boolean checkValidity(int Type) {
        String name = b.name.getText().toString().trim();
        if (name.isEmpty()) {
            return false;
        }
        switch (b.radioGroupItemBased.getCheckedRadioButtonId()) {
            case R.id.radio_button_weight_based:
                String pricePerKg = b.pricePerKg.getText().toString().trim();
                String minQty = b.minQuantity.getText().toString().trim();
                if (pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)")) {
                    return false;
                }
                if (Type == PRODUCT_ADD) {
                    product = new Product(name, Integer.parseInt(pricePerKg), extractMinimumQuantity(minQty));
                } else {
                    product.makeWeightProduct(name, Integer.parseInt(pricePerKg), extractMinimumQuantity(minQty));
                }
                return true;

            case R.id.radio_button_varient_based:
                String variants = b.variants.getText().toString().trim();
                if (Type == PRODUCT_ADD) {
                    product = new Product(name);
                } else {
                    product.makeVariantProduct(name);
                }
                return checkVariantsDetails(variants);
        }
        return false;
    }

    private boolean checkVariantsDetails(String variants) {
        if (variants.length() == 0) {
            return false;
        }
        String[] vs = variants.split("\n");
        Pattern pattern = Pattern.compile("^\\w+(\\s|\\w)+,\\d+$");
        for (String variant : vs) {
            if (!pattern.matcher(variant).matches()) {
                return false;
            }
        }
        product.fromVariantStrings(vs);
        return true;
    }


    private float extractMinimumQuantity(String minQty) {
        if (minQty.contains("kg")) {
            return Float.parseFloat(minQty.replace("kg", ""));
        } else {
            return Float.parseFloat(minQty.replace("g", "")) / 1000f;
        }
    }



    private void eventHandlerRadioBtn() {
        b.radioGroupItemBased.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_weight_based) {
                    b.weightBasedRoot.setVisibility(View.VISIBLE);
                    b.variantsRoot.setVisibility(View.GONE);
                } else {
                    b.variantsRoot.setVisibility(View.VISIBLE);
                    b.weightBasedRoot.setVisibility(View.GONE);
                }
            }
        });
    }


    public interface OnProductAddListener{
        void onProductAddedOrEdit(Product product);
        void OnCancelled();
    }
}
