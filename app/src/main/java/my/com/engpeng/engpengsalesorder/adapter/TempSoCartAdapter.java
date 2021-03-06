package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tubb.smrv.SwipeHorizontalMenuLayout;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.tempSalesorderDetail.TempSalesorderDetailDisplay;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

public class TempSoCartAdapter extends RecyclerView.Adapter<TempSoCartAdapter.DetailViewHolder> {

    private Context context;
    private List<TempSalesorderDetailDisplay> tempSalesorderDetailDisplayList;
    private TempSalesorderSummaryAdapterListener tssaListener;

    public interface TempSalesorderSummaryAdapterListener {
        void afterItemDelete(long item_packing_id, int position);
    }

    public TempSoCartAdapter(Context context, TempSalesorderSummaryAdapterListener tssaListener) {
        this.context = context;
        this.tssaListener = tssaListener;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_temp_so_cart, viewGroup, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull DetailViewHolder detailViewHolder, final int i) {
        final TempSalesorderDetailDisplay dt = tempSalesorderDetailDisplayList.get(i);

        detailViewHolder.shml.setSwipeEnable(true);
        detailViewHolder.shml.computeScroll();

        detailViewHolder.tvCode.setText(dt.getSkuCode());
        detailViewHolder.tvName.setText(dt.getSkuName());
        detailViewHolder.cpQty.setText(StringUtils.getDisplayQty((int) dt.getQty()));
        detailViewHolder.cpWgt.setText(StringUtils.getDisplayWgt(dt.getWeight()));

        detailViewHolder.tvTtlPrice.setText(StringUtils.getDisplayPrice(dt.getTotalPrice()));
        detailViewHolder.tvTaxAmt.setText("(" + dt.getTaxCode() + " ~ " + dt.getTaxRate() + "%) " + StringUtils.getDisplayPrice(dt.getTaxAmt()));
        detailViewHolder.tvTotalPriceTax.setText(StringUtils.getDisplayPrice(dt.getTotalPrice() + dt.getTaxAmt()));

        detailViewHolder.cpPriceMethod.setText(dt.getPriceMethod());
        if (dt.getPriceByWeight() == 1) {
            detailViewHolder.cpPriceByWeight.setVisibility(View.VISIBLE);
        } else {
            detailViewHolder.cpPriceByWeight.setVisibility(View.GONE);
        }

        detailViewHolder.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tssaListener.afterItemDelete(dt.getItemPackingId(), detailViewHolder.getAdapterPosition());
            }
        });
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

    public void setListAfterDelete(List<TempSalesorderDetailDisplay> tempSalesorderDetailDisplays, int position) {
        this.tempSalesorderDetailDisplayList = tempSalesorderDetailDisplays;
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, tempSalesorderDetailDisplayList.size());
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCode, tvTtlPrice, tvTotalPriceTax, tvTaxAmt;
        Chip cpPriceMethod, cpPriceByWeight, cpQty, cpWgt;
        SwipeHorizontalMenuLayout shml;
        FloatingActionButton fabDelete;

        DetailViewHolder(View view) {
            super(view);

            shml = view.findViewById(R.id.li_shml);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
            tvTtlPrice = view.findViewById(R.id.li_tv_price);
            tvTaxAmt = view.findViewById(R.id.li_tv_tax_amt);
            tvTotalPriceTax = view.findViewById(R.id.li_tv_total_price);
            cpPriceMethod = view.findViewById(R.id.li_cp_price_method);
            cpPriceByWeight = view.findViewById(R.id.li_cp_price_by_weight);
            cpQty = view.findViewById(R.id.li_cp_qty);
            cpWgt = view.findViewById(R.id.li_cp_wgt);
            fabDelete = view.findViewById(R.id.li_fab_delete);
        }
    }
}
