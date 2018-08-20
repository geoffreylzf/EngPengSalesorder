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
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingDisplay;

import static my.com.engpeng.engpengsalesorder.Global.getDisplayPrice;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder> {

    private Context context;
    private List<ItemPackingDisplay> itemPackingDisplays;
    private ItemSelectionAdapterListener isaListener;

    public interface ItemSelectionAdapterListener {
        void onItemSelected(Long id);
    }

    public ItemSelectionAdapter(Context context, ItemSelectionAdapterListener isaListener) {
        this.context = context;
        this.isaListener = isaListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_card_view, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final ItemPackingDisplay item = itemPackingDisplays.get(i);

        double price = 0;
        long priceSettingId = 0;
        boolean isStandard = true;
        String priceIndicator = "*";

        if(item.getCustomerPrice() != 0){
            price = item.getCustomerPrice();
            priceSettingId = item.getCustomerPriceSettingId();
            isStandard = false;
            priceIndicator = "";
        }else{
            price = item.getStandardPrice();
            priceSettingId = item.getStandardPriceSettingId();
        }

        itemViewHolder.tvCode.setText(item.getSkuCode());
        itemViewHolder.tvName.setText(item.getSkuName());
        itemViewHolder.tvPrice.setText(getDisplayPrice(price) + priceIndicator);

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isaListener.onItemSelected(item.getId());
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

        TextView tvName, tvCode, tvPrice;

        public ItemViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
            tvPrice = view.findViewById(R.id.li_tv_tertiary);
        }
    }
}
