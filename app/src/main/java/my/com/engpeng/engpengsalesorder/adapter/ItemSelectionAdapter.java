package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingDisplay;

import static my.com.engpeng.engpengsalesorder.Global.getDisplayPrice;
import static my.com.engpeng.engpengsalesorder.Global.getDisplayWgt;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder> {

    private Context context;
    private List<ItemPackingDisplay> itemPackingDisplays;
    private ItemSelectionAdapterListener isaListener;

    public interface ItemSelectionAdapterListener {
        void onItemSelected(long id, double factor, int priceByWeight, String priceMethod, long priceSettingId, double price);
    }

    public ItemSelectionAdapter(Context context, ItemSelectionAdapterListener isaListener) {
        this.context = context;
        this.isaListener = isaListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_2_column_card_view, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final ItemPackingDisplay item = itemPackingDisplays.get(i);

        double price;
        long priceSettingId;
        String priceMethod;
        String priceIndicator = "";
        String priceByWeightIndicator = "";

        if (item.getCustomerPrice() != 0) {
            price = item.getCustomerPrice();
            priceSettingId = item.getCustomerPriceSettingId();
            priceMethod = Global.PriceMethod.CUSTOMER.toString();
            priceIndicator = "*";
        } else {
            price = item.getStandardPrice();
            priceSettingId = item.getStandardPriceSettingId();
            if (item.getStandardPrice() != 0) {
                priceMethod = Global.PriceMethod.STANDARD.toString();
            } else {
                priceMethod = Global.PriceMethod.SELF.toString();
            }
        }

        if (item.getPriceByWeight() == 1) {
            priceByWeightIndicator = "*";
        }

        final String f_priceMethod = priceMethod;
        final long f_priceSettingId = priceSettingId;
        final double f_price = price;

        itemViewHolder.tvCode.setText(item.getSkuCode());
        itemViewHolder.tvName.setText(item.getSkuName());
        itemViewHolder.tvPrice.setText(getDisplayPrice(price) + priceIndicator);
        itemViewHolder.tvWeight.setText(getDisplayWgt(item.getFactor()) + priceByWeightIndicator);

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isaListener.onItemSelected(item.getId(), item.getFactor(), item.getPriceByWeight(), f_priceMethod, f_priceSettingId, f_price);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (itemPackingDisplays == null) {
            return 0;
        }
        return itemPackingDisplays.size();
    }

    public void setItemEntryList(List<ItemPackingDisplay> itemPackingDisplays) {
        this.itemPackingDisplays = itemPackingDisplays;
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCode, tvWeight, tvPrice;

        public ItemViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
            tvWeight = view.findViewById(R.id.li_tv_weight);
            tvPrice = view.findViewById(R.id.li_tv_tertiary);
        }
    }
}
