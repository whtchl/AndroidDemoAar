package com.jdjz.lrucachedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jdjz.contacts.ContactsActivity;
import com.jdjz.db.DBActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_lrucache)
    Button btnLrucache;
    @BindView(R.id.btn_contacts)
    Button btnContacts;
    @BindView(R.id.btn_db)
    Button btnDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

    }


    @OnClick({R.id.btn_lrucache, R.id.btn_contacts,R.id.btn_db})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lrucache:
                startActivity(new Intent(this, LrucacheDemo.class));
                break;
            case R.id.btn_contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                break;
            case R.id.btn_db:
                startActivity(new Intent(this, DBActivity.class));
                break;
        }
    }

}
