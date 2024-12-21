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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudySets extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener{
    //DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //UI stuffs
    ListView listView;
    SimpleAdapter adapter;
    ArrayList<Map<String,Object>> arrayList;


    //Intent
    public final static String SET_ID = "cs477.gmu.project3_tngo26.SET_ID";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study_sets);


        //My code start here
        listView = findViewById(R.id.study_sets_list);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);




    }

    // Long-click will specific study set
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
        //Toast.makeText(getApplicationContext(),"Position = " + position, Toast.LENGTH_LONG).show(); //DB
        Map<String, Object> temp = arrayList.get(position);


        String setID = temp.get("id").toString();

        //Toast.makeText(getApplicationContext(),"ID = " + setID, Toast.LENGTH_LONG).show();    //DB-get the ID of Study Set document
        Intent intent = new Intent(this, ManageSingleSet.class);
        intent.putExtra(SET_ID, setID);       // Pass setID for next activity
        startActivity(intent);

        return true;
    }


    public void onResume(){
        super.onResume();

        // Load data from DB and display on listView
        loadDataToList();



    }


    private void loadDataToList(){

        // This array contain data loaded from DB as Map's object
        arrayList = new ArrayList<>();


        //Read all data from DB and store it in List of Map
        db.collection(MainActivity.mainCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // add to listArray
                                arrayList.add(document.getData());

                                //Toast.makeText(getApplicationContext(),"added to arrayList " + document.getId(), Toast.LENGTH_LONG).show(); //DB
                            }
                            //Toast.makeText(getApplicationContext(),"DB size = " + arrayList.size(), Toast.LENGTH_LONG).show(); //DB


                            String[] fields = {"name", "totalCards", "percentProficient"};
                            adapter = new SimpleAdapter(getApplicationContext(), arrayList,R.layout.study_set_list_item, fields, new int[] {R.id.set_name_item,
                                    R.id.total_cards_item, R.id.percent_proficient_item} );


                            listView.setAdapter(adapter);

                        }
                    }
                });

    }










    // Add new study set clicked
    public void addNewSet(View v){
        Intent intent = new Intent(this, NewStudySet.class);
        startActivity(intent);
    }

    // Use to refresh the screen, actually just jump to other activity current activity can load data from DB, do nothing
    public void refreshScreen(View v){
        Intent intent = new Intent(this, RefreshScreen.class);
        startActivity(intent);
    }


    // Study set tap will be removed
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        Map<String, Object> temp = arrayList.get(position);
        String setID = temp.get("id").toString();

        db.collection(MainActivity.mainCollection).document(setID).collection("Cards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d( "", document.getId() + " => " + document.getData());


                                //Perform delete cards
                                db.collection(MainActivity.mainCollection).document(setID).collection("Cards").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"Card removed ", Toast.LENGTH_LONG).show();    //DB-get the ID of Study Set document

                                            }
                                        });


                            }

                            //Delete the study set itself
                            db.collection(MainActivity.mainCollection).document(setID)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Study set removed ", Toast.LENGTH_LONG).show();    //DB-get the ID of Study Set document
                                            arrayList.remove(position);
                                            adapter.notifyDataSetChanged();

                                        }
                                    });



                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }




}