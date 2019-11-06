package ii954.csci314au19.fake_uber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehiclesViewHolder>
{
    private Context mCtx;

    private List<Car> vehiclesList;

    @Override
    public VehiclesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_vehicle, null);
        return new VehiclesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VehiclesViewHolder holder, int position)
    {
        //getting the product of the specified position
        Car vehicle = vehiclesList.get(position);

        //binding the data with the viewholder views
        //brand
        if (vehicle.getBrand() == null)
            holder.textViewBrand.setText("Brand : ");
        else
            holder.textViewBrand.setText("Brand : " + vehicle.getBrand());

        //model
        if (vehicle.getModel() == null)
            holder.textViewvModel.setText("Model: ");
        else
            holder.textViewvModel.setText("Model: " + vehicle.getModel());

        //reg num
        if (vehicle.getRegNum() == null)
            holder.textViewvRegNo.setText("Registration Number: ");
        else
            holder.textViewvRegNo.setText("Registration Number: " + vehicle.getRegNum());

        //nickname
        if (vehicle.getNickname() == null)
            holder.textViewvNickname.setText("Nickname");
        else
            holder.textViewvNickname.setText("" + vehicle.getNickname());
    }

    @Override
    public int getItemCount() {
        return vehiclesList.size();
    }

    public VehicleAdapter(Context mCtx, List<Car> vehiclesList) {
        this.mCtx = mCtx;
        this.vehiclesList = vehiclesList;
    }

    class VehiclesViewHolder extends RecyclerView.ViewHolder {
        TextView textViewvModel, textViewvRegNo, textViewvNickname, textViewBrand;

        public VehiclesViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBrand = itemView.findViewById(R.id.textViewBrand);
            textViewvModel = itemView.findViewById(R.id.textViewvModel);
            textViewvRegNo = itemView.findViewById(R.id.textViewvRegNo);
            textViewvNickname = itemView.findViewById(R.id.textViewvNickname);
        }
    }

}
