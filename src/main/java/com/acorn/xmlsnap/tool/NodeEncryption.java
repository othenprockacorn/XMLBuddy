package com.acorn.xmlsnap.tool;

public class NodeEncryption {


    public String decryptString(String s){

        char[] charArray = s.toCharArray();
        char tmpChar;

        //the 7th and 10th digit flip-flopped

        tmpChar = charArray[9];
        charArray[9] = charArray[6];
        charArray[6] = tmpChar;

        //the 1st and 6th digit flip-flopped

        tmpChar = charArray[5];

        charArray[5] = charArray[0];
        charArray[0] = tmpChar;

        return String.valueOf(charArray).replaceFirst("^0+(?!$)", "");


    }



}
