package creativestation.smartgas;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import creativestation.smartgas.Preferences.PrefManager;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference SMART;
    String gaschild, gantiPassword;
    EditText passwordNow, passwordNew;
    Button btnSave;
    public PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Setting Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        SMART = myRef.child("SmartGas");

        prefManager = new PrefManager(this);
        gaschild = prefManager.getAlat();
//        gaschild = "smartgas1";
        /*gaschild =  getIntent().getStringExtra("child");*/

        passwordNow = findViewById(R.id.password_now);
        passwordNew = findViewById(R.id.password_new);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference PASS = SMART.child(gaschild).child("password");

        PASS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pw = dataSnapshot.getValue(String.class);
                passwordNow.setText(pw);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                gantiPassword = passwordNew.getText().toString();
                changePassword(gaschild, gantiPassword);
                onBackPressed();
                break;
        }
    }

    private void changePassword(String gaschild, String gantiPassword) {
        SMART.child(gaschild).child("password").setValue(gantiPassword);
    }
}
