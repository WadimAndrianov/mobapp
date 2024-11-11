package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.CartProductAdapter;
import com.example.myapplication.Adapters.DeliveryProductAdapter;
import com.example.myapplication.models.ProductsInCart;
import com.example.myapplication.models.ProductsInDelivery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeliveryUserActivity extends AppCompatActivity {

    private DeliveryProductAdapter deliveryProductAdapter;

    ArrayList<ProductsInDelivery> ProductsList = new ArrayList<>();

    Button adminPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_user);
        adminPhone = findViewById(R.id.button_copy_phone);
        fillAdminPhone();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Доставка");
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerViewUserDelivery);
        deliveryProductAdapter = new DeliveryProductAdapter(this, ProductsList);
        recyclerView.setAdapter(deliveryProductAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillProductList(); // Заполняем список товаров
    }

    private void fillAdminPhone(){
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        db.child("Admins").child("gI6EpyzZ6CYwBMc7GmQOcoPLlui1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminPhone.setText(snapshot.child("phone").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeliveryUserActivity.this, "Не удалось получить номер администратора", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fillProductList() {
        ProductsList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String userUid = FirebaseAuth.getInstance().getUid();
        db.child("Users").child(userUid).child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot deliverySnapshot : snapshot.getChildren()) {
                    for(DataSnapshot productSnapshot : deliverySnapshot.getChildren()) {
                        String productCode = productSnapshot.getKey();
                        String productName = productSnapshot.child("product_name").getValue(String.class);
                        if(productName != null) {
                            String productPrice = productSnapshot.child("product_price").getValue(String.class);
                            String productDescription = productSnapshot.child("product_description").getValue(String.class);
                            String imageUri = productSnapshot.child("product_image").getValue(String.class);
                            String quantity = productSnapshot.child("product_quantity").getValue(String.class);

                            String address = productSnapshot.child("delivery_address").getValue(String.class);
                            ProductsInDelivery product = new ProductsInDelivery(productCode, productName, productPrice, productDescription, imageUri, quantity, address);
                            ProductsList.add(product);
                        }
                    }
                }
                // Оповещаем адаптер о том, что данные изменились
                deliveryProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeliveryUserActivity.this, "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void btnCopyDeliveryPhoneNumber(View v){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Phone Number", ((Button) v).getText());

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Номер телефона " + ((Button) v).getText() + " скопирован", Toast.LENGTH_SHORT).show();
    }


}