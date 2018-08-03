package it.denning.search.paymentrecord;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.PaymentRecord;
import it.denning.model.PaymentSection;
import it.denning.model.ValueDescription;
import it.denning.search.utils.SearchAdapter;

/**
 * Created by denningit on 2017-12-24.
 */

public class PaymentRecordAdapter extends SectioningAdapter {
    private final PaymentRecord paymentRecord;

    public PaymentRecordAdapter(PaymentRecord paymentRecord) {
        this.paymentRecord = paymentRecord;
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;
        @BindView(R.id.second_textview)
        TextView secondTextView;
        @BindView(R.id.third_textview)
        TextView thirdTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class PaymentFirstViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.first_textview)
        TextView firstTextView;
        @BindView(R.id.second_textview)
        TextView secondTextView;
        @BindView(R.id.third_textview)
        TextView thirdTextView;

        public PaymentFirstViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class PaymentViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.first_textview)
        TextView firstTextView;
        @BindView(R.id.second_textview)
        TextView secondTextView;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {
        return 3;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = 1;
        switch (sectionIndex) {
            case 0:
                count = paymentRecord.section1.size();
                break;
            case 1:
                count = paymentRecord.section2.size();
                break;
            case 2:
                count = paymentRecord.section3.size();
                break;
        }

        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        return sectionIndex;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View view;
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
        switch (itemUserType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_payment_first, parent, false);
                itemViewHolder = new PaymentFirstViewHolder(view);
                break;
            case 1:
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_payment, parent, false);
                itemViewHolder = new PaymentViewHolder(view);
                break;
        }

        return itemViewHolder;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_three_column_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        switch (sectionIndex) {
            case 0:
                displayFirstSection(viewHolder, itemIndex);
                break;
            case 1:
                displayItemSection(viewHolder, paymentRecord.section2.get(itemIndex));
                break;
            case 2:
                displayItemSection(viewHolder, paymentRecord.section3.get(itemIndex));
                break;
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        switch (sectionIndex) {
            case 0:
                displayHeader(headerViewHolder, "Date paid", "Paid to", "Amount");
                break;
            default:
                displayHeader(headerViewHolder, "", "", "");
                break;
        }
    }

    private void displayHeader(HeaderViewHolder viewHolder, String first, String second, String third) {
        viewHolder.firstTitle.setText(first);
        viewHolder.secondTextView.setText(second);
        viewHolder.thirdTextView.setText(third);
    }

    private void displayFirstSection(SectioningAdapter.ItemViewHolder viewHolder, int itemIndex) {
        PaymentFirstViewHolder holder = (PaymentFirstViewHolder) viewHolder;
        PaymentSection section1 = paymentRecord.section1.get(itemIndex);
        holder.firstTextView.setText(DIHelper.getOnlyDateFromDateTime(section1.dtDatePaid));
        holder.secondTextView.setText(section1.strPaidTo);
        holder.thirdTextView.setText(section1.decAmount);
    }

    private void displayItemSection(SectioningAdapter.ItemViewHolder viewHolder, ValueDescription section) {
        PaymentViewHolder holder = (PaymentViewHolder) viewHolder;
        holder.firstTextView.setText(section.strDescription);
        holder.secondTextView.setText(section.strValue);
    }
}