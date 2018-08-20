package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;

import static my.com.engpeng.engpengsalesorder.Global.getDisplayPrice;

public class TempSalesorderSummaryAdapter extends RecyclerView.Adapter<TempSalesorderSummaryAdapter.DetailViewHolder> {

    private Context context;
    private List<TempSalesorderDetailDisplay> tempSalesorderDetailDisplayList;

    public TempSalesorderSummaryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_card_view, viewGroup, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder detailViewHolder, int i) {
        final TempSalesorderDetailDisplay dt = tempSalesorderDetailDisplayList.get(i);

        detailViewHolder.tvCode.setText(dt.getSkuCode());
        detailViewHolder.tvName.setText(dt.getSkuName());
        detailViewHolder.tvPrice.setText(getDisplayPrice(dt.getId()));
    }

    @Override
    public int getItemCount() {
        if (tempSalesorderDetailDisplayList == null) {
            return 0;
        }
        return tempSalesorderDetailDisplayList.size();
    }

    public void setList(List<TempSalesorderDetailDisplay> tempSalesorderDetailDisplays) {
        this.tempSalesorderDetailDisplayList = tempSalesorderDetailDisplays;
        notifyDataSetChanged();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCode, tvPrice;

        public DetailViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
            tvPrice = view.findViewById(R.id.li_tv_tertiary);
        }
    }
}
