package uz.texnopos.elektrolife.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import uz.texnopos.elektrolife.R;

public class MyKeyboard extends ConstraintLayout implements View.OnClickListener {

    // constructors
    public MyKeyboard(Context context) {
        this(context, null, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    SparseArray<String> keyValues = new SparseArray<>();

    // Our communication link to the EditText
    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.my_keyboard, this, true);
        // keyboard keys (buttons)
        CardView mButton1 = findViewById(R.id.btOne);
        CardView mButton2 = findViewById(R.id.btTwo);
        CardView mButton3 = findViewById(R.id.btThree);
        CardView mButton4 = findViewById(R.id.btFour);
        CardView mButton5 = findViewById(R.id.btFive);
        CardView mButton6 = findViewById(R.id.btSix);
        CardView mButton7 = findViewById(R.id.btSeven);
        CardView mButton8 = findViewById(R.id.btEight);
        CardView mButton9 = findViewById(R.id.btNine);
        CardView mButton0 = findViewById(R.id.btZero);
        ImageView mButtonBackspace = findViewById(R.id.btBackspace);
        ImageView mButtonFingerPrint = findViewById(R.id.btFingerPrint);

        // set button click listeners
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonBackspace.setOnClickListener(this);
        mButtonFingerPrint.setOnClickListener(this);

        // map buttons IDs to input strings
        keyValues.put(R.id.btOne, "1");
        keyValues.put(R.id.btTwo, "2");
        keyValues.put(R.id.btThree, "3");
        keyValues.put(R.id.btFour, "4");
        keyValues.put(R.id.btFive, "5");
        keyValues.put(R.id.btSix, "6");
        keyValues.put(R.id.btSeven, "7");
        keyValues.put(R.id.btEight, "8");
        keyValues.put(R.id.btNine, "9");
        keyValues.put(R.id.btZero, "0");
        keyValues.put(R.id.btFingerPrint, "\n");
    }

    @Override
    public void onClick(View v) {

        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == R.id.btBackspace) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }
}

