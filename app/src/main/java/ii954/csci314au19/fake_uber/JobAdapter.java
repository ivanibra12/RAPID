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

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder>
{
    //variable declaration
    private Context mCtx;
    private List<Job> jobList;
    private SimpleDateFormat simpleDateFormat;

    @NonNull
    @Override
    public JobAdapter.JobViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_job, null);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return new JobAdapter.JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.JobViewHolder holder, int position)
    {
        //getting the membership
        Job job = jobList.get(position);

        //binding data with holder views
        holder.idTextView.setText(job.getJobid());
        holder.vehicleTextView.setText("Vehicle : "+job.getRequest().getVehicle().getNickname()+" ("+job.getRequest().getVehicle().getRegNum()+")");
        holder.jobDateTextView.setText("Job date : "+simpleDateFormat.format(job.getJobDateTime()));
        holder.jobStatusTextView.setText("Job status : "+job.getStatus());

    }

    @Override
    public int getItemCount()
    {
        return jobList.size();
    }


    public JobAdapter(Context mCtx, List<Job> jobList) {
        this.mCtx = mCtx;
        this.jobList = jobList;
    }


    class JobViewHolder extends RecyclerView.ViewHolder{
        TextView idTextView, vehicleTextView, jobDateTextView, jobStatusTextView;
        ImageView imageView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            //views initialization
            idTextView = itemView.findViewById(R.id.idTextView);
            vehicleTextView = itemView.findViewById(R.id.vehicleTextView);
            jobDateTextView = itemView.findViewById(R.id.jobDateTextView);
            jobStatusTextView = itemView.findViewById(R.id.jobStatusTextView);

        }
    }
}
