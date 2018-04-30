package com.ljy.musicplayer.biomusicplayer;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //사용자이름
        setTitle("홍길동");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.bg_gradient));


        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout =findViewById(R.id.tabs);


        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());


        //탭 추가
        adapter.addFragment(new Tab1Fragment(),"Tab1");
        adapter.addFragment(new Tab2Fragment(),"Tab2");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_face_scan) {
            Toast.makeText(this, "얼굴인식", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
}
