package com.example.ecom.controllers.databinders;

import android.content.Context;

import android.view.View;


import com.example.ecom.controllers.AdapterCallbacksListener;
import com.example.ecom.databinding.DialogWeightPickerBinding;
import com.example.ecom.databinding.ItemWbProductBinding;
import com.example.ecom.dialogs.WeightPickerDialog;
import com.example.ecom.models.Cart;
import com.example.ecom.models.Product;


public class WBProductBinder {

    private Context context;
    private Cart cart;
    private AdapterCallbacksListener listener;



    public WBProductBinder(Context context, Cart cart, AdapterCallbacksListener listener){
        this.context = context;
        this.cart = cart;
        this.listener = listener;
    }

   public void bind(ItemWbProductBinding b, Product product, int position){


        b.productsName.setText(product.name);
        b.productPrice.setText(String.format("₹%.2f/kg",product.pricePerKg));
        b.imageViewWB.setImageResource(product.imageURL);

        buttonEventHandlers(b,product,position);

        checkWbProductInCart(b,product);
    }

    public void checkWbProductInCart(ItemWbProductBinding b, Product product) {
        if(cart.cartItems.containsKey(product.name)){
            b.nonZeroQtyGroupWB.setVisibility(View.VISIBLE);
            b.addBtn.setVisibility(View.GONE);
            b.qty.setText(cart.cartItems.get(product.name).qty + "Kg");
        }
        else{
            b.nonZeroQtyGroupWB.setVisibility(View.GONE);
            b.addBtn.setVisibility(View.VISIBLE);
        }

    }

    private void buttonEventHandlers(ItemWbProductBinding b,Product product,int position) {
       b.addBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                showDialog(product,position);
           }
       });

       b.editBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showDialog(product,position);
           }
       });
    }

    private void showDialog(Product product, int position) {
        new WeightPickerDialog(context,cart,position,product,listener).show();
    }

}
