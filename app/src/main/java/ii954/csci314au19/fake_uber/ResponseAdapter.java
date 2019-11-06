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

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder>
{
    //variable declaration
    private Context mCtx;
    private List<Response> responseList;

    @NonNull
    @Override
    public ResponseAdapter.ResponseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_response, null);
        return new ResponseAdapter.ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseAdapter.ResponseViewHolder holder, int position)
    {
        //getting the response
        Response response = responseList.get(position);

        //binding data with holder views
        if (response.getProfessional()==null)       //set id and name
        {
            holder.idTextView.setText("Professional ID : ");
            holder.nameTextView.setText("Name : ");
        }
        else
        {
            holder.idTextView.setText("Professional ID : "+response.getProfessional().getpID());
            holder.nameTextView.setText("Name : "+response.getProfessional().getFirstName()+" "+response.getProfessional().getLastName());
        }

        if (response.getDistance()==null)           //set distance
        {
            holder.distanceTextView.setText("Distance : ");
        }
        else
        {
            holder.distanceTextView.setText("Distance : "+response.getDistance());
        }
    }

    @Override
    public int getItemCount()
    {
        return responseList.size();
    }


    public ResponseAdapter(Context mCtx, List<Response> responseList) {
        this.mCtx = mCtx;
        this.responseList = responseList;
    }


    class ResponseViewHolder extends RecyclerView.ViewHolder{
        TextView idTextView, nameTextView, distanceTextView;
        ImageView imageView;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);

            //views initialization
            idTextView = itemView.findViewById(R.id.idTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
        }
    }
}
