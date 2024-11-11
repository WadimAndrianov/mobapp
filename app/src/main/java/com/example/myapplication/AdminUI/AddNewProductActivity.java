package com.example.myapplication.AdminUI;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.CatalogActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewProductActivity extends AppCompatActivity {
    private String categoryName;
    EditText product_name, product_description, product_price;
    private String productName, productPrice, productDescription, currentDate, currentTime, productRandomKey;
    ImageView productImage;
    private static final int GALLERYPICK = 1;
    private Uri ImageUri;
    private String downloadImageUrl;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductsRef;

    EditText number_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        ActionBar actionBar = getSupportActionBar();
        categoryName = getIntent().getExtras().get("category").toString();
        if (actionBar != null) {
            actionBar.setTitle(categoryName);
        }

        init();

    }

    public void imageClick(View v){
        openGallery();
    }

    private void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERYPICK);// запускает активность выбора изображения, и ожидается результат
        // (например, выбранный файл), который будет обработан в onActivityResult()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // вызывается после
    // завершения действия, которое было запущено с помощью startActivityForResult()
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null){
            //requestCode проверяет, какой запрос был выполнен (в данном случае это GALLERYPICK)
            //проверяет, успешно ли завершено действие (должно быть RESULT_OK).
            //содержит данные, возвращённые активностью (например, выбранное изображение).
            ImageUri = data.getData();
            productImage.setImageURI(ImageUri);
        }
    }

    private void init(){

        product_name = (EditText)findViewById(R.id.product_name);
        product_description = (EditText)findViewById(R.id.product_description);
        product_price = (EditText)findViewById(R.id.product_price);
        productImage = (ImageView)findViewById(R.id.product_image);

        ProductImageRef = FirebaseStorage.getInstance("gs://delivery-of-building-mat-100df.appspot.com").getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Products");

        number_product = (EditText)findViewById(R.id.number_product);
    }

    public void btnPostProduct(View v){
        ValidateProductData();

    }

    private boolean validPrice(String priceInput){
        return (priceInput.matches("\\d+"));
    }

    private void ValidateProductData() {
        productDescription = product_description.getText().toString();
        productPrice = product_price.getText().toString();
        productName = product_name.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Добавьте изображение товара.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productDescription)){
            Toast.makeText(this, "Добавьте описание товара.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productPrice)){
            Toast.makeText(this, "Добавьте стоимость товара.", Toast.LENGTH_SHORT).show();
        }
        else if(!validPrice(product_price.getText().toString())){
            Toast.makeText(this, "Цена не корректна", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productName)){
            Toast.makeText(this, "Добавьте название товара.", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation(){
        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        this.currentDate = currentDate.format(calendar.getTime());


        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        this.currentTime = currentTime.format(calendar.getTime());

        productRandomKey = this.currentDate + this.currentTime;

        final StorageReference filePath = ProductImageRef.child(productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddNewProductActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddNewProductActivity.this, "Изображение успешно загружено.", Toast.LENGTH_SHORT).show();

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AddNewProductActivity.this, "Фото сохранено", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase(){
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("description", productDescription);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", productPrice);
        productMap.put("name", productName);

        ProductsRef.child(categoryName).child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddNewProductActivity.this);
                            builder.setTitle("Товар добавлен")
                                    .setMessage("Код товара: " + productRandomKey)
                                    .setCancelable(false)
                                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            product_name.getText().clear();
                                            product_description.getText().clear();
                                            product_price.getText().clear();
                                            ImageUri = null;
                                            productImage.setImageResource(R.drawable.photo_produkt);
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(AddNewProductActivity.this, "Ошибка: "+ message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void btnClearForm(View v){
        product_name.getText().clear();
        product_description.getText().clear();
        product_price.getText().clear();
    }

    public void btnDeleteProductImage(View v){
        ImageUri = null;
        productImage.setImageResource(R.drawable.photo_produkt);
    }

    public void btnDeleteProduct(View v){
        String numberCode = number_product.getText().toString();
        if(numberCode.length() != 14){
            Toast.makeText(AddNewProductActivity.this, "Неверный формат кода товара", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        db.child("Products").child(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(numberCode)) {
                    db.child("Products").child(categoryName).child(numberCode).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddNewProductActivity.this, "Товар успешно удален из базы данных", Toast.LENGTH_SHORT).show();
                                        StorageReference productImageRef = FirebaseStorage.getInstance("gs://delivery-of-building-mat-100df.appspot.com").getReference().child("Product Images/" + numberCode + ".jpg");
                                        productImageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AddNewProductActivity.this, "Изображение успешно удалено из storage", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AddNewProductActivity.this, "Ошибка при удалении изображения", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AddNewProductActivity.this, "Ошибка при удалении товара", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Товара с таким кодом не существует", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Ошибка при подключении к базе данных", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void btnGoExitFromAddProduct(View v){
        btnDeleteProductImage(v);
        btnClearForm(v);
        startActivity(new Intent(AddNewProductActivity.this, CatalogActivity.class));
    }
}