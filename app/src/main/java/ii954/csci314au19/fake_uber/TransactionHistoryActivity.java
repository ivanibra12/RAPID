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

public class TransactionHistoryActivity extends AppCompatActivity {

    //variable declaration
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TextView emptyTextView;
    private Transaction transactionSelected;
    private Dialog dialog;
    private SimpleDateFormat simpleDateFormat;

    //firebase database reference variable declaration
    private DatabaseReference userTransactionDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //firebase database reference variable initialization
        userTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userTransactionDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());

        //variable initialization
        transactionRecyclerView = (RecyclerView) findViewById(R.id.transactionRecyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        transactionSelected=null;
        dialog = new Dialog(this);

        //load transactionRecyclerView
        loadTransactionRecyclerView();

        //listener for transactionRecyclerView
        transactionRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, transactionRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //get selected transaction position
                transactionSelected = UserMainActivity.userTransactionList.get(position);

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

        userTransactionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //load transactionRecyclerView
                loadTransactionRecyclerView();

                //if user has 0 transaction
                if (UserMainActivity.userTransactionList.size()==0)
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
        if (transactionSelected!=null)
        {
            dialog.setContentView(R.layout.popup_transaction);

            TextView idTextView = (TextView) dialog.findViewById(R.id.idTextView);
            TextView descriptionTextView = (TextView) dialog.findViewById(R.id.descriptionTextView);
            TextView dateTextView = (TextView) dialog.findViewById(R.id.dateTextView);
            TextView timeTextView = (TextView) dialog.findViewById(R.id.timeTextView);
            TextView amountTextView = (TextView) dialog.findViewById(R.id.amountTextView);
            TextView closeButton = (TextView) dialog.findViewById(R.id.closeButton);

            idTextView.setText(transactionSelected.getTransactionID());
            descriptionTextView.setText(transactionSelected.getPaymentDescription());
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateTextView.setText(simpleDateFormat.format(transactionSelected.getTransactionDateTime()));
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            timeTextView.setText(simpleDateFormat.format(transactionSelected.getTransactionDateTime()));
            amountTextView.setText("A$ "+transactionSelected.getAmount());

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
    private void loadTransactionRecyclerView()
    {
        transactionAdapter = new TransactionAdapter(getApplication(), UserMainActivity.userTransactionList);
        transactionRecyclerView.setAdapter(transactionAdapter);
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
        startActivity(new Intent(TransactionHistoryActivity.this,HistoryActivity.class));
    }
    /**********************************************************************************************/
}
