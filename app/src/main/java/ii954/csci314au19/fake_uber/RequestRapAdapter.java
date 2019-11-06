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

public class RequestRapAdapter extends RecyclerView.Adapter<RequestRapAdapter.RequestRapViewHolder>
{
    //variable declaration
    private Context mCtx;
    private List<Request> requestList;
    private List<String> distanceList;

    @NonNull
    @Override
    public RequestRapAdapter.RequestRapViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_request_rap, null);
        return new RequestRapAdapter.RequestRapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRapAdapter.RequestRapViewHolder holder, int position)
    {
        //getting the membership
        Request request = requestList.get(position);
        String distance = distanceList.get(position);

        //binding data with holder views
        if (request.getVehicle()==null)     //set vehicle and vehicle type
        {
            holder.vehicleTextView.setText("Vehicle : unknown");
            holder.vehicleTypeTextView.setText("Vehicle type : unknown");
        }
        else
        {
            holder.vehicleTextView.setText("Vehicle : "+request.getVehicle().getBrand()+" "+request.getVehicle().getModel()
                    +" ("+request.getVehicle().getRegNum()+")");
            holder.vehicleTypeTextView.setText("Vehicle type : "+request.getVehicle().getType());
        }

        if (request.getProblem()==null)     //set problem
        {
            holder.problemTextView.setText("Problem : unknown");
        }
        else
        {
            holder.problemTextView.setText("Problem : "+request.getProblem());
        }

        if (distance==null)                 //set distance
        {
            holder.distanceTextView.setText("Distance : unknown");
        }
        else
        {
            holder.distanceTextView.setText("Distance : "+distance);
        }

    }

    @Override
    public int getItemCount()
    {
        return requestList.size();
    }


    public RequestRapAdapter(Context mCtx, List<Request> requestList, List<String> distanceList) {
        this.mCtx = mCtx;
        this.requestList = requestList;
        this.distanceList = distanceList;
    }


    class RequestRapViewHolder extends RecyclerView.ViewHolder{
        TextView vehicleTextView, vehicleTypeTextView, problemTextView, distanceTextView;
        ImageView imageView;

        public RequestRapViewHolder(@NonNull View itemView) {
            super(itemView);

            //views initialization
            vehicleTextView = itemView.findViewById(R.id.vehicleTextView);
            vehicleTypeTextView = itemView.findViewById(R.id.vehicleTypeTextView);
            problemTextView = itemView.findViewById(R.id.problemTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);

        }
    }
}
