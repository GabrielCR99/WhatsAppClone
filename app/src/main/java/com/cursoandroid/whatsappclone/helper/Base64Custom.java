package com.cursoandroid.whatsappclone.helper;

import android.util.Base64;

public class Base64Custom {

    //nao precisa instanciar
    public static String base64Code(String text){

        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String base64Decode(String codeText){
        return new String(Base64.decode(codeText, Base64.DEFAULT));
    }
}
