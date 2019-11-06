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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>
{
    //variable declaration
    private Context mCtx;
    private List<Request> requestList;
    private SimpleDateFormat simpleDateFormat;

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_request, null);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return new RequestAdapter.RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.RequestViewHolder holder, int position)
    {
        //getting the membership
        Request request = requestList.get(position);

        //binding data with holder views
        holder.descTextView.setText(request.getRequestID());
        holder.vehicleTextView.setText("Vehicle : "+request.getVehicle().getNickname()+" ("+request.getVehicle().getRegNum()+")");
        holder.requestDateTextView.setText("Request date : "+simpleDateFormat.format(request.getRequestDateTime()));
        holder.requestStatusTextView.setText("Request status : "+request.getStatus());

    }

    @Override
    public int getItemCount()
    {
        return requestList.size();
    }


    public RequestAdapter(Context mCtx, List<Request> requestList) {
        this.mCtx = mCtx;
        this.requestList = requestList;
    }


    class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView descTextView, vehicleTextView, requestDateTextView, requestStatusTextView;
        ImageView imageView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            //views initialization
            descTextView = itemView.findViewById(R.id.descTextView);
            vehicleTextView = itemView.findViewById(R.id.vehicleTextView);
            requestDateTextView = itemView.findViewById(R.id.requestDateTextView);
            requestStatusTextView = itemView.findViewById(R.id.requestStatusTextView);

        }
    }




}
