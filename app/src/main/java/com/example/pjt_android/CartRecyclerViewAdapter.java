package com.example.pjt_android;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pjt_android.com.tje.model.Cart;
import com.example.pjt_android.com.tje.model.Comment;

import java.util.ArrayList;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder> {

    ArrayList<Cart> cartList;

    public CartRecyclerViewAdapter(ArrayList<Cart> cartList) {
        this.cartList = cartList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox cart_checkbox;
        ImageView cart_imageview;
        TextView cart_title;
        EditText cart_number;
        TextView cart_category;
        TextView cart_price;
        TextView cart_add_wrtie;


         public ViewHolder(@NonNull final View itemView){
             super(itemView);

             cart_checkbox=itemView.findViewById(R.id.cart_checkbox);
             cart_imageview=itemView.findViewById(R.id.cart_imageview);
             cart_title=itemView.findViewById(R.id.cart_title);
             cart_number=itemView.findViewById(R.id.cart_number);
             cart_category=itemView.findViewById(R.id.cart_category);
             cart_price=itemView.findViewById(R.id.cart_price);
             cart_add_wrtie=itemView.findViewById(R.id.cart_add_wrtie);
         }
     }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Context context=holder.itemView.getContext();

        final Cart cart=this.cartList.get(position);

        String imgUrl=context.getString(R.string.HOST_NETWORK_PROTOCOL)+
                context.getString(R.string.HOST_ADDRESS)+
                context.getString(R.string.HOST_IMG_URL)+
                cart.getImage();

        holder.cart_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    cart.setSelected(true);
                else
                    cart.setSelected(false);
            }
        });
        holder.cart_checkbox.setChecked(cart.isSelected());

        Glide.with(holder.itemView.getContext()).load(imgUrl).into(holder.cart_imageview);
        holder.cart_title.setText(cart.getTitle());
        holder.cart_category.setText(cart.getCategoryString());
        holder.cart_price.setText(cart.getPrice());
        holder.cart_number.setText(cart.getNumber()+"");

        holder.cart_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    cart.setNumber(Integer.parseInt( holder.cart_number.getText().toString().equals("") ? "0" : holder.cart_number.getText().toString() ));
                    Log.e("position", holder.getAdapterPosition()+"");
                }
                else
                    cart.setNumber(Integer.parseInt( holder.cart_number.getText().toString().equals("") ? "0" : holder.cart_number.getText().toString() ));
            }
        });

        holder.cart_add_wrtie.setText(cart.getAdd_timeString());
    }

    @Override
    public int getItemCount() {
        return this.cartList.size();
    }

    public ArrayList<Cart> getCartList(){
        return this.cartList;
    }

}
