package creativestation.smartgas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class ProfileFragment extends Fragment implements View.OnClickListener {
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference USER;
    TextView txtUsername, txtTipe;
    Button btnPassword, btnComplain;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid;
    String gaschild;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        txtUsername = v.findViewById(R.id.txt_username);
        txtTipe = v.findViewById(R.id.txt_tipe);
        btnPassword = v.findViewById(R.id.setting_pw);
        btnComplain = v.findViewById(R.id.complain);
        btnPassword.setOnClickListener(this);
        btnComplain.setOnClickListener(this);
        /*gaschild =  getActivity().getIntent().getStringExtra("child");*/
        gaschild = "smartgas1";
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        USER = myRef.child("users");

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Profil");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        
        final DatabaseReference User = USER.child(uid);

        User.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("username").getValue(String.class);
                txtUsername.setText(nama);
                String tipe = dataSnapshot.child("alat").getValue(String.class);
                txtTipe.setText(tipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_pw:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.complain:
                startActivity(new Intent(getContext(), Notif.class));
                break;
        }
    }
}
