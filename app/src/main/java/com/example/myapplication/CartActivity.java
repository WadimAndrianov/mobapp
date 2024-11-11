package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.CartProductAdapter;
import com.example.myapplication.Adapters.ProductAdapter;
import com.example.myapplication.models.Product;
import com.example.myapplication.models.ProductsInCart;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TotpSecret;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements OnMapReadyCallback, CartUpdateListener {
    Button buy;
    TextView selected_address, cart_price;

    Integer price = 0;

    private CartProductAdapter cartProductAdapter;

    ArrayList<ProductsInCart> ProductsList = new ArrayList<>();

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Корзина");
        }
        cheking_phone_number();

        selected_address = (TextView) findViewById(R.id.delivery_Address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

        RecyclerView recyclerView = findViewById(R.id.RecyclerViewProductsInCart);
        cartProductAdapter = new CartProductAdapter(this, ProductsList, this);
        recyclerView.setAdapter(cartProductAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buy = (Button) findViewById(R.id.button_buy);
        cart_price = findViewById(R.id.total_price);
        fillProductList(); // Заполняем список продуктов
    }

    private void goEnterPhoneData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setTitle("Возможность заказа недоступна")
                .setMessage("Введите номер телефона для того, чтобы мы могли связаться с вами")
                .setCancelable(false)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(CartActivity.this, MeAccountActivity.class));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cheking_phone_number() {
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String userUid = FirebaseAuth.getInstance().getUid();
        db.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("phone")) {
                    goEnterPhoneData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Ошибка загрузки базы данных", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fillProductList() {
        ProductsList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String userUid = FirebaseAuth.getInstance().getUid();
        db.child("Users").child(userUid).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productCode = productSnapshot.getKey();
                    String productName = productSnapshot.child("product_name").getValue(String.class);
                    String productPrice = productSnapshot.child("product_price").getValue(String.class);
                    String productDescription = productSnapshot.child("product_description").getValue(String.class);
                    String imageUri = productSnapshot.child("imageUri").getValue(String.class);
                    String quantity = productSnapshot.child("quantity").getValue(String.class);
                    ProductsInCart product = new ProductsInCart(productCode, productName, productPrice, productDescription, imageUri, quantity);
                    ProductsList.add(product);
                    price += Integer.parseInt(productPrice) * Integer.parseInt(quantity);
                }
                // Оповещаем адаптер о том, что данные изменились
                cartProductAdapter.notifyDataSetChanged();
                cart_price.setText("Сумма заказа: " + String.valueOf(price) + " BYN");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCartUpdated() {
        price = 0; // Reset price
        for (ProductsInCart product : ProductsList) {
            price += Integer.parseInt(product.getProduct_price()) * Integer.parseInt(product.getQuantity());
        }
        cart_price.setText("Сумма заказа: " + price + " BYN");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        LatLng defaultLocation = new LatLng(53.9, 27.56); //Минск
        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Минск"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Добавляем обработчик нажатий на карту
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                // Преобразуем координаты в адрес
                getAddressFromLatLng(latLng);
            }
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0); // Получаем полный адрес
                String country = address.getCountryName();
                if (!country.equals("Беларусь")) {
                    Toast.makeText(CartActivity.this, "Доставка поддерживается только по Беларуси", Toast.LENGTH_SHORT).show();
                } else {
                    gMap.clear();
                    gMap.addMarker(new MarkerOptions().position(latLng).title("Выбранный адрес"));
                    selected_address.setText(addressText);
                }
                // Сохранение адреса в переменную или отправка на сервер
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Не удалось получить адрес", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnBuy(View v) {
        if (selected_address.getText().toString().equals("Выберите адрес доставки") ||
                selected_address.getText().toString().isEmpty()) {
            Toast.makeText(CartActivity.this, "Адрес доставки не выбран", Toast.LENGTH_SHORT).show();
        } else if (price == 0) {
            Toast.makeText(CartActivity.this, "Корзина пуста", Toast.LENGTH_SHORT).show();
        } else {
            String userUid = FirebaseAuth.getInstance().getUid();
            DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();

            // Проверка телефона пользователя
            db.child("Users").child(userUid).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String phone = snapshot.getValue(String.class);
                    if (phone != null && phone.matches("\\+375\\d{9}")) {
                        // Телефон корректный, продолжаем оформление заказа
                        processOrder(userUid, db);
                    } else {
                        // Некорректный или отсутствующий номер телефона
                        Toast.makeText(CartActivity.this, "Некорректный номер телефона. Пожалуйста, обновите данные.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void processOrder(String userUid, DatabaseReference db) {
        Calendar calendar = Calendar.getInstance();
        String CurrentDate, deliveryRandomKey, CurrentTime;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        CurrentDate = currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        CurrentTime = currentTime.format(calendar.getTime());

        deliveryRandomKey = CurrentDate + CurrentTime;

        for (ProductsInCart product : ProductsList) {
            db.child("Users").child(userUid).child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    HashMap<String, Object> productDetails = new HashMap<>();
                    productDetails.put("product_quantity", String.valueOf(product.getQuantity()));
                    productDetails.put("product_price", String.valueOf(Integer.parseInt(product.getProduct_price()) * Integer.parseInt(product.getQuantity())));
                    productDetails.put("product_description", product.getProduct_description());
                    productDetails.put("product_name", product.getProduct_name());
                    productDetails.put("product_image", product.getImageUri());
                    productDetails.put("delivery_address", selected_address.getText().toString());

                    db.child("Users").child(userUid).child("Delivery").child(deliveryRandomKey).child(product.getProduct_code()).setValue(productDetails);
                    db.child("Users").child(userUid).child("Delivery").child(deliveryRandomKey).child("Address").setValue(selected_address.getText().toString());
                    Toast.makeText(CartActivity.this, "Заказ успешно оформлен", Toast.LENGTH_SHORT).show();
                    db.child("Users").child(userUid).child("Cart").removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                }
            });
        }
        ProductsList.clear();
        cartProductAdapter.notifyDataSetChanged();
        cart_price.setText("Сумма заказа: 0 BYN");
    }
}