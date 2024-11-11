package com.example.myapplication.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.models.Product;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
    ArrayList<Product> ProductList;

    public ProductAdapter(Context context, ArrayList<Product> ProductList){
    this.context = context;
    this.ProductList = ProductList;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        Product product = ProductList.get(position);

        holder.product_name.setText(product.getProduct_name());
        holder.product_description.setText(product.getProduct_description());
        holder.product_price.setText(product.getProduct_price() + " BYN");
        holder.copy_code.setText(product.getProduct_code());

         //Загрузка изображения с помощью Glide
        Glide.with(context)
                .load(product.getImageUri())  // Здесь должно быть URL-изображение из продукта
                .placeholder(R.drawable.photo_produkt)  // Замена на случай отсутствия изображения
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24))) // Закругление углов
                .into(holder.image);
        // Настраиваем GestureDetector для двойного нажатия на изображение
//        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                // Проверяем текущий масштаб и изменяем его
//                if (holder.scale == 1f) {
//                    holder.scale = 3f; // Увеличиваем
//                    holder.product_price.setTextColor(Color.TRANSPARENT); //transparent - прозрачный
//                    holder.product_name.setTextColor(Color.TRANSPARENT); //transparent - прозрачный
//                    holder.product_description.setTextColor(Color.TRANSPARENT);//transparent - прозрачный
//                } else {
//                    holder.scale = 1f; // Возвращаем к исходному
//                    holder.product_price.setTextColor(ContextCompat.getColor(context, R.color.register)); //@color/register
//                    holder.product_name.setTextColor(Color.BLACK); //BLACK
//                    holder.product_description.setTextColor(Color.BLACK);//BLACK
//                }
//                holder.image.setScaleX(holder.scale);
//                holder.image.setScaleY(holder.scale);
//                return true;
//            }
//        });
//
//        // Устанавливаем слушатель нажатий на изображение для обработки жестов
//        holder.image.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        // Обработка клика по кнопке "Добавить в корзину"
        holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Здесь реализуйте логику добавления товара в корзину
                DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                String userUid = FirebaseAuth.getInstance().getUid();
                db.child("Users").child(userUid).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(product.getProduct_code())){
                            Toast.makeText(context, "Товар уже добавлен в корзину", Toast.LENGTH_SHORT).show();
                        }else {
                            Map<String, Object> productDetails = new HashMap<>();
                            productDetails.put("product_name", product.getProduct_name());
                            productDetails.put("product_description", product.getProduct_description());
                            productDetails.put("product_price", product.getProduct_price());
                            productDetails.put("imageUri", product.getImageUri());
                            productDetails.put("quantity", "1"); // Устанавливаем начальное количество равным 1

                            db.child("Users").child(userUid).child("Cart").child(product.getProduct_code()).setValue(productDetails)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, product.getProduct_name() + " добавлен в корзину", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Ошибка добавления товара в корзину", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

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

    }

    @Override
    public int getItemCount() {
        return this.ProductList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView product_name, product_description, product_price;
        Button add_to_cart, copy_code;
        //float scale = 1f; // начальный масштаб

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_image);
            product_name = itemView.findViewById(R.id.item_name);
            product_description = itemView.findViewById(R.id.item_description);
            product_price = itemView.findViewById(R.id.item_price);
            add_to_cart = itemView.findViewById(R.id.button_add_item_to_cart);
            copy_code = itemView.findViewById(R.id.button_copy_item_code);
        }
    }
}
