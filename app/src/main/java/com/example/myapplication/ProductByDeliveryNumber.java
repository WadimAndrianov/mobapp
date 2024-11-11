package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.DeliveryProductAdapter;
import com.example.myapplication.models.ProductsInDelivery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductByDeliveryNumber extends AppCompatActivity {

    private DeliveryProductAdapter deliveryProductAdapter;

    ArrayList<ProductsInDelivery> ProductsList = new ArrayList<>();

    private String deliveryNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_delivery_number);
        deliveryNumber = getIntent().getExtras().get("deliverynumber").toString();

        RecyclerView recyclerView = findViewById(R.id.recyclerProductsByDeliveryNumber);
        deliveryProductAdapter = new DeliveryProductAdapter(this, ProductsList);
        recyclerView.setAdapter(deliveryProductAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillProductList(deliveryNumber); // Заполняем список товаров
    }

    private void fillProductList(String deliveryNumber) {
        ProductsList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        // Ищем среди всех пользователей
        db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Получаем телефон пользователя
                    String userPhone = userSnapshot.child("phone").getValue(String.class);

                    // Ищем по deliveryNumber для каждого пользователя
                    DataSnapshot deliverySnapshot = userSnapshot.child("Delivery").child(deliveryNumber);
                    if (deliverySnapshot.exists()) {
                        // Если доставка найдена, перебираем товары
                        for (DataSnapshot productSnapshot : deliverySnapshot.getChildren()) {
                            String productCode = productSnapshot.getKey();
                            String productName = productSnapshot.child("product_name").getValue(String.class);
                            if (productName != null) { // Исключаем не связанные с продуктами поля
                                String productPrice = productSnapshot.child("product_price").getValue(String.class);
                                String productDescription = productSnapshot.child("product_description").getValue(String.class);
                                String imageUri = productSnapshot.child("product_image").getValue(String.class);
                                String quantity = productSnapshot.child("product_quantity").getValue(String.class);

                                // Вместо адреса используем телефон
                                String contactInfo = "Контакт клиента: " + userPhone;
                                ProductsInDelivery product = new ProductsInDelivery(productCode, productName, productPrice, productDescription, imageUri, quantity, contactInfo);
                                ProductsList.add(product);
                            }
                        }

                        // Оповещаем адаптер о том, что данные изменились
                        deliveryProductAdapter.notifyDataSetChanged();
                        break; // Прерываем цикл, как только нашли нужную доставку
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки
                Toast.makeText(ProductByDeliveryNumber.this, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

}