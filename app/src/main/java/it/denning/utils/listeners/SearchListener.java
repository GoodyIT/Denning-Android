package it.denning.utils.listeners;

public interface SearchListener {

    void prepareSearch();

    void search(String searchQuery);

    void cancelSearch();
}