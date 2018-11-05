package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.List;

import my.com.engpeng.engpengsalesorder.Global;
import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.itemPacking.ItemPackingDisplay;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder> {

    private Context context;
    private List<ItemPackingDisplay> itemPackingDisplays;
    private ItemSelectionAdapterListener isaListener;

    public interface ItemSelectionAdapterListener {
        void onItemSelected(long id,
                            double factor,
                            int priceByWeight,
                            String priceMethod,
                            long priceSettingId,
                            double price,
                            long taxCodeId,
                            double taxRate,
                            double taxAmt);
    }

    public ItemSelectionAdapter(Context context, ItemSelectionAdapterListener isaListener) {
        this.context = context;
        this.isaListener = isaListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_item_selection, viewGroup, false);
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
        itemViewHolder.tvWeight.setText(StringUtils.getDisplayWgt(item.getFactor()) + priceByWeightIndicator);

        final double taxAmt = Math.round(price * item.getTaxRate()) / 100.0;
        double priceWithTax = price + taxAmt;

        itemViewHolder.tvTax.setText("(" + item.getTaxCode() + " ~ " + item.getTaxRate() + "%) " + StringUtils.getDisplayPrice(taxAmt));
        itemViewHolder.tvPrice.setText(priceIndicator + StringUtils.getDisplayPrice(price));
        itemViewHolder.tvPriceWithTax.setText(StringUtils.getDisplayPrice(priceWithTax));

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isaListener.onItemSelected(
                        item.getId(),
                        item.getFactor(),
                        item.getPriceByWeight(),
                        f_priceMethod,
                        f_priceSettingId,
                        f_price,
                        item.getTaxCodeId(),
                        item.getTaxRate(),
                        taxAmt);
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

        TextView tvName, tvCode, tvWeight;
        TextView tvPrice, tvTax, tvPriceWithTax;

        public ItemViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_item_name);
            tvCode = view.findViewById(R.id.li_tv_item_code);
            tvWeight = view.findViewById(R.id.li_tv_weight);
            tvPrice = view.findViewById(R.id.li_tv_price);
            tvTax = view.findViewById(R.id.li_tv_tax);
            tvPriceWithTax = view.findViewById(R.id.li_tv_price_with_tax);
        }
    }
}
