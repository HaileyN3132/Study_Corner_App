package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCard extends AppCompatActivity {

    //Database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //For intent use
    String cardId;
    String setId;

    //UI
    EditText updateTerm, updateDefinition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_card);

        //My code start here

        //Get data from intent
        Intent intent = getIntent();
        cardId = intent.getStringExtra(ManageSingleSet.CARD_ID);
        setId = intent.getStringExtra(StudySets.SET_ID);

        //Toast.makeText(getApplicationContext(),"Set ID = " + setId + " Card ID = " + cardId, Toast.LENGTH_LONG).show();    //DB-get the ID for target card

        //Get reference to edit text
        updateTerm = findViewById(R.id.update_term);
        updateDefinition = findViewById(R.id.update_definition);


        //Load info of card as hint on EditText
        DocumentReference docRef = db.collection(MainActivity.mainCollection).document(setId).collection("Cards").document(cardId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Card curCard = documentSnapshot.toObject(Card.class);
                updateTerm.setHint("Front: " + curCard.getFront());
                updateDefinition.setHint("Back: " + curCard.getBack());

            }
        });
    }


    // Save button click
    public void saveCard(View v){
        String newTerm = updateTerm.getText().toString();
        String newDefinition = updateDefinition.getText().toString();


        DocumentReference docRef = db.collection(MainActivity.mainCollection).document(setId).collection("Cards").document(cardId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // If name invalid, display error message
                if(documentSnapshot.exists()){
                    //Toast.makeText(getApplicationContext(),"Card existed!", Toast.LENGTH_LONG).show();    //DB-get the ID for target card
                    //Updated Front card
                    if(!newTerm.isEmpty()){
                        db.collection(MainActivity.mainCollection)
                                .document(setId)
                                .collection("Cards")
                                .document(cardId)
                                .update("front", newTerm);

                    }

                    // Update Back card

                    if(!newDefinition.isEmpty()){
                        db.collection(MainActivity.mainCollection)
                                .document(setId)
                                .collection("Cards")
                                .document(cardId)
                                .update("back", newDefinition);

                    }

                }


            }
        });



        finish();

    }





}