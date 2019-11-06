package ii954.csci314au19.fake_uber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionsViewHolder>
{
    //variable declaration
    private Context mCtx;
    private List<Transaction> transactionList;
    private SimpleDateFormat simpleDateFormat;

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_transaction, null);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return new TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionsViewHolder holder, int position)
    {
        //getting the transaction
        Transaction transaction = transactionList.get(position);

        //binding data with holder views
        holder.descTextView.setText(transaction.getTransactionID());
        holder.amountTextView.setText("A$ "+transaction.getAmount());
        holder.dateTextView.setText(simpleDateFormat.format(transaction.getTransactionDateTime()));

    }

    @Override
    public int getItemCount()
    {
        return transactionList.size();
    }


    public TransactionAdapter(Context mCtx, List<Transaction> transactionList) {
        this.mCtx = mCtx;
        this.transactionList = transactionList;
    }


    class TransactionsViewHolder extends RecyclerView.ViewHolder{
        TextView descTextView, amountTextView, dateTextView;
        ImageView imageView;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);

            //views initialization
            descTextView = itemView.findViewById(R.id.descTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

        }
    }
}
