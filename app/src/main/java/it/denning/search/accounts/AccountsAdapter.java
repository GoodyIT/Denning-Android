package it.denning.search.accounts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.model.Accounts;
import it.denning.search.bank.BankAdapter;
import it.denning.search.utils.OnDetailItemClickListener;

/**
 * Created by denningit on 29/04/2017.
 */

public class AccountsAdapter extends RecyclerView.Adapter {
    Context mContext;
    Accounts accounts;
    Map<Integer, Integer> typeSet;
    OnDetailItemClickListener itemClickListener;

    AccountsAdapter(Accounts accounts, Context context) {
        this.mContext = context;
        this.accounts = accounts;
        typeSet = new HashMap<>();
    }

    public void setItemClickListener(OnDetailItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class HeaderTypeViewHolder  extends RecyclerView.ViewHolder {
        @BindView(R.id.search_header_name)
        TextView headerName;
        @BindView(R.id.search_header_IDNo) TextView headerIDNo;
        @BindView(R.id.search_header_oldID) TextView oldID;
        @BindView(R.id.search_header_chatBtn)
        ImageButton chatBtn;
        @BindView(R.id.search_header_editBtn)
        ImageButton editBtn;

        public HeaderTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LedgerTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.accounts_type) TextView accountType;
        @BindView(R.id.accounts_balance) TextView accountBalance;
        @BindView(R.id.ledger_cardview)
        CardView cardView;

        public LedgerTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            typeSet.put(position, DIConstants.HEADER_TYPE);
            return DIConstants.HEADER_TYPE; //
        } else  {
            typeSet.put(position, DIConstants.LEDGER_TYPE);
            return DIConstants.LEDGER_TYPE; //
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DIConstants.HEADER_TYPE:
                View itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_header, parent, false);

                return new HeaderTypeViewHolder(itemView);
            case DIConstants.LEDGER_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_accounts, parent, false);

                return new LedgerTypeViewHolder(itemView);
            default:
                break;
        }
        return null;
    }

    private void displayHeader(RecyclerView.ViewHolder holder, int position) {
        HeaderTypeViewHolder headerTypeViewHolder = (HeaderTypeViewHolder) holder;
        headerTypeViewHolder.headerName.setText(accounts.fileNo);
        headerTypeViewHolder.headerIDNo.setText(accounts.fileName);
    }

    private void displayAccounts(RecyclerView.ViewHolder holder, final int position) {
        LedgerTypeViewHolder ledgerTypeViewHolder = (LedgerTypeViewHolder) holder;
        ledgerTypeViewHolder.accountType.setText(accounts.ledgerArrayList.get(position-1).accountName);
        ledgerTypeViewHolder.accountBalance.setText(accounts.ledgerArrayList.get(position-1).getBalance());
        ledgerTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, accounts.ledgerArrayList.get(position-1).accountName, accounts.fileNo);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Integer type = typeSet.get(position);
        if (type == DIConstants.HEADER_TYPE) {
            displayHeader(holder, position);
        } else if (type == DIConstants.LEDGER_TYPE) {
            displayAccounts(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + accounts.ledgerArrayList.size();
    }
}
