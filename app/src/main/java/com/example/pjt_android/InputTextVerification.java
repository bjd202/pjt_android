package com.example.pjt_android;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class InputTextVerification implements TextWatcher {

    private String regex;
    private EditText editText;
    private TextInputLayout textInputLayout;

    public InputTextVerification(String regex, EditText editText, TextInputLayout textInputLayout) {
        this.regex = regex;
        this.editText = editText;
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        if(!Pattern.matches(regex, editText.getText().toString().trim())){
            textInputLayout.setError("올바르지 않은 형식입니다.");
        }else{
            textInputLayout.setError(null);
        }
    }
}
