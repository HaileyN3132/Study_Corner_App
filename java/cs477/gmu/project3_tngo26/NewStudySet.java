package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
* This class will add new study set to database.
* Beside, it will perform to check for valid name and short description
*
* */



public class NewStudySet extends AppCompatActivity {
    EditText etName;
    EditText etAbout;
    Button btCreateSet;

    //Database
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_study_set);

        // My code start here
        etName = findViewById(R.id.set_name_et);
        etAbout = findViewById(R.id.about_et);
        btCreateSet = findViewById(R.id.create_set_btn);
    }

    // Check for valid input before process data to next step
    private Boolean inputValid(){
        String name = etName.getText().toString();
        String about = etAbout.getText().toString();
        Boolean valid = true;

        if(name.isEmpty()){
            etName.setHint("Error! Name is EMPTY");
            etName.setHintTextColor(getColor(R.color.error));
            valid = false;
        }

        if(about.isEmpty() || about.length() > 100){
            if(about.length() == 0){
                etAbout.setHint("Error! About is EMPTY");
            }
            else{
                etAbout.setHint("Error! About must SHORT, limited to 100 characters");
            }
            etAbout.setHintTextColor(getColor(R.color.error));
            valid = false;
        }

        return valid;       // always return true here
    }


    public void createSet(View v){
        // Only process valid input
        if(inputValid()){

            //Initialize database
            firestore = FirebaseFirestore.getInstance();

            // Temporary custom object
            Set studySet = new Set(etName.getText().toString(),etName.getText().toString(), etAbout.getText().toString(), 0, 0.0);


            DocumentReference docRef = firestore.collection(MainActivity.mainCollection).document(studySet.getId());            // Change from getName() to getId()
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override

                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    // If document existed, print error message
                    if(documentSnapshot.exists()){
                        Set targetSet = documentSnapshot.toObject(Set.class);
                        Toast.makeText(getApplicationContext(), "Error! Cannot add, "+ targetSet.getId() + " already existed!", Toast.LENGTH_LONG).show();
                    }


                    // Add completely study set
                    else{
                        firestore.collection(MainActivity.mainCollection).document(studySet.getId())              // Notice bug if using "etName.getText().toString() will resolve EMPTY"
                                .set(studySet)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(getApplicationContext(),"Successfull added to DB", Toast.LENGTH_LONG).show();            //DB
                                    }
                                });
                    }


                }
            });

            // Exit
            finish();
        }
    }

}