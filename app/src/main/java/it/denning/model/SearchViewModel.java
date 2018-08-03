package it.denning.model;

/**
 * Created by denningit on 24/04/2017.
 */

public class SearchViewModel {public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int AUDIO_TYPE=2;

    public int type;
    public int data;
    public String text;

    public SearchViewModel(int type, String text, int data)
    {
        this.type=type;
        this.data=data;
        this.text=text;
    }
}
