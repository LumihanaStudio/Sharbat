package kr.edcan.sharbat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import kr.edcan.sharbat.R;
import kr.edcan.sharbat.adapters.CommonRecyclerAdapter;
import kr.edcan.sharbat.adapters.SettingsAdapter;
import kr.edcan.sharbat.models.MailData;
import kr.edcan.sharbat.models.MainRecycleData;
import kr.edcan.sharbat.utils.DataManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int tab_on[] = {R.drawable.btn_tab_mailbox_on, R.drawable.btn_tab_home_on, R.drawable.btn_tab_settings_on};
    int tab_off[] = {R.drawable.btn_tab_mailbox_off, R.drawable.btn_tab_home_off, R.drawable.btn_tab_settings_off};
    ImageView tab1, tab2, tab3;
    ImageView[] tabs;
    ViewPager pager;
    DataManager manager;
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new DataManager();
        manager.initializeManager(getApplicationContext());
        setDefault();
    }

    private void setDefault() {
        tab1 = (ImageView) findViewById(R.id.main_tab_mailbox);
        tab2 = (ImageView) findViewById(R.id.main_tab_home);
        tab3 = (ImageView) findViewById(R.id.main_tab_settings);
        pager = (ViewPager) findViewById(R.id.main_viewpager);
        pager.setAdapter(new PagerAdapterClass(this));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabs = new ImageView[]{tab1, tab2, tab3};
        for (ImageView view : tabs) view.setOnClickListener(this);
        setTab(1);
        pager.setCurrentItem(1);
    }

    private void setTab(int position) {
        if (position > tabs.length) return;
        for (int i = 0; i < tabs.length; i++) {
            if (i == position) {
                tabs[i].setImageResource(tab_on[i]);
            } else tabs[i].setImageResource(tab_off[i]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_tab_mailbox:
                setTab(0);
                pager.setCurrentItem(0);
                break;
            case R.id.main_tab_home:
                setTab(1);
                pager.setCurrentItem(1);
                break;
            case R.id.main_tab_settings:
                setTab(2);
                pager.setCurrentItem(2);
                break;

        }
    }

    class PagerAdapterClass extends PagerAdapter {
        Context context;
        private LayoutInflater mInflater;

        public PagerAdapterClass(Context c) {
            super();
            mInflater = LayoutInflater.from(c);
            this.context = c;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            switch (position) {
                case 0:
                    v = mInflater.inflate(R.layout.main_first_page, null);
                    setFirstPage(v);
                    break;
                case 1:
                    v = mInflater.inflate(R.layout.main_second_page, null);
                    setSecondPage(v);
                    break;
                case 2:
                    v = mInflater.inflate(R.layout.main_third_page, null);
                    setThirdPage(v);
                    break;
            }
            ((ViewPager) pager).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager) pager).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


        /* Replaces Fragment, setting Page 1,2,3*/
        void setFirstPage(View v) {
        }

        void setSecondPage(View v) {
            RecyclerView view = (RecyclerView) v.findViewById(R.id.main_recyclerView);
            view.setHasFixedSize(true);
            view.setLayoutManager(new LinearLayoutManager(context));
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<MailData> results = realm.where(MailData.class).findAll();
            realm.commitTransaction();
            ArrayList<MainRecycleData> arr = new ArrayList<>();
            for(MailData data : results){
                arr.add(new MainRecycleData("asdf", "asdf", data.getTitle(), data.getContent(), new Date(System.currentTimeMillis()), false));
            }
            view.setAdapter(new CommonRecyclerAdapter(context, arr));
        }

        void setThirdPage(View v) {
            ListView settings = (ListView) v.findViewById(R.id.main_settings_listview);
            ArrayList<String> arr = new ArrayList<>();
            arr.add("전체 메일 새로고침");
            arr.add("전체 메일 삭제");
            arr.add("메일박스 설정");
            arr.add("메일 서명 설정");
            arr.add("알림 설정");
            arr.add("스팸 메일에 대한 정책 설정");
            arr.add("앱 시작 시 암호걸기");
            arr.add("앱 버전 정보");
            settings.setAdapter(new SettingsAdapter(context, arr));
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View header = inflater.inflate(R.layout.main_settings_header, null);
            settings.addHeaderView(header);
            TextView userName = (TextView) header.findViewById(R.id.main_settings_header_username);
            TextView email = (TextView) header.findViewById(R.id.main_settings_header_email);
            TextView logout = (TextView) header.findViewById(R.id.main_settings_header_logout);
            userName.setText("Junseok Oh");
            String s = manager.getString(DataManager.ID);
            switch (manager.getInt(DataManager.LOGIN_TYPE)){
                case 0:
                    if(!(s.contains("@gmail.com"))) s+="@gmail.com";
                    break;
                case 1:
                    if(!(s.contains("@naver.com"))) s+="@naver.com";
                    break;
            }
            email.setText(s);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("로그아웃")
                            .content("저장된 데이터를 모두 삭제하고 로그아웃합니다")
                            .positiveText("확인")
                            .negativeText("취소")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    manager.removeAllData();
                                    realm.beginTransaction();
                                    realm.deleteAll();
                                    realm.commitTransaction();
                                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                    finish();
                                }
                            }).show();
                }
            });
            settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    switch (position) {
                        case 1:
                        /* 전체 메일 새로고침 */
                            break;
                        case 2:
                        /* 전체 메일 삭제 */
                            break;
                        case 3:
                        /* 메일박스 설정 */
                            break;
                        case 4:
                        /* 메일 서명 설정*/
                            break;
                        case 5:
                        /* 알림 설정 */
                            break;
                        case 6:
                        /* 스팸 메일에 대한 정책 설정 */
                            context.startActivity(new Intent(context, SpamMailSettingActivity.class));
                            break;
                        case 7:
                        /* 앱 시작 시 암호걸기 */
                            context.startActivity(new Intent(context, PasswordAuthActivity.class));
                            break;
                        case 8:
                        /*앱 버전 정보*/
                            context.startActivity(new Intent(context, DeveloperActivity.class));
                            break;
                    }
                }
            });
        }
    }
}


