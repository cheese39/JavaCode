package com.example.classmission.util;

public class MathTools {
    public static double computing(double a, double b, char operator) {
        double result = 0;
        switch (operator) {
            case '＋':
                result = a + b;
                break;
            case '×':
                result = a * b;
                break;
            case '÷':
                result = a / b;
                break;
            case'－':
                result = a - b;
        }
        return result;
    }
}
