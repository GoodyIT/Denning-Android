package it.denning.navigation.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;

import butterknife.BindView;
import it.denning.R;
import it.denning.general.DIMessageInterface;
import it.denning.general.DIService;
import it.denning.model.ChatContactModel;
import it.denning.model.ChatFirmModel;
import it.denning.navigation.message.utils.MessageStaffAdapter;
import it.denning.utils.KeyboardUtils;

/**
 * Created by hothongmee on 11/11/2017.
 */

public class DenningContact extends MessageBaseFragment {
    protected static final String TAG = DenningContact.class.getSimpleName();
    protected static final int LOADER_ID = DenningContact.class.hashCode();

    @BindView(R.id.button_contact)
    ImageButton btnStaff;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_chat, container, false);
        activateButterKnife(view);
        setupList();
        initSearchView();
        loadUsersFromServer();
        btnStaff.setImageResource(R.drawable.ic_contact_selected);
        return view;
    }

    void setupList() {
        messageAdapter = new MessageStaffAdapter(new ArrayList<ChatFirmModel>(), this, this);
        chatListView.setLayoutManager(new StickyHeaderLayoutManager());
        chatListView.setHasFixedSize(true);
        chatListView.setItemAnimator(new DefaultItemAnimator());
        chatListView.setAdapter(messageAdapter);

        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardUtils.hideKeyboard(v);
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        addActions();
//        if (messageAdapter.getCount() == 0){
//            loadUsersFromServer();
//        }
    }

    public void loadUsersFromServer() {
        baseActivity.showProgress();

//        DIService service = new DIService(getContext(), myDialogList);
        DIService.fetchAllContactsFromServer(null, new DIMessageInterface() {
            @Override
            public void onSuccess(final ChatContactModel chatContactModel) {
                messageAdapter.setChatContactModel(chatContactModel);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyAllSectionsDataSetChanged();
                            baseActivity.hideProgress();
                            checkEmptyList(messageAdapter.getCount());
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    baseActivity.hideProgress();
                    }
                });
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

}
