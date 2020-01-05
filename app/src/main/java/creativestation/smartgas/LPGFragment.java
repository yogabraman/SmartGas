package creativestation.smartgas;


import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import creativestation.smartgas.Preferences.PrefManager;


public class LPGFragment extends Fragment {
    String gaschild;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference SMART;
    Button lpg;
    SpeedView speedView;
    LinearLayout loading;
    public PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lpg, container, false);
        lpg = v.findViewById(R.id.btn_lpg);
        speedView = v.findViewById(R.id.speedView);
        loading = v.findViewById(R.id.loading);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        SMART = myRef.child("SmartGas");

        prefManager = new PrefManager(getContext());
        gaschild = prefManager.getAlat();
//        gaschild = "smartgas1";
        /*gaschild =  getActivity().getIntent().getStringExtra("child");*/
        /*Toast.makeText(getActivity(), gaschild, Toast.LENGTH_SHORT).show();*/
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("LPG");
        final DatabaseReference LPG = SMART.child(gaschild).child("Regulator");
        lpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change status
                LPG.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LPG.child("status").setValue("0");
                        Toast.makeText(getContext(),"Regulator Terlepas",Toast.LENGTH_SHORT).show();
                        Thread i = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(1800);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    LPG.child("status").setValue("1");
                                }
                            }
                        };
                        i.start();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        LPG.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check status
                LPG.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loading.setVisibility(View.GONE);
                        Integer value = dataSnapshot.child("value").getValue(Integer.class);
                        speedView.speedPercentTo(value);

                        final String reg = dataSnapshot.child("status").getValue(String.class);
                        if (reg.equals("0")) {
                            lpg.setBackgroundResource(R.drawable.red_button);
                        } else {
                            lpg.setBackgroundResource(R.drawable.orange_button);
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

    /*final String reg = dataSnapshot.child("status").getValue(String.class);
                        switch (reg) {
                                case "1":
                                LPG.child("status").setValue("0");
                                Toast.makeText(getContext(),"Regulator Terlepas",Toast.LENGTH_LONG).show();
                                break;
                                case "0":
                                LPG.child("status").setValue("1");
                                Toast.makeText(getContext(),"Regulator Terpasang",Toast.LENGTH_LONG).show();
                                break;
                                }*/