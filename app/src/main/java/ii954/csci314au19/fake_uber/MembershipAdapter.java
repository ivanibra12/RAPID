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

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder>
{
    //variable declaration
    private Context mCtx;
    private List<Membership> membershipList;
    private SimpleDateFormat simpleDateFormat;

    @NonNull
    @Override
    public MembershipViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_membership, null);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return new MembershipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipViewHolder holder, int position)
    {
        //getting the membership
        Membership membership = membershipList.get(position);

        //binding data with holder views
        holder.descTextView.setText(membership.getMID());
        holder.startDateTextView.setText("Start date : "+simpleDateFormat.format(membership.getStartDate()));
        holder.endDateTextView.setText("End date : "+simpleDateFormat.format(membership.getEndDate()));

    }

    @Override
    public int getItemCount()
    {
        return membershipList.size();
    }


    public MembershipAdapter(Context mCtx, List<Membership> membershipList) {
        this.mCtx = mCtx;
        this.membershipList = membershipList;
    }


    class MembershipViewHolder extends RecyclerView.ViewHolder{
        TextView descTextView, startDateTextView, endDateTextView;
        ImageView imageView;

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);

            //views initialization
            descTextView = itemView.findViewById(R.id.descTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);

        }
    }



}
