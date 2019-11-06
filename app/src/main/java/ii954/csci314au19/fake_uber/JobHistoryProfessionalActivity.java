package ii954.csci314au19.fake_uber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobHistoryProfessionalActivity extends AppCompatActivity {

    //variable declaration
    private RecyclerView jobRecyclerView;
    private TextView emptyTextView;

    private List<Job> jobList;
    public static Job jobSelected;
    private JobAdapter jobAdapter;

    //firebase database reference variable declaration
    private DatabaseReference professionalJobDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_history_professional);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable initialization
        jobRecyclerView = (RecyclerView) findViewById(R.id.jobRecyclerView);
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);

        jobList = new ArrayList<>();
        jobSelected = null;

        //firebase database reference variable initialization
        professionalJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalJobDatabaseName).toString()).child(ProfessionalMainActivity.loggedInUser.getpID());

        //onclick listener on jobRecyclerView
        jobRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, jobRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        //get selected job at position
                        jobSelected = ProfessionalMainActivity.jobList.get(position);

                        //go to jobDetails activity
                        finish();
                        startActivity(new Intent(JobHistoryProfessionalActivity.this,JobDetailsActivity.class));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

    }

    @Override
    protected void onStart() {
        super.onStart();

        professionalJobDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //load jobRecyclerView
                loadJobRecyclerView();

                //if professional has 0 service job
                if (ProfessionalMainActivity.jobList.size()==0)
                {
                    //set empty text view to visible
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    emptyTextView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //load jobRecyclerView method
    private void loadJobRecyclerView()
    {
        jobAdapter = new JobAdapter(getApplication(),ProfessionalMainActivity.jobList);
        jobRecyclerView.setAdapter(jobAdapter);
    }

    /**********************************************************************************************/
    //override listener method for back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if id = back button
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        //override and go to HistoryProfessional  activity
        finish();
        startActivity(new Intent(JobHistoryProfessionalActivity.this,HistoryProfessionalActivity.class));
    }
    /**********************************************************************************************/
}
