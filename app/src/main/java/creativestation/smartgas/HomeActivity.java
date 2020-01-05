package creativestation.smartgas;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import creativestation.smartgas.Preferences.PrefManager;

public class HomeActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    public PrefManager prefManager;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;
    boolean doubleBackToExitPressedOnce = false;
    String gaschild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNav);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Untuk Notif
        prefManager = new PrefManager(this);
        gaschild = prefManager.getAlat();
        /*gaschild = "smartgas1";*/
        FirebaseMessaging.getInstance().subscribeToTopic("kadar"+gaschild);
        FirebaseMessaging.getInstance().subscribeToTopic("lpg"+gaschild);
        /*gaschild =  getIntent().getStringExtra("child");*/
       /* Toast.makeText(this, "Selamat menggunakan " + gaschild, Toast.LENGTH_SHORT).show();*/

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new ProfileFragment()).commit();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new ProfileFragment()).commit();
                        break;
                    case R.id.navigation_gas:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new GasFragment()).commit();
                        break;
                    case R.id.navigation_lpg:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new LPGFragment()).commit();
                        break;
                    case R.id.navigation_monitor:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new MonitorFragment()).commit();
                        break;
                }return true;
            }
        });

        //Buka fragment dari notif
        String channel = getIntent().getStringExtra("pindah");
        if (channel !=null){
            Log.d("getExtra", channel);
            if (channel.equals("CHANNEL_1")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new GasFragment()).commit();
            } else if (channel.equals("CHANNEL_2")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new LPGFragment()).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.tips) {
            startActivity(new Intent(this, TipsActivity.class));
            return true;
        }else if (i == R.id.ganti_alat) {
            prefManager.setPunyaAlat(false);
            resetAlat(mFireBaseUser.getUid());
            startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
            finish();
            return true;
        }else if (i == R.id.logout) {
            signOut();
            prefManager.setLoginStatus(false);
            prefManager.setPunyaAlat(false);
            resetAlat(mFireBaseUser.getUid());
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        else {
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }
}
