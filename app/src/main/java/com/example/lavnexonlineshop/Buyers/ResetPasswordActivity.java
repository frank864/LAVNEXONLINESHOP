package com.example.lavnexonlineshop.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Prevalent.Prevalent;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check="";
    private EditText phoneNumber,question1,question2;
    private TextView pageTitle,titleQuestions;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check=getIntent().getStringExtra("check");
        pageTitle= findViewById(R.id.page_title);
        phoneNumber= findViewById(R.id.find_phone_number);
        question1=findViewById(R.id.question_1);
        question2= findViewById(R.id.question_2);
        titleQuestions=findViewById(R.id.title_question);
        verifyButton=findViewById(R.id.verify_btn);
    }


    @Override
    protected void onStart() {

        super.onStart();
        phoneNumber.setVisibility(View.GONE);
        if (check.equals("settings")){

            pageTitle.setText("Set questions");

            titleQuestions.setText("Please answer the following security question?");
            displayPreviousAnswers();
            verifyButton.setText("Set");
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();

                }
            });


        } else if (check.equals("login")) {

            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();
                }
            });

        }
    }

    private void verifyUser() {
        final   String phone = phoneNumber.getText().toString();
        final   String answer1=question1.getText().toString().toLowerCase();
        final   String answer2=question2.getText().toString().toLowerCase();


        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")){
            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    if (datasnapshot.exists()){
                        String mPhone = datasnapshot.child("phone").getValue().toString();

                        if (datasnapshot.hasChild("Security Questions")){
                            String ans1= datasnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2= datasnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this, "Your first answwr is wrong", Toast.LENGTH_SHORT).show();
                            } else if (!ans2.equals(answer2)) {
                                Toast.makeText(ResetPasswordActivity.this, "Your second answer is incorrect", Toast.LENGTH_SHORT).show();
                            }else {
                                AlertDialog.Builder builder= new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New password");
                                final  EditText newpassword = new EditText(ResetPasswordActivity.this);
                                newpassword.setHint("Write new password here...");
                                builder.setView(newpassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (!newpassword.getText().toString().equals("")){
                                            ref.child("password").setValue(newpassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){

                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                                Toast.makeText(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                builder.show();

                            }
                        }else {
                            Toast.makeText(ResetPasswordActivity.this, "You have not set the security questions.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "This phone number does not exist...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show();
        }


    }

    private void setAnswers(){
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();

        if (question1.equals("") && question2.equals("")){
            Toast.makeText(ResetPasswordActivity.this, "Please fill all the question correctly", Toast.LENGTH_SHORT).show();
        }else {
            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1",answer1);
            userdataMap.put("answer2",answer2);

            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "You have answered security question successfully", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void displayPreviousAnswers(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {


                if (datasnapshot.exists()){

                    String ans1= datasnapshot.child("answer1").getValue().toString();
                    String ans2= datasnapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}