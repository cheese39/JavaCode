package com.example.classmission;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.classmission.util.MathTools;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_0;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;
    Button btn_6;
    Button btn_7;
    Button btn_8;
    Button btn_9;

    Button btn_point;// 小数点
    Button btn_divide;// 除以
    Button btn_multiply;// 乘以
    Button btn_minus;// 减去
    Button btn_pluse;// 加
    Button btn_equal;// 等于

    Button btn_clear;
    Button btn_del;

    EditText et_showview;
    //用于记录运算符，#代表没有输入运算符
    char operator = '#';
    //用于记录运算符前一个数字
    double num = 0;
    //用于计算输入的数字中是否包含小数点
    boolean hasdot = false;
    //用于判断num中是否已经存储了一个数字
    boolean hasnum = false;
    boolean specificState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_point = findViewById(R.id.btn_point);// 小数点
        btn_divide = findViewById(R.id.btn_divide);// 除以
        btn_multiply = findViewById(R.id.btn_multiply);// 乘以
        btn_minus = findViewById(R.id.btn_minus);// 减去
        btn_pluse = findViewById(R.id.btn_pluse);// 加
        btn_equal = findViewById(R.id.btn_equal);// 等于

        btn_clear = findViewById(R.id.btn_clear);
        btn_del = findViewById(R.id.btn_del);
        et_showview = findViewById(R.id.showView);

        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);

        btn_point.setOnClickListener(this);
        btn_divide.setOnClickListener(this);
        btn_multiply.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_pluse.setOnClickListener(this);
        btn_equal.setOnClickListener(this);

        btn_clear.setOnClickListener(this);
        btn_del.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        StringBuilder sb = new StringBuilder();
        sb.append(et_showview.getText().toString());
        switch (v.getId()) {
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                if(sb.length() == 1){
                    int tmp = Integer.parseInt(sb.toString());
                    if (tmp == 0) sb.delete(0, 1);
                }
                if(specificState){
                    sb.delete(0, sb.length());
                    specificState = false;
                }
                sb.append(((Button) v).getText());
                break;
            case R.id.btn_point:
                if (!specificState) {
                    sb.append('.');
                    hasdot = true;
                }
                break;
            case R.id.btn_pluse:
            case R.id.btn_minus:
            case R.id.btn_multiply:
            case R.id.btn_divide:
                if (specificState) {
                    Toast.makeText(this, "请点击数字", Toast.LENGTH_SHORT);
                    break;
                }
                if (operator == '#') {
                    operator = ((Button) v).getText().charAt(0);
                    num = Double.parseDouble(sb.toString());
                    hasnum = true;
                    specificState = true;
                    hasdot = false;
                } else {
                    double result = MathTools.computing(num,
                            Double.parseDouble(sb.toString()), operator);
                    operator = ((Button) v).getText().charAt(0);
                    num = result;
                    sb.delete(0, sb.length());
                    sb.append(result);
                    specificState = true;
                }
                break;
            case R.id.btn_equal:
                if (specificState)
                    break;
                if (hasnum) {
                    double result = MathTools.computing(num,
                            Double.parseDouble(sb.toString()), operator);
                    num = result;
                    init();
                    sb.delete(0, sb.length());
                    sb.append(result);
                }
                break;
            case R.id.btn_del:
                if (specificState)
                    break;
                sb.delete(sb.length() - 1, sb.length());
                break;
            case R.id.btn_clear:
                init();
                sb.delete(0, sb.length());
        }
        et_showview.setText(sb.toString());
    }

    private void init(){
        operator = '#';
        hasnum = hasdot = false;
        specificState = true;
    }
}
