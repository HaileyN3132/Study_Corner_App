package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class EditStudySet extends AppCompatActivity {

    // ID passed through Intent
    String setId;

    //Database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //UI
    EditText name_et, about_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_study_set);

        //My code start here

        Intent intent = getIntent();
        setId = intent.getStringExtra(StudySets.SET_ID);
        //Toast.makeText(getApplicationContext(),"Edit ID at " + setId, Toast.LENGTH_LONG).show();    //DB-get the ID of Study Set document

        name_et = findViewById(R.id.edit_name);
        about_et = findViewById(R.id.edit_about);

        // Load basic info
        DocumentReference docRef = db.collection(MainActivity.mainCollection).document(setId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Set curSet = documentSnapshot.toObject(Set.class);
                name_et.setHint(curSet.getName());
                about_et.setHint(curSet.getAbout());
            }
        });



    }


    public void saveClicked(View v){
        String newName = name_et.getText().toString();
        String newAbout = about_et.getText().toString();;

        // Update Name if name NOT duplicate and NOT empty
        if(!newName.isEmpty()){
            DocumentReference docRef = db.collection(MainActivity.mainCollection).document(newName);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    // If name invalid, display error message
                    if(documentSnapshot.exists()){
                        Set targetSet = documentSnapshot.toObject(Set.class);
                        name_et.setHint("Error! '" + newName + "' is INVALID, choose other name");          // Invalid because newName matched the ID of study set
                        name_et.setHintTextColor( getResources().getColor(R.color.error));
                        name_et.setText("");        //Reset field for next time
                        return;
                    }

                    // Update name
                    else{
                        //Toast.makeText(getApplicationContext(), newName + " is valid to update", Toast.LENGTH_LONG).show(); //DB

                        db.collection(MainActivity.mainCollection)
                                .document(setId)
                                .update("name", newName);

                    }
                }
            });
        }


        //Update about, about is optional
        if(!newAbout.isEmpty()){
            //Toast.makeText(getApplicationContext(), newAbout + " is valid to update", Toast.LENGTH_LONG).show(); //DB

            db.collection(MainActivity.mainCollection)
                    .document(setId)
                    .update("about", newAbout);

        }

        finish();

    }






}