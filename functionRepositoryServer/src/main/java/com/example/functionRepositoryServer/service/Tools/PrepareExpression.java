package com.example.functionRepositoryServer.service.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс подготовки строки к анализу
 */
public class PrepareExpression {
    /**
     * Разбивает выражение на части
     * @return expression list
     */
    public static List<String> decompose(String expression){
        List<String> result = new ArrayList<>();

        char[] temp = expression.toLowerCase()
                .replaceAll(" ", "")
                .toCharArray();

        for (int i = 0; i < temp.length; i++) {
            if(isNum(temp[i])){
                String compare = "";
                int j = i;
                for (; j < temp.length && isNum(temp[j]); j++) {
                    compare += temp[j];
                }
                if(j < temp.length && temp[j] == 'e'){
                    compare += "E";
                    j = j + 1;
                    for (; j < temp.length && isNum(temp[j]); j++) {
                        compare += temp[j];
                    }
                }
                result.add(compare);
                i = j - 1;
            } else if(isLetter(temp[i])){
                String compare = "";
                int j = i;
                compare += temp[i];
                j++;
                for (; j < temp.length && isLetter(temp[j]) || temp[j - 1] == '_'; j++) {
                    compare += temp[j];
                }
                result.add(compare);
                i = j - 1;
            } else {
                result.add(temp[i]+"");
            }
        }

        return result;
    }

    /**
     * Проверяет является ли символ буквой
     * @param value
     * @return is letter
     */
    private static boolean isLetter(char value){
        return (value > 94 && value < 123);
    }

    /**
     * Проверяет является ли символ числом
     * @param value
     * @return is number
     */
    private static boolean isNum(char value){
        return (value > 47 && value < 58) || value == 46;
    }
}
