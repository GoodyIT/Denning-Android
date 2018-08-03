package it.denning.utils.listeners;

import com.quickblox.q_municate_core.service.QBService;

public interface ServiceConnectionListener {

    void onConnectedToService(QBService service);
}