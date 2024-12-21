package cs477.gmu.project3_tngo26;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class QuizSection extends AppCompatActivity {
    //Database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //For intent use
    String setId;

    //UI
    TextView tvQuestNum, tvQuestion, tvProficient, tvCorrectAns, tvIncorrectAns, totalCards1,totalCards2;
    EditText etUser;
    Button btnProcess;
    LinearLayout quizPart;
    LinearLayout resultPart;


    //Helper variables
    int position;
    ArrayList<Map<String,Object>> arrayList;    //List of Cards




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_section);

        //My code start here
        Intent intent = getIntent();
        setId = intent.getStringExtra(StudySets.SET_ID);

        initializeValue();
        loadCards();




    }


    private void initializeValue(){
        position = 0;

        /* UI stuffs*/
        tvQuestNum = findViewById(R.id.quest_num);
        tvQuestNum.setText(String.valueOf(position+1));

        tvQuestion = findViewById(R.id.question_tv);        // Initialize in loadCards()

        tvCorrectAns = findViewById(R.id.correct_ans);
        tvCorrectAns.setText("0");

        tvIncorrectAns = findViewById(R.id.incorrect_ans);
        tvIncorrectAns.setText("0");

        tvProficient = findViewById(R.id.percent_result);
        tvProficient.setText("0");

        totalCards1 = findViewById(R.id.totalCards_1);   // Initialize in loadCards()
        totalCards2 = findViewById(R.id.totalCards_2);   // Initialize in loadCards()

        //EditText
        etUser = findViewById(R.id.answer_et);

        //Button
        btnProcess = findViewById(R.id.process_btn);

        //LinearLayout
        quizPart = findViewById(R.id.quiz_part);
        resultPart = findViewById(R.id.result_part);



    }

    // Load card info from database to arrayList
    private void loadCards(){
        arrayList = new ArrayList<>();

        db.collection(MainActivity.mainCollection).document(setId).collection("Cards")
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

                            //If study set is empty, display error
                            if(arrayList.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Error! Study set EMPTY. Please add some cards " + arrayList.size(), Toast.LENGTH_LONG).show(); //DB
                                finish();
                            }
                            else{
                                //Initialize the Question
                                Map<String, Object> temp = arrayList.get(position);
                                tvQuestion.setText(temp.get("back").toString());
                                totalCards1.setText(String.valueOf(arrayList.size()));
                                totalCards2.setText(String.valueOf(arrayList.size()));
                            }

                            //Toast.makeText(getApplicationContext(),"DB size = " + arrayList.size(), Toast.LENGTH_LONG).show(); //DB
                        }
                    }
                });

    }


    public void buttonClicked(View v){
        if(position >= arrayList.size()){
            quizPart.setVisibility(View.INVISIBLE);         // Hide the quiz part

            //Add percent proficient
            float percentProficent = (Float.valueOf(tvCorrectAns.getText().toString()) / Float.valueOf(totalCards1.getText().toString())) * 100.0f;

            String percentStr = String.format("%.2f", percentProficent);
            //Toast.makeText(getApplicationContext(),"Percent = " + percentStr, Toast.LENGTH_LONG).show(); //DB


            //Update database
            db.collection(MainActivity.mainCollection)
                    .document(setId)
                    .update("percentProficient", Float.valueOf(percentStr));


            tvProficient.setText(String.format("%.2f", percentProficent));
            resultPart.setVisibility(View.VISIBLE);         // Show result
            return;
        }

        if(inputValid()){
            //Check answer
            String ans = etUser.getText().toString();
            String frontCard = arrayList.get(position).get("front").toString();
            if(ans.equals(frontCard)){
                int numCorrect = Integer.parseInt(tvCorrectAns.getText().toString());
                tvCorrectAns.setText(String.valueOf(numCorrect+1));
            }
            else{
                int numIncorrect = Integer.parseInt(tvIncorrectAns.getText().toString());
                tvIncorrectAns.setText(String.valueOf(numIncorrect+1));
            }

            // Process next question
            position++;
            if (position < arrayList.size()) {
                tvQuestion.setText(arrayList.get(position).get("back").toString()); //update question

                //Reset EditText
                etUser.setText("");
                etUser.setHint("Enter your answer");
                etUser.setHintTextColor(getResources().getColor(R.color.hintColor));


            } else {
                btnProcess.setText("Show Result");
                //Hide userInput
                etUser.setVisibility(View.INVISIBLE);


            }


        }

    }



    private Boolean inputValid(){
        Boolean valid = true;
        String ans = etUser.getText().toString();

        if(ans.isEmpty()){
            etUser.setHint("Please enter the answer");
            etUser.setHintTextColor(getResources().getColor(R.color.error));
            valid = false;
        }


        return valid;
    }



}