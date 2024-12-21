package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewCard extends AppCompatActivity {

    //Database
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    //Study setID want to add card
    String setId;


    //UI
    EditText etTerm, etDefinition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_card);

        //My code start here
        Intent intent = getIntent();
        setId = intent.getStringExtra(StudySets.SET_ID);
        //Toast.makeText(getApplicationContext(),"Study ID to add = " + setId, Toast.LENGTH_LONG).show();


        etTerm = findViewById(R.id.term_et);
        etDefinition = findViewById(R.id.definition_et);




    }


    public void addNewCard(View v){
        if(inputValid()){
            //Toast.makeText(getApplicationContext(),"The card ready to add " + setId, Toast.LENGTH_LONG).show();

            String front = etTerm.getText().toString();
            String back = etDefinition.getText().toString();

            Card newCard = new Card("",front, back);

            db.collection(MainActivity.mainCollection).document(setId)
                    .collection("Cards")
                    .add(newCard).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            documentReference.update("cardId", documentReference.getId());      //Update the ID match the random ID assigned

                            //Toast.makeText(getApplicationContext(),"The card ID added " + documentReference.getId(), Toast.LENGTH_LONG).show();  //DB

                            //Increase the number of card in study set by 1
                            DocumentReference currentSetRef = db.collection(MainActivity.mainCollection).document(setId);
                            currentSetRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Set currentSet = documentSnapshot.toObject(Set.class);
                                    int totalCards = currentSet.getTotalCards();

                                    currentSetRef.update("totalCards", totalCards+1);           // Update the totalCards
                                }
                            });



                        }
                    });

            // Exit
            finish();
        }

        else{
            Toast.makeText(getApplicationContext(),"Error! Input invalid, try again ", Toast.LENGTH_LONG).show();
        }


    }


    private Boolean inputValid(){
        Boolean valid = true;

        String term = etTerm.getText().toString();
        String definition = etDefinition.getText().toString();

        if(term.isEmpty()){
            // Display error as hint
            etTerm.setHint("Error! Term is EMPTY");
            etTerm.setHintTextColor(getResources().getColor(R.color.error));

            valid = false;
        }

        if(definition.isEmpty()){
            //Display error as hint
            etDefinition.setHint("Error! Definition is EMPTY");
            etDefinition.setHintTextColor(getResources().getColor(R.color.error));
            valid = false;

        }

        return valid;
    }





}