package creativestation.smartgas;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import creativestation.smartgas.Preferences.PrefManager;




public class GasFragment extends Fragment {
    String gaschild;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference SMART;
    LinearLayout loading;
    TextView kondisi;
    TubeSpeedometer tubeSpeedometer;
    public PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gas, container, false);
        kondisi=v.findViewById(R.id.txt_kondisi);
        tubeSpeedometer = v.findViewById(R.id.tubeSpeedometer);
        loading = v.findViewById(R.id.loading);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        SMART = myRef.child("SmartGas");

        prefManager = new PrefManager(getContext());
        gaschild = prefManager.getAlat();
        /*gaschild = "smartgas1";*/
        /*gaschild =  getActivity().getIntent().getStringExtra("child");*/
        /*Toast.makeText(getActivity(), gaschild, Toast.LENGTH_SHORT).show();*/
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Kadar Gas");
        final DatabaseReference GAS = SMART.child(gaschild).child("Gas");
        GAS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check status
                GAS.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loading.setVisibility(View.GONE);
                        Float value = dataSnapshot.child("kadar").getValue(Float.class);
                        tubeSpeedometer.speedTo(value);
                        if (value < 2.5){
                            kondisi.setText("AMAN");
                            kondisi.setTextColor(Color.parseColor("#14d94c"));
                        }
                        else if (value > 2.5){
                            kondisi.setText("BAHAYA");
                            kondisi.setTextColor(Color.parseColor("#d11717"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
