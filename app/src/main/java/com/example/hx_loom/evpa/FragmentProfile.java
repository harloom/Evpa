package com.example.hx_loom.evpa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.w3c.dom.Text;

import javax.annotation.Nullable;

public class FragmentProfile extends Fragment {
    private FirebaseAuth mAuth;
    private String uid;
    ImageView image_p;
    TextView nama_p;

    private final String TAG = "Database";

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            return;
        }
        uid = currentUser.getUid();
        final DocumentReference docUsers = db.collection("Users").document(uid);
        docUsers.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("DataBase", "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    String dataNama = (String) documentSnapshot.getString("nama");
                    String data_image = (String) documentSnapshot.getString("image_url");
                    Log.d(TAG, "user data: " + dataNama);
                    Log.d(TAG, "url_image: " + data_image);
                    setProfile(dataNama, data_image);


                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileF = inflater.inflate(R.layout.activity_profile_fragment, container, false);
        image_p = (ImageView) profileF.findViewById(R.id.profile_image);
        nama_p = (TextView) profileF.findViewById(R.id.profile_nama);

        final LinearLayout v_goBack = (LinearLayout) profileF.findViewById(R.id.backfromAbout);
        v_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gg = new Intent(v_goBack.getContext(), AboutActivity.class);
                startActivity(gg);

            }
        });

        final Button v_logout = (Button) profileF.findViewById(R.id.btn_logout);
        v_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

            }
        });

        return profileF;
    }

    private void setProfile(String _nama, String _urlImage) {
        Glide.with(this).asGif().load(_urlImage).into(image_p);
        nama_p.setText(_nama);

    }
}
