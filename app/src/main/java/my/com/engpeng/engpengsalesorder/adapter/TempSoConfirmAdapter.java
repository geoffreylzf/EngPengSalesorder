package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

public class TempSoConfirmAdapter extends RecyclerView.Adapter<TempSoConfirmAdapter.DetailViewHolder>{

    private Context context;
    private List<TempSalesorderDetailDisplay> tempSalesorderDetailDisplayList;

    public TempSoConfirmAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_temp_so_confirm, viewGroup, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder detailViewHolder, int i) {
        final TempSalesorderDetailDisplay dt = tempSalesorderDetailDisplayList.get(i);

        detailViewHolder.tvCode.setText(dt.getSkuCode());
        detailViewHolder.tvName.setText(dt.getSkuName());
        detailViewHolder.cpQty.setText(StringUtils.getDisplayQty((int) dt.getQty()));
        detailViewHolder.cpWgt.setText(StringUtils.getDisplayWgt(dt.getWeight()));
        detailViewHolder.tvTotalPrice.setText(StringUtils.getDisplayPrice(dt.getTotalPrice() + dt.getTaxAmt()));
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
        TextView tvName, tvCode, tvTotalPrice;
        Chip cpQty, cpWgt;

        DetailViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
            tvTotalPrice = view.findViewById(R.id.li_tv_total_price);
            cpQty = view.findViewById(R.id.li_cp_qty);
            cpWgt = view.findViewById(R.id.li_cp_wgt);
        }
    }
}
