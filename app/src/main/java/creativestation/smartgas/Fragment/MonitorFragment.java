package creativestation.smartgas.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import creativestation.smartgas.Preferences.PrefManager;
import creativestation.smartgas.R;
import creativestation.smartgas.RiwayatActivity;
import creativestation.smartgas.models.PointValue;

public class MonitorFragment extends Fragment {
    Button btn_riwayat;
    TextView rerata, prediksi;
    int value, pilihBulan;
    int total = 0;
    int count = 0;
    int average = 0;
    String gaschild, year, month, pilihTahun;
    FirebaseDatabase database;
    DatabaseReference myRef, SMART, Grafik, LPG, AI;
    SimpleDateFormat sdf = new SimpleDateFormat("\ndd/M \nHH:mm");
    Spinner tahun, bulan;
    GraphView graphView;
    LineGraphSeries series;
    public PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_monitor, container, false);
        prefManager = new PrefManager(getContext());
        gaschild = prefManager.getAlat();

        //Setting waktu
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat myear = new SimpleDateFormat("yyyy");
        SimpleDateFormat mmonth = new SimpleDateFormat("M");
        year= myear.format(calendar.getTime());
        month = mmonth.format(calendar.getTime());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        SMART = myRef.child("SmartGas");
        Grafik = SMART.child(gaschild).child("Grafik");
        LPG = SMART.child(gaschild).child("Regulator").child("value");
        AI = SMART.child(gaschild).child("AI");

        //Variabel Tahun dan Bulan
        tahun = v.findViewById(R.id.tahun);
        bulan = v.findViewById(R.id.bulan);

        rerata = v.findViewById(R.id.rerata);
        prediksi = v.findViewById(R.id.prediksi);

        btn_riwayat = v.findViewById(R.id.btn_ganti);
        btn_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RiwayatActivity.class));
            }
        });

        graphView= v.findViewById(R.id.graphView);
        series= new LineGraphSeries();
        graphView.addSeries(series);

        series.setAnimated(true);
        series.setThickness(6);
        series.setDrawBackground(true);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(15);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg="Tanggal : "+sdf.format(new Date((long)dataPoint.getX())) +"\nGas Terpakai : "+dataPoint.getY()+"%";
                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Grafik");

        //Sorting Tahun dan Bulan
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.tahun, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tahun.setAdapter(adapter);
        tahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Tahun")){
                    //Grafik
                    Grafik.child(year).child(month).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                            int index = 0;

                            for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
                                dp[index] = new DataPoint(Double.parseDouble(pointValue.gettgl()), pointValue.getFirstValue() - pointValue.getLastValue());
                                index++;
                            }
                            series.resetData(dp);
                            zoom(graphView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    //Artificial Inteligence
                    AI.child("AI-data").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                                Integer selisih = myDataSnapshot.child("selisih").getValue(Integer.class);
                                total = total + selisih;
                                count = count + 1;
                                average = total / count;
                            }
                            //rata-rata penggunaan
                            AI.child("average").setValue(average);
                            rerata.setText(average + " %");

                            //Isi gas terkini
                            LPG.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    value = dataSnapshot.getValue(Integer.class);
                                    //Prediksi hampir habis
                                    if (average != 0) {
                                        AI.child("prediksi").setValue(value / average);
                                        prediksi.setText(value / average + " hari lagi");
                                    } else {
                                        AI.child("prediksi").setValue(0);
                                        prediksi.setText("belum menggunakan");
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

                }else {
                    pilihTahun = adapterView.getItemAtPosition(i).toString();
                    Toast.makeText(getContext(),pilihTahun,Toast.LENGTH_SHORT).show();

                    //Sorting Bulan
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),R.array.bulan, android.R.layout.simple_spinner_item);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bulan.setAdapter(adapter1);
                    bulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (adapterView.getItemAtPosition(i).equals("Bulan")){

                            } else {
                                pilihBulan = (int) adapterView.getItemIdAtPosition(i);
                                String namaBulan = adapterView.getItemAtPosition(i).toString();
                                Toast.makeText(getContext(),namaBulan,Toast.LENGTH_SHORT).show();

                                //Sorting Chart
                                Grafik.child(pilihTahun).child(String.valueOf(pilihBulan)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        DataPoint[] dp=new DataPoint[(int) dataSnapshot.getChildrenCount()];
                                        int index=0;

                                        for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren())
                                        {
                                            PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
                                            dp[index]=new DataPoint(Double.parseDouble(pointValue.gettgl()),pointValue.getFirstValue()-pointValue.getLastValue());
                                            index++;
                                        }

                                        series.resetData(dp);
                                        zoom(graphView);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                //Artificial Inteligence
                                AI.child("AI-data").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                                            Integer selisih = myDataSnapshot.child("selisih").getValue(Integer.class);

                                            total = total + selisih;
                                            count = count + 1;
                                            average = total / count;
                                        }
                                        //rata-rata penggunaan
                                        AI.child("average").setValue(average);
                                        rerata.setText(average + " %");

                                        //Isi gas terkini
                                        LPG.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                value = dataSnapshot.getValue(Integer.class);
                                                //Prediksi hampir habis
                                                if (average != 0) {
                                                    AI.child("prediksi").setValue(value / average);
                                                    prediksi.setText(value / average + " hari lagi");
                                                } else {
                                                    AI.child("prediksi").setValue(0);
                                                    prediksi.setText("belum menggunakan");
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

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void zoom(GraphView graphView) {
        graphView.getAnimation();
        graphView.refreshDrawableState();

        graphView.getViewport().scrollToEnd();

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        graphView.getViewport().getMinY(true);
        graphView.getViewport().getMaxY(true);

        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return sdf.format(new Date((long) value));
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });

    }
}