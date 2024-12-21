package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class QuizSelection extends AppCompatActivity implements AdapterView.OnItemClickListener{
    //DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //UI
    ListView listView;
    SimpleAdapter adapter;
    ArrayList<Map<String,Object>> arrayList;    //List Study Sets

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_selection);


        /* My code start here  */

        //ListView
        listView = findViewById(R.id.quiz_selection_list);
        listView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        //Toast.makeText(getApplicationContext(),"Position = " + position, Toast.LENGTH_LONG).show(); //DB
        Map<String, Object> temp = arrayList.get(position);


        String setID = temp.get("id").toString();

        //Toast.makeText(getApplicationContext(),"ID = " + setID, Toast.LENGTH_LONG).show();    //DB-get the ID of Study Set document

        Intent intent = new Intent(this, QuizSection.class);
        intent.putExtra(StudySets.SET_ID, setID);
        startActivity(intent);

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







}