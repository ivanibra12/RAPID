package ii954.csci314au19.fake_uber;

import android.app.Dialog;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RequestHistoryActivity extends AppCompatActivity {

    //variable declaration
    private RecyclerView requestRecyclerView;
    private RequestAdapter requestAdapter;
    private TextView emptyTextView;
    private Request selected;
    private List<Request> requestList;
    private SimpleDateFormat simpleDateFormat;
    private Dialog dialog;

    //firebase database reference variable declaration
    private DatabaseReference userRequestDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_history);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //firebase database reference variable initialization
        userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());

        //variable initialization
        requestRecyclerView = (RecyclerView) findViewById(R.id.requestRecyclerView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        requestList = new ArrayList<>();
        selected=null;
        dialog = new Dialog(this);

        //load requestRecyclerView
        loadRequestRecyclerView();

        //listener for requestRecyclerView
        requestRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, requestRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //get selected request position
                selected = UserMainActivity.userRequestList.get(position);

                //show popup
                showPopup();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();

        userRequestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //load RequestRecyclerView
                loadRequestRecyclerView();

                //if user has 0 request
                if (UserMainActivity.userRequestList.size()==0)
                {
                    //set empty text view to visible
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //show popup
    private void showPopup()
    {
        if (selected!=null)
        {

            dialog.setContentView(R.layout.popup_request);

            TextView idTextView = (TextView) dialog.findViewById(R.id.idTextView);
            TextView vehicleTextView = (TextView) dialog.findViewById(R.id.vehicleTextView);
            TextView dateTextView = (TextView) dialog.findViewById(R.id.dateTextView);
            TextView timeTextView = (TextView) dialog.findViewById(R.id.timeTextView);
            TextView problemTextView = (TextView) dialog.findViewById(R.id.problemTextView);
            TextView noteTextView = (TextView) dialog.findViewById(R.id.noteTextView);
            TextView statusTextView = (TextView) dialog.findViewById(R.id.statusTextView);
            TextView closeButton = (TextView) dialog.findViewById(R.id.closeButton);

            idTextView.setText(selected.getRequestID());
            vehicleTextView.setText(selected.getVehicle().getNickname()+" ("+selected.getVehicle().getRegNum()+")");
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateTextView.setText(simpleDateFormat.format(selected.getRequestDateTime()));
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            timeTextView.setText(simpleDateFormat.format(selected.getRequestDateTime()));
            problemTextView.setText(selected.getProblem());
            noteTextView.setText(selected.getAdditionalNote());
            statusTextView.setText(selected.getStatus());

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    /**********************************************************************************************/
    //load requestRecyclerView
    private void loadRequestRecyclerView()
    {
        requestAdapter = new RequestAdapter(getApplication(),UserMainActivity.userRequestList);
        requestRecyclerView.setAdapter(requestAdapter);
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

        //override and go to History activity
        finish();
        startActivity(new Intent(RequestHistoryActivity.this,HistoryActivity.class));
    }
    /**********************************************************************************************/
}
