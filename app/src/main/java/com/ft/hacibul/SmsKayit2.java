
package com.ft.hacibul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SmsKayit2 extends AppCompatActivity {

    String telNo;
    Long zaman = 120L;
    String onayKodu;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressBar progressBar;
    EditText smsGiris;
    TextView resendOtpTextView;
    PhoneAuthProvider.ForceResendingToken  resendingToken;
    FirebaseUser user1;
    Button ileriBtn;
    String id;

    String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_kayit2);

        telNo = getIntent().getStringExtra("telNo");
        flag = getIntent().getStringExtra("flag");
        smsGiris = findViewById(R.id.login_otp);
        ileriBtn = findViewById(R.id.ileri_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        sendOtp(telNo,false);

        ileriBtn.setOnClickListener(v -> {
            String enteredOtp  = smsGiris.getText().toString();
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(onayKodu,enteredOtp);
            signIn(credential);
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(telNo,true);
        });

    }


    void sendOtp(String phoneNumber,boolean isResend){
        user1 = mAuth.getCurrentUser();
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(telNo)
                        .setTimeout(zaman, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(getApplicationContext(), "Sms gönderimi başarısız.", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                onayKodu = s;
                                resendingToken = forceResendingToken;
                                user1 = mAuth.getCurrentUser();
                                setInProgress(false);

                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity

        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Doğrulama Başarılı", Toast.LENGTH_SHORT).show();


                    if(flag.equals("1"))
                    {
                        user1 = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(),"Kullanıcı kayıt sayfasına yönlendiriliyorsunuz",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SmsKayit2.this, KullaniciSmsKayit.class);
                        intent.putExtra("telNo",telNo);
                        intent.putExtra("userId",user1.getUid());
                        finish();
                        startActivity(intent);

                    }

                    else if(flag.equals("0"))
                    {
                        user1 = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(),"Rehber kayıt sayfasına yönlendiriliyorsunuz",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SmsKayit2.this, RehberSmsKayit.class);
                        intent.putExtra("telNo",telNo);
                        intent.putExtra("userId",user1.getUid());
                        finish();
                        startActivity(intent);

                    }

                }else{

                    Toast.makeText(getApplicationContext(), "Doğrulama Başarısız", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            ileriBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            ileriBtn.setVisibility(View.VISIBLE);
        }
    }

    void startResendTimer() {
        resendOtpTextView.setEnabled(false);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                zaman--;

                if (zaman <= 0) {
                    // Süre bitti
                    zaman = 120L;
                    resendOtpTextView.setText("Yeniden SMS gönder");
                    resendOtpTextView.setEnabled(true);
                } else {

                    resendOtpTextView.setText("SMS doğrulaması için kalan zaman " + zaman);
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.post(runnable);
    }

}