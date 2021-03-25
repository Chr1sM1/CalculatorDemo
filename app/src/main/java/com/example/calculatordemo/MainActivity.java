package com.example.calculatordemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.googlecode.aviator.AviatorEvaluator;

public class MainActivity extends AppCompatActivity  {
    private Button mbtn_0;//0数字按钮
    private Button mbtn_1;//1数字按钮
    private Button mbtn_2;//2数字按钮
    private Button mbtn_3;//3数字按钮
    private Button mbtn_4;//4数字按钮
    private Button mbtn_5;//5数字按钮
    private Button mbtn_6;//6数字按钮
    private Button mbtn_7;//7数字按钮
    private Button mbtn_8;//8数字按钮
    private Button mbtn_9;//9数字按钮
    private Button mbtn_dot;//小数点按钮
    private Button mbtn_c;//all clear按钮，清除所有的计算。
    private Button mbtn_ce;//Clear Entry，只清除当前这步输入的数字，可重新输入。
    private ImageButton mbtn_back;//back按钮
    private Button mbtn_plus;//+按钮
    private Button mbtn_minus;//-按钮
    private Button mbtn_multply;//*按钮
    private Button mbtn_divide;//除号按钮
    private Button mbtn_equal;//=按钮
    private EditText met_result;

    boolean clear_flag=false;//清空标识

    private static final String[] OPERATOR = new String[]{"+", "-", "×", "÷"};
    private static final String[] NUMBER = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};


    private void ini() {
//        mbtn_0 = findViewById(R.id.btn_0);
//        mbtn_1 = findViewById(R.id.btn_1);
//        mbtn_2 = findViewById(R.id.btn_2);
//        mbtn_3 = findViewById(R.id.btn_3);
//        mbtn_4 = findViewById(R.id.btn_4);
//        mbtn_5 = findViewById(R.id.btn_5);
//        mbtn_6 = findViewById(R.id.btn_6);
//        mbtn_7 = findViewById(R.id.btn_7);
//        mbtn_8 = findViewById(R.id.btn_8);
//        mbtn_9 = findViewById(R.id.btn_9);
//        mbtn_dot = findViewById(R.id.btn_dot);
//        mbtn_c = findViewById(R.id.btn_c);
//        mbtn_c = findViewById(R.id.btn_ce);
//        mbtn_back = findViewById(R.id.btn_back);
//        mbtn_plus = findViewById(R.id.btn_plus);
//        mbtn_minus = findViewById(R.id.btn_minus);
//        mbtn_multply =  findViewById(R.id.btn_multiply);
//        mbtn_divide = findViewById(R.id.btn_divide);
//        mbtn_equal = findViewById(R.id.btn_equal);
        met_result = findViewById(R.id.et_result);
//        met_result.setText("");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridlayout);
        ini();
    }

    public void clearError(View view) {
        checkEqualSign(false);
        String str = met_result.getText().toString();
        if (!isContainsOperator(str)) {
            met_result.setText("0");
            return;
        }
        met_result.setText(str.substring(0, calLastOperationPosition(str) + 1));
    }

    public void backspace(View view) {
        checkEqualSign(false);
        String str = met_result.getText().toString();
        met_result.setText(str.length() == 1 ? "0" : str.substring(0, met_result.getText().length() - 1));
    }

    public void clear(View view) {
        met_result.setText("0");
    }

    @SuppressLint("SetTextI18n")
    public void calculate(View view) {
        checkEqualSign(true);
        String expression = met_result.getText().toString()
                .replace("×", "*")
                .replace("÷", "/");
        String resultStr = met_result.getText().toString();
        try {
            Object result = AviatorEvaluator.execute(expression);
            met_result.setText(resultStr + (resultStr.endsWith(".") ? "0" : "") + "\n" + "=" + result);
        } catch (ArithmeticException e) {
            met_result.setText(resultStr + (resultStr.endsWith(".") ? "0" : "") + "\n" + "=" + "除数不能为0");
        }
    }

    @SuppressLint("SetTextI18n")
    public void inputFormula(View view) {
        checkEqualSign(true);
        Button button = (Button) view;
        String press = button.getText().toString();
        String currentText = met_result.getText().toString();
        if (isEndsWithOperator(currentText) && isContainsOperator(press)) {
            return;
        }
        if (!"0".equals(currentText)) {
            if ("0".equals(currentText.substring(calLastOperationPosition(currentText) + 1))) {
                return;
            }
        }

        if (isContainsOperator(press) && currentText.endsWith(".")) {
            currentText = currentText + "0";
        }

        if ("0".equals(currentText) && isContainsNumeric(press)) {
            met_result.setText(press);
        } else {
            met_result.setText(currentText + press);
        }
    }

    public void processDot(View view) {
        checkEqualSign(true);
        String currentText = met_result.getText().toString();
        if ("0".equals(currentText)) {
            met_result.setText("0.");
            return;
        }
        if (currentText.endsWith(".")) {
            return;
        }
        if (currentText.contains(".") && !isContainsOperator(currentText)) {
            return;
        }
        if (currentText.substring(calLastOperationPosition(currentText + 1)).contains(".")) {
            return;
        }
        met_result.setText(calLastOperationPosition(currentText) == currentText.length() - 1
                ? currentText + "0." : currentText + ".");
    }

    @SuppressLint("SetTextI18n")
    public void processPercent(View view) {
        checkEqualSign(true);
        String currentText = met_result.getText().toString();
        if (!isContainsOperator(currentText)) {
            met_result.setText(String.valueOf(Double.parseDouble(currentText) / 100));
            return;
        }
        if (isContainsOperator(currentText.substring(currentText.length() - 1))) {
            String processStr = currentText.substring(0, currentText.length() - 1);
            String num = processStr.substring(calLastOperationPosition(processStr));
            met_result.setText(currentText + (Double.parseDouble(num) / 100));
        } else {
            String processStr = currentText.substring(0, calLastOperationPosition(currentText) + 1);
            String num = currentText.substring(calLastOperationPosition(currentText) + 1);
            met_result.setText(processStr + (Double.parseDouble(num) / 100));
        }
    }


    private boolean isEndsWithOperator(String str) {
        for (String item : OPERATOR) {
            if (str.endsWith(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isContainsOperator(String str) {
        for (String item : OPERATOR) {
            if (str.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isContainsNumeric(String str) {
        for (String item : NUMBER) {
            if (str.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private void checkEqualSign(boolean usePreResult) {
        String processStr = met_result.getText().toString();
        if (processStr.contains("除数不能为0")) {
            met_result.setText("0");
            return;
        }
        if (met_result.getText().toString().contains("=")) {
            if (!usePreResult) {
                met_result.setText("0");
            } else {
                met_result.setText(processStr.substring(processStr.lastIndexOf("=") + 1));
            }
        }
    }

    private int calLastOperationPosition(String str) {
        int position = 0;
        for (String operator : OPERATOR) {
            int currentOperatorPosition = str.lastIndexOf(operator);
            if (currentOperatorPosition > position) {
                position = currentOperatorPosition;
            }
        }
        return position;
    }
}
