package it.denning.utils;

public interface Loggable {

    public static final String CAN_PERFORM_LOGOUT = "can_perform_logout";

    public boolean isCanPerformLogoutInOnStop();
}