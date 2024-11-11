package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }


    public void btnRegistration(View v){
        if(!validateInputs()){
            return;
        }
        CreateAccountInAuthentication();
    }

    private boolean validateInputs() {
        if(binding.editTextTextEmailAddressRegisr.getText().toString().isEmpty() ||
                binding.editTextTextPasswordRegistr.getText().toString().isEmpty() ||
                binding.editTextTextUsername.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextTextEmailAddressRegisr.getText().toString()).matches()) {
            Toast.makeText(getApplicationContext(), "Некорректный формат email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editTextTextPasswordRegistr.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(binding.editTextTextUsername.getText().toString().equals("admin")){
            Toast.makeText(getApplicationContext(), "Имя не доступно", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void CreateAccountInAuthentication() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.editTextTextEmailAddressRegisr.getText().toString(),
                        binding.editTextTextPasswordRegistr.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        createRecordInDatabase(userUid);
                        editor.putBoolean("isAdmin", false); // Пользователь обычный
                        editor.apply();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Ошибка при создании аккаунта (код 3)", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createRecordInDatabase(String userUid){
        try {
            DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("Users").child(userUid).exists()) {
                        HashMap<String, Object> userInfo = new HashMap<>();
                        userInfo.put("email", binding.editTextTextEmailAddressRegisr.getText().toString());
                        userInfo.put("username", binding.editTextTextUsername.getText().toString());

                        // Используем UID как ключ для записи
                        db.child("Users").child(userUid).updateChildren(userInfo)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Аккаунт успешно создан", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ошибка при записи данных в базу (код 1)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Ошибка при запросе базы данных (код 2)", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (DatabaseException e) {
            Toast.makeText(getApplicationContext(), "Ошибка подключения к базе данных", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
            return;
        }
    }


    public void bntBack_to_login(View v){
        startActivity(new Intent(this, LoginActivity.class));
    }
}