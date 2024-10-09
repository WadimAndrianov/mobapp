package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityMeAccountBinding;
import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MeAccountActivity extends AppCompatActivity {

    private ActivityMeAccountBinding binding;

    static boolean isAdmin;

    private String Admin_or_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_me_account);
        binding = ActivityMeAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false); // По умолчанию false
        Admin_or_User = retAdminOrUser();

        dysplayMyInformation();

    }

    private String retAdminOrUser(){
        if(isAdmin)
            return "Admins";
        else
            return "Users";
    }

    private void dysplayMyInformation(){
        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String userUid = FirebaseAuth.getInstance().getUid();
        db.child(Admin_or_User).child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("phone")) {
                    String phone = snapshot.child("phone").getValue(String.class);
                    binding.myPhoneText.setText(phone);
                }
                binding.myEmailText.setText(snapshot.child("email").getValue(String.class));
                binding.myNameText.setText(snapshot.child("username").getValue(String.class));
                binding.buttonChangeEmail.setEnabled(false); //надо починить
                if(isAdmin)
                {
                    binding.buttonChangeName.setEnabled(false);
                    binding.buttonChangeName.setTextColor(Color.DKGRAY);
                    binding.deleteAccountButton.setEnabled(false);
                    binding.deleteAccountButton.setTextColor(Color.DKGRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Ошибка при подключении к базе данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bntChangeName(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите новое имя");

        EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = input.getText().toString().trim(); // Получаем новое имя

                if (newName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Введите новое имя", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.myNameText.setText(newName);
                DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(newName)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Имя успешно изменено", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Ошибка при изменении имени в базе данных", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        })
        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void bntChangePhone(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите ваш номер");

        EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newPhone = input.getText().toString().trim();

                        if (newPhone.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Введите ваш телефон", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        binding.myPhoneText.setText(newPhone);
                        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                db.child(Admin_or_User).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").setValue(newPhone)
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "Контакт успешно изменён", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "Ошибка при изменении контакта в базе данных", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnChangeEmail(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите новую почту");

        EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newEmail = input.getText().toString();

                        if (!Patterns.EMAIL_ADDRESS.matcher(input.getText().toString()).matches()) {
                            Toast.makeText(getApplicationContext(), "Некорректный ввод", Toast.LENGTH_SHORT).show();
                            return; // Выход, если поле пустое
                        }
                        binding.myEmailText.setText(newEmail);

                        DatabaseReference db = FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                db.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(newEmail)
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "Почта успешно изменена в базе данных", Toast.LENGTH_SHORT).show();

                                                //Здесь должна быть смена почты в аутентификации

                                            }else{
                                                Toast.makeText(getApplicationContext(), "Ошибка при изменении почты в базе данных", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Ошибка доступа к базе данных", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    } //Не рабочая

    public void btnGoExitFromMeAccount(View v){
        startActivity(new Intent(MeAccountActivity.this, MainActivity.class));
    }

    public void btnExitFromAccount(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MeAccountActivity.this, LoginActivity.class));
        finish(); // Закрываем текущую активность
    }
    public void bntDeleteAccount(View v){
        // Получаем текущего пользователя
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Удаляем пользователя из Firebase Authentication
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Удаляем данные пользователя из базы данных
                        FirebaseDatabase.getInstance("https://delivery-of-building-mat-100df-default-rtdb.europe-west1.firebasedatabase.app").getReference()
                                .child("Users")
                                .child(user.getUid())
                                .removeValue();
                        startActivity(new Intent(MeAccountActivity.this, LoginActivity.class));
                        finish(); // Закрываем текущую активность
                        Toast.makeText(getApplicationContext(), "Аккаунт удален", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ошибка при удалении аккаунта", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка при получении текущего пользователя", Toast.LENGTH_SHORT).show();
        }
    }
}