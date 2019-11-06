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

import java.text.SimpleDateFormat;

public class MembershipHistoryActivity extends AppCompatActivity {

    //variable declaration
    private RecyclerView membershipRecyclerView;
    private MembershipAdapter membershipAdapter;
    private TextView emptyTextView;
    private Membership membershipSelected;
    private Dialog dialog;
    private SimpleDateFormat simpleDateFormat;

    //firebase database reference variable declaration
    private DatabaseReference membershipDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_history);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //firebase database reference variable initialization
        membershipDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.membershipDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());

        //variable initialization
        membershipRecyclerView = (RecyclerView) findViewById(R.id.membershipRecyclerView);
        membershipRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        membershipSelected = null;
        dialog = new Dialog(this);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //load membershipRecyclerView
        loadMembershipRecyclerView();

        //listener for membershipRecyclerView
        membershipRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, membershipRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //get selected membership position
                membershipSelected = UserMainActivity.membershipList.get(position);

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

        membershipDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //load membershipRecyclerView
                loadMembershipRecyclerView();

                //if user has 0 membership history
                if (UserMainActivity.membershipList.size()==0)
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
        if (membershipSelected!=null)
        {
            dialog.setContentView(R.layout.popup_membership);

            TextView idTextView = (TextView) dialog.findViewById(R.id.idTextView);
            TextView durationTextView = (TextView) dialog.findViewById(R.id.durationTextView);
            TextView startDateTextView = (TextView) dialog.findViewById(R.id.startDateTextView);
            TextView endDateTextView = (TextView) dialog.findViewById(R.id.endDateTextView);
            TextView tidTextView = (TextView) dialog.findViewById(R.id.tidTextView);
            TextView closeButton = (TextView) dialog.findViewById(R.id.closeButton);

            idTextView.setText(membershipSelected.getMID());
            durationTextView.setText(membershipSelected.getDuration());
            startDateTextView.setText(simpleDateFormat.format(membershipSelected.getStartDate()));
            endDateTextView.setText(simpleDateFormat.format(membershipSelected.getEndDate()));
            tidTextView.setText(membershipSelected.getTransactionID());

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    //load membershipRecyclerView
    private void loadMembershipRecyclerView()
    {
        membershipAdapter = new MembershipAdapter(getApplication(),UserMainActivity.membershipList);
        membershipRecyclerView.setAdapter(membershipAdapter);
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
        startActivity(new Intent(MembershipHistoryActivity.this,HistoryActivity.class));
    }
    /**********************************************************************************************/
}
