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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.MyViewHolder2> {

    Context context;
    ArrayList<ProductsInCart> ProductList;
    CartUpdateListener cartUpdateListener;
    public CartProductAdapter(Context context, ArrayList<ProductsInCart> ProductList,  CartUpdateListener Listener){
        this.context = context;
        this.ProductList = ProductList;
        this.cartUpdateListener = Listener;
    }

    @NonNull
    @Override
    public CartProductAdapter.MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_cart_products, parent, false);
        return new CartProductAdapter.MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductAdapter.MyViewHolder2 holder, int position) {
        ProductsInCart product = ProductList.get(position);

        holder.product_name.setText(product.getProduct_name());
        holder.product_description.setText(product.getProduct_description());
        holder.product_price.setText(product.getProduct_price() + " BYN");
        holder.copy_code.setText(product.getProduct_code());
        holder.quantity.setText(product.getQuantity());

        //Загрузка изображения с помощью Glide
        Glide.with(context)
                .load(product.getImageUri())  // Здесь должно быть URL-изображение из продукта
                .placeholder(R.drawable.photo_produkt)  // Замена на случай отсутствия изображения
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24))) // Закругление углов
                .into(holder.image);

        holder.increment_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                String userUid = FirebaseAuth.getInstance().getUid();

                db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String quantityString = snapshot.child("quantity").getValue(String.class);
                        Integer currentQuantity = Integer.parseInt(quantityString);
                        db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).child("quantity").setValue(String.valueOf(currentQuantity + 1));
                        holder.quantity.setText(String.valueOf(currentQuantity + 1));
                        ProductList.get(position).setQuantity(String.valueOf(currentQuantity + 1));
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onCartUpdated(); // Notify the listener
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.decrement_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                String userUid = FirebaseAuth.getInstance().getUid();

                db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!holder.quantity.getText().toString().equals("1")) {
                            String quantityString = snapshot.child("quantity").getValue(String.class);
                            Integer currentQuantity = Integer.parseInt(quantityString);
                            db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).child("quantity").setValue(String.valueOf(currentQuantity - 1));
                            holder.quantity.setText(String.valueOf(currentQuantity - 1));
                            ProductList.get(position).setQuantity(String.valueOf(currentQuantity - 1));
                            if (cartUpdateListener != null) {
                                cartUpdateListener.onCartUpdated(); // Notify the listener
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Обработка клика по кнопке "Копировать код товара"
        holder.copy_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Копирование кода продукта в буфер обмена
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Product Code", product.getProduct_code());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Код товара скопирован: " + product.getProduct_code(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.delete_from_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                String userUid = FirebaseAuth.getInstance().getUid();

                db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).removeValue();

                        // Обновление UI (например, удаление элемента из списка)
                        ProductList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, ProductList.size());
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onCartUpdated(); // Notify the listener
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.ProductList.size();
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder{
        ImageView image;
        TextView product_name, product_description, product_price, quantity;
        Button increment_quantity, decrement_quantity, copy_code;
        ImageButton delete_from_cart;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_inCart_image);
            product_name = itemView.findViewById(R.id.item_inCart_ProductName);
            product_description = itemView.findViewById(R.id.item_inCart_ProductDescription);
            product_price = itemView.findViewById(R.id.item_inCart_ProductPrice);
            quantity = itemView.findViewById(R.id.quantity_products);
            copy_code = itemView.findViewById(R.id.button_copy_code_product_in_cart);
            delete_from_cart = itemView.findViewById(R.id.button_delete_product_from_cart);

            increment_quantity = itemView.findViewById(R.id.button_increment_quantity);
            decrement_quantity = itemView.findViewById(R.id.button_decremtnt_quantity);

        }
    }
}
