package com.cursoandroid.whatsappclone.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cursoandroid.whatsappclone.fragments.ChatFragment;
import com.cursoandroid.whatsappclone.fragments.ContactsFragment;


public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] tabsTitles = {"CHATS", "CONTACTS"};


    public TabAdapter(FragmentManager fm) {
        super(fm);
    }


    //retorna para o pager, os fragments, nesse caso as conversas e contatos
    @Override
    public Fragment getItem(int i) {

        Fragment fragment = null;

        switch (i){
            case 0:
                fragment = new ChatFragment();
                break;

            case 1:
                fragment = new ContactsFragment();
                break;
        }
        return fragment;
    }

    //retorna a quantidade de abas
    @Override
    public int getCount() {


        return tabsTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabsTitles[position];
    }
}
