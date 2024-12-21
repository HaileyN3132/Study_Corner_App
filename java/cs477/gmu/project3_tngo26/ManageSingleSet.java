package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ManageSingleSet extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    //Database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //UI
    TextView tvSetId, tvAbout;
    ListView listView;
    SimpleAdapter adapter;
    ArrayList<Map<String,Object>> arrayList;

    //Store set ID passed
    String setId;


    //For intent use
    //Intent
    public final static String CARD_ID = "cs477.gmu.project3_tngo26.CARD_ID";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_single_set);

        // My code start here
        tvSetId = findViewById(R.id.set_id_tv);
        tvAbout = findViewById(R.id.set_about_tv);

        Intent intent = getIntent();
        setId = intent.getStringExtra(StudySets.SET_ID);
        //Toast.makeText(getApplicationContext(),"ID Here = " + setId, Toast.LENGTH_LONG).show();    //DB-get the ID of Study Set document

        //Listview
        listView = findViewById(R.id.list_cards);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);


    }


    // Long-click will specific study set
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
        //Toast.makeText(getApplicationContext(),"Position = " + position, Toast.LENGTH_LONG).show(); //DB
        Map<String, Object> temp = arrayList.get(position);

        String targetCardId = temp.get("cardId").toString();
        //String targetFront = temp.get("front").toString();            //DB
        //Toast.makeText(getApplicationContext(),"Front = " + targetFront + " cardID = " + targetCardId , Toast.LENGTH_LONG).show();    //DB

        //Passed ID for EditCard activity
        Intent intent = new Intent(this, EditCard.class);
        intent.putExtra(StudySets.SET_ID, setId);   // Passed setID and cardID
        intent.putExtra(CARD_ID, targetCardId);
        startActivity(intent);
        return true;
    }


    // Card tap
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){

        Map<String, Object> temp = arrayList.get(position);
        String targetCardId = temp.get("cardId").toString();

        String front = temp.get("front").toString();        //DB
        //Toast.makeText(getApplicationContext(),"Selected Front = " + temp.get("front").toString(), Toast.LENGTH_LONG).show(); //DB


        db.collection(MainActivity.mainCollection).document(setId).collection("Cards").document(targetCardId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(),"Deleted Front = " + temp.get("front").toString(), Toast.LENGTH_LONG).show(); //DB

                        //Remove card on arrayList
                        arrayList.remove(position);
                        adapter.notifyDataSetChanged();         //notify data chance on listView


                        //Update the totalCard on the current study set, decrease the card by 1
                        DocumentReference currentSetRef = db.collection(MainActivity.mainCollection).document(setId);
                        currentSetRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Set currentSet = documentSnapshot.toObject(Set.class);
                                int totalCards = currentSet.getTotalCards();

                                currentSetRef.update("totalCards", totalCards-1);           // Update the totalCards
                            }
                        });




                    }
                });


    }




    public void onResume(){
        super.onResume();

        // Load basic info
        DocumentReference docRef = db.collection(MainActivity.mainCollection).document(setId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Set curSet = documentSnapshot.toObject(Set.class);
                tvSetId.setText(curSet.getName());
                tvAbout.setText(curSet.getAbout());
            }
        });


        //Load listview of card
        loadCardToList();


    }

    private void loadCardToList(){
        arrayList = new ArrayList<>();

        //Read all cards from database
        db.collection(MainActivity.mainCollection).document(setId).collection("Cards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                //add to array list
                                arrayList.add(document.getData());
                            }
                            //Toast.makeText(getApplicationContext(),"Array size  = " + arrayList.size(), Toast.LENGTH_LONG).show();    //DB

                            String[] fields = {"front","back"};
                            adapter = new SimpleAdapter(getApplicationContext(),arrayList, R.layout.card_list_item, fields, new int[] {
                               R.id.front_card,R.id.back_card});

                            listView.setAdapter(adapter);

                        }
                    }
                });
    }




    // Edit basic info of StudySet, such as change name and about
    public void editSet(View v){
        Intent intent = new Intent(this, EditStudySet.class);
        intent.putExtra(StudySets.SET_ID, setId);
        startActivity(intent);

    }

    // Create new card to study set
    public void createCard(View v){
        Intent intent = new Intent(this, NewCard.class);
        intent.putExtra(StudySets.SET_ID, setId);
        startActivity(intent);

    }



    //Refresh the screen to
    public void refreshScreen2(View v){
        Intent intent = new Intent(this, RefreshScreen.class);
        startActivity(intent);
    }








}