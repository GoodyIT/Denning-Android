package it.denning.search.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import it.denning.R;
import it.denning.search.SearchActivity;

/**
 * Created by denningit on 24/04/2017.
 */

public class ClearableAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    boolean justCleared = false;
    private boolean showAlways;

    // if not set otherwise, the default clear listener clears the text in the
    // text view
    private OnClearListener defaultClearListener = new OnClearListener() {

        @Override
        public void onClear() {
            ClearableAutoCompleteTextView et = ClearableAutoCompleteTextView.this;
            et.setText("");
        }
    };

    private OnClearListener onClearListener = defaultClearListener;

    // The image we defined for the clear button
    public Drawable imgClearButton = getResources().getDrawable(
            R.drawable.ic_clear);
    public Drawable imageSearchButton = getResources().getDrawable(R.drawable.ic_search);

    public interface OnClearListener {
        void onClear();
    }

    /* Required methods, not used in this implementation */
    public ClearableAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    /* Required methods, not used in this implementation */
    public ClearableAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /* Required methods, not used in this implementation */
    public ClearableAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setShowAlways(boolean showAlways) {
        this.showAlways = showAlways;
    }

    @Override
    public boolean enoughToFilter() {
        return showAlways || super.enoughToFilter();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        showDropDownIfFocused();
    }

    public void showDropDownIfFocused() {
        if (enoughToFilter() && isFocused() && getWindowVisibility() == View.VISIBLE) {

            showDropDown();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        showDropDownIfFocused();
    }

    void init() {
        this.setThreshold(1);
        hideClearButton();

        this.setDropDownBackgroundDrawable(new ColorDrawable(0xffffff));
        // Set the bounds of the button
        this.setCompoundDrawablesWithIntrinsicBounds(imageSearchButton, null,
                imgClearButton, null);

        // if the clear button is pressed, fire up the handler. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ClearableAutoCompleteTextView et = ClearableAutoCompleteTextView.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > et.getWidth() - et.getPaddingRight() - imgClearButton.getIntrinsicWidth()) {
                    onClearListener.onClear();
                    justCleared = true;
                    v.requestFocus();
                }
                return false;
            }
        });

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    hideClearButton();
                } else {
                    showClearButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setImgClearButton(Drawable imgClearButton) {
        this.imgClearButton = imgClearButton;
    }

    public void setOnClearListener(final OnClearListener clearListener) {
        this.onClearListener = clearListener;
    }

    public void hideClearButton() {
        this.setCompoundDrawables(imageSearchButton, null, null, null);
    }

    public void showClearButton() {
        this.setCompoundDrawablesWithIntrinsicBounds(imageSearchButton, null, imgClearButton, null);
    }
}
