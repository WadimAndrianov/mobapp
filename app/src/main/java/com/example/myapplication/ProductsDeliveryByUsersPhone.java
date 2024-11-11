package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.CartProductAdapter;
import com.example.myapplication.Adapters.ChangeProductsInUsersDeliveryAdapter;
import com.example.myapplication.Adapters.ProductAdapter;
import com.example.myapplication.models.ProductInUserDelivery;
import com.example.myapplication.models.ProductsInCart;
import com.example.myapplication.models.ProductsInDelivery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductsDeliveryByUsersPhone extends AppCompatActivity {

    EditText phoneNumber;
    private ChangeProductsInUsersDeliveryAdapter changeProductsInUsersDeliveryAdapter;

    ArrayList<ProductInUserDelivery> ProductsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_delivery_by_users_phone);
        phoneNumber = (EditText) findViewById(R.id.Phone_number_for_search_deivery);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewUserDeliveryByPhone);
        changeProductsInUsersDeliveryAdapter = new ChangeProductsInUsersDeliveryAdapter(this, ProductsList);
        recyclerView.setAdapter(changeProductsInUsersDeliveryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    public void btnSearchDeliveryByPhoneNumber(View v){
        if(phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(ProductsDeliveryByUsersPhone.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }else{
            fillProductList(phoneNumber.getText().toString());
        }
    }
    private void fillProductList(String phoneNumber){
        ProductsList.clear(); // очищаем список перед новым поиском
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        // Поиск всех пользователей
        db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false; // переменная для проверки нахождения пользователя

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Получаем номер телефона пользователя
                    String userPhone = userSnapshot.child("phone").getValue(String.class);
                    if (userPhone != null && userPhone.equals(phoneNumber)) {
                        userFound = true; // пользователь найден
                        String userUid = userSnapshot.getKey(); // UID найденного пользователя

                        // Поиск всех доставок пользователя
                        DataSnapshot deliverySnapshot = userSnapshot.child("Delivery");
                        for (DataSnapshot delivery : deliverySnapshot.getChildren()) {
                            String deliveryNumber = delivery.getKey(); // Номер доставки

                            // Перебираем все товары в доставке
                            for (DataSnapshot productSnapshot : delivery.getChildren()) {
                                String productCode = productSnapshot.getKey();
                                String productName = productSnapshot.child("product_name").getValue(String.class);
                                if (productName != null) { // проверка на пустые значения
                                    String productPrice = productSnapshot.child("product_price").getValue(String.class);
                                    String productDescription = productSnapshot.child("product_description").getValue(String.class);
                                    String imageUri = productSnapshot.child("product_image").getValue(String.class);
                                    String quantity = productSnapshot.child("product_quantity").getValue(String.class);
                                    String address = productSnapshot.child("delivery_address").getValue(String.class);

                                    // Создаем объект продукта
                                    ProductInUserDelivery product = new ProductInUserDelivery(
                                            address, productName, productPrice, productDescription, imageUri,
                                            quantity, userUid, deliveryNumber, productCode
                                    );

                                    // Добавляем продукт в список
                                    ProductsList.add(product);
                                }
                            }
                        }
                        break; // Прерываем цикл, как только нашли пользователя
                    }
                }

                if (!userFound) {
                    Toast.makeText(ProductsDeliveryByUsersPhone.this, "Пользователь с таким номером телефона не найден", Toast.LENGTH_SHORT).show();
                }

                // Оповещаем адаптер о том, что данные изменились
                changeProductsInUsersDeliveryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsDeliveryByUsersPhone.this, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
            }
        });
    }
}