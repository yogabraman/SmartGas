package creativestation.smartgas;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    final DatabaseReference LAMPU = myRef.child("lampu");

    ImageView lampu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("PCC CLASS");
        lampu = findViewById(R.id.img_lampu);

        lampu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LAMPU.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String reg = dataSnapshot.getValue(String.class);
                        switch (reg){
                            case "1":
                                LAMPU.setValue("0");
                                Toast.makeText(getApplicationContext(),"Lampu Mati",Toast.LENGTH_LONG).show();
                                break;
                            case "0":
                                LAMPU.setValue("1");
                                Toast.makeText(getApplicationContext(),"Lampu Hidup",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        LAMPU.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String reg = dataSnapshot.getValue(String.class);
                if (reg.equals("0")) {
                    lampu.setImageResource(R.drawable.lightoff);
                } else {
                    lampu.setImageResource(R.drawable.lighton);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
