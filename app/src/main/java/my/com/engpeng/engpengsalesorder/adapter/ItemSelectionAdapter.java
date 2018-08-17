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
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingEntry;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder>{

    private Context context;
    private List<ItemPackingEntry> itemPackingEntryList;
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
        final ItemPackingEntry cc = itemPackingEntryList.get(i);
        itemViewHolder.tvCode.setText(cc.getSkuCode());
        itemViewHolder.tvName.setText(cc.getSkuName());
        itemViewHolder.tvPrice.setText("RM9999.99");

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isaListener.onItemSelected(cc.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (itemPackingEntryList == null) {
            return 0;
        }
        return itemPackingEntryList.size();
    }

    public void setItemEntryList(List<ItemPackingEntry> itemPackingEntryList) {
        this.itemPackingEntryList = itemPackingEntryList;
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
