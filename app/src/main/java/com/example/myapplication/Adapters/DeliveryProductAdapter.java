package com.example.myapplication.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.CartUpdateListener;
import com.example.myapplication.R;
import com.example.myapplication.models.ProductsInCart;
import com.example.myapplication.models.ProductsInDelivery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeliveryProductAdapter extends RecyclerView.Adapter<DeliveryProductAdapter.MyViewHolder3> {
    Context context;
    ArrayList<ProductsInDelivery> ProductList;
    public DeliveryProductAdapter(Context context, ArrayList<ProductsInDelivery> ProductList){
        this.context = context;
        this.ProductList = ProductList;
    }

    @NonNull
    @Override
    public DeliveryProductAdapter.MyViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_delivery_products, parent, false);
        return new DeliveryProductAdapter.MyViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryProductAdapter.MyViewHolder3 holder, int position) {
        ProductsInDelivery product = ProductList.get(position);

        holder.product_name.setText(product.getProduct_name());
        holder.product_description.setText(product.getProduct_description());
        holder.productS_price.setText(product.getProduct_price() + " BYN");
        holder.AddressDelivery.setText(product.getAddress());
        holder.quantity.setText(product.getQuantity());

        //Загрузка изображения с помощью Glide
        Glide.with(context)
                .load(product.getImageUri())  // Здесь должно быть URL-изображение из продукта
                .placeholder(R.drawable.photo_produkt)  // Замена на случай отсутствия изображения
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24))) // Закругление углов
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return this.ProductList.size();
    }

    public static class MyViewHolder3 extends RecyclerView.ViewHolder{
        ImageView image;
        TextView product_name, product_description, productS_price, quantity, AddressDelivery;

        public MyViewHolder3(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.ImageProductDelivery);
            product_name = itemView.findViewById(R.id.NameProductDelivery);
            product_description = itemView.findViewById(R.id.DescriptionProductDelivery);//
            productS_price = itemView.findViewById(R.id.PriceProductDelivery); //
            quantity = itemView.findViewById(R.id.QuantityProductDelivery);//
            AddressDelivery = itemView.findViewById(R.id.AddressProductDelivery);//

        }
    }
}
