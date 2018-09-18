package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoGroupByDateDisplay;

public class SoDashboardDateAdapter extends RecyclerView.Adapter<SoDashboardDateAdapter.DateViewHolder>{

    private Context context;
    private List<SoGroupByDateDisplay> soGroupByDateDisplayList;
    private SoDashboardDateAdapterListener sddaListener;

    public interface SoDashboardDateAdapterListener {
        void onDateSelected(String dateType, String date);
    }

    public SoDashboardDateAdapter(Context context, SoDashboardDateAdapterListener sddaListener) {
        this.context = context;
        this.sddaListener = sddaListener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_so_dashboard_date, viewGroup, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder dateViewHolder, int i) {
        final SoGroupByDateDisplay item = soGroupByDateDisplayList.get(i);
        dateViewHolder.tvDate.setText(item.getDocumentDate());
        dateViewHolder.cpCount.setText(String.valueOf(item.getCount()));

        dateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sddaListener.onDateSelected(item.getDateType(), item.getDocumentDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (soGroupByDateDisplayList == null) {
            return 0;
        }
        return soGroupByDateDisplayList.size();
    }

    public void setList(List<SoGroupByDateDisplay> soGroupByDateDisplayList){
        this.soGroupByDateDisplayList = soGroupByDateDisplayList;
        notifyDataSetChanged();
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate;
        Chip cpCount;

        public DateViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.li_tv_date);
            cpCount = view.findViewById(R.id.li_cp_count);
        }
    }
}
