package creativestation.smartgas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import creativestation.smartgas.Preferences.PrefManager;

public class PasswordActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;
    Button password;
    GoogleSignInClient mGoogleSignInClient;
    public PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        password = findViewById(R.id.btn_password);

        prefManager = new PrefManager(this);
        //Ben langsung ning HomeActivity()
        if (prefManager.isPunyaAlat()){
            menuUtama();
            finish();
        }

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        //Button Password
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert =new AlertDialog.Builder(PasswordActivity.this);
                AlertDialog dialog;
                alert.setTitle("Password");
                alert.setMessage("Tulis password");

                final EditText input = new EditText(PasswordActivity.this);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                alert.setView(input);
                alert.setIcon(R.drawable.password);
                alert.setPositiveButton("Masuk", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference passRef = FirebaseDatabase.getInstance().getReference().child("SmartGas");
                        passRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()){
                                    final String childName = areaSnapshot.getKey();
                                    String pass = dataSnapshot.child(childName).child("password").getValue(String.class);
                                    if (pass.equals(input.getText().toString())){
                                        pilihAlat(mFireBaseUser.getUid(), childName);
                                        menuUtama();
                                        /*startActivity(new Intent(getApplicationContext(),HomeActivity.class).putExtra("child",childName));*/
                                        /*Toast.makeText(PasswordActivity.this, "Pintu Terbuka", Toast.LENGTH_SHORT).show();*/
                                    }else{
                                        /*Toast.makeText(PasswordActivity.this, "Password Salah", Toast.LENGTH_SHORT).show();*/
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog = alert.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alert.show();
            }
        });
    }

    private void pilihAlat(String uid, String alat) {
        mDatabase.child("users").child(uid).child("alat").setValue(alat);
        prefManager.setAlat(alat);
    }

    private void menuUtama() {
        prefManager.setPunyaAlat(true);
        startActivity(new Intent(PasswordActivity.this,HomeActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.tips).setVisible(false);
        menu.findItem(R.id.ganti_alat).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.logout) {
            signOut();
            resetAlat(mFireBaseUser.getUid());
            prefManager.setLoginStatus(false);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void resetAlat(String uid) {
        mDatabase.child("users").child(uid).child("alat").setValue("0");
    }

    private void signOut() {
        // Firebase sign out
        /*firebaseAuth.signOut();*/
        FirebaseAuth.getInstance().signOut();

//          google account sign out & revoke saved account
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}
