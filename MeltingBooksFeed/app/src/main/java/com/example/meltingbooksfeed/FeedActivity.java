package com.example.meltingbooksfeed;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class FeedActivity extends AppCompatActivity {
    //피드 리사이클
    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;
    private List<FeedItem> feedList;

    //하단 메뉴
    private BottomNavigationView bottomNavigationView;
    private ImageView gradientCircle;
    private int[] iconPositions;


    private int commentCounter = 0; // 초기 댓글 수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            //상태바 디자인 설정--------------------------------------------------------
            // 상태바를 완전히 투명하게 만들어서 배경이 보이도록 설정
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT); // 상태바를 투명하게 설정

            // 레이아웃이 상태바 영역까지 확장되도록 설정
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            // 상태바 아이콘 & 글자를 검정색으로 변경
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            //피드 리사이클 뷰---------------------------------------------------
            feedRecyclerView = findViewById(R.id.feedRecyclerView);
            feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // 더미 데이터 추가
            feedList = new ArrayList<>();

            // 나중에 서버 통신으로 바꿀시 Null 처리 어떻게 할 것인지 생각해보기.
            // 이미지 게시글
            feedList.add(new FeedItem("Alice", "이 고양이좀 봐~! 책읽고 있어", "2025-03-10", "https://i.imgur.com/iWf9Yuh.jpeg"));

            // 투표 게시글
            /*List<String> pollOptions = Arrays.asList("재미있다", "그저 그렇다", "별로다");x
            feedList.add(new FeedItem("Bob", "이번 책 어땠어?", "2025-03-09", null, pollOptions));*/

            //추후 서버 연결해서 바꾸는 식으로 조정.
            feedList.add(new FeedItem("Alice", "오늘 읽은 책 너무 재밌었어!------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "2025-03-10", null));
            feedList.add(new FeedItem("Bob", "독서가 정말 힘이 되네.", "2025-03-09",null));
            feedList.add(new FeedItem("Charlie", "이번 책도 완독했다!", "2025-03-08",null));
            feedList.add(new FeedItem("Alice", "오늘 읽은 책 너무 재밌었어!", "2025-03-10",null));
            feedList.add(new FeedItem("Bob", "독서가 정말 힘이 되네.", "2025-03-09",null));
            feedList.add(new FeedItem("Charlie", "이번 책도 완독했다!", "2025-03-08",null));
            feedList.add(new FeedItem("Alice", "오늘 읽은 책 너무 재밌었어!", "2025-03-10",null));
            feedList.add(new FeedItem("Bob", "독서가 정말 힘이 되네.", "2025-03-09",null));
            feedList.add(new FeedItem("Charlie", "이번 책도 완독했다!", "2025-03-08",null));
            feedList.add(new FeedItem("Alice", "오늘 읽은 책 너무 재밌었어!", "2025-03-10",null));
            feedList.add(new FeedItem("Bob", "독서가 정말 힘이 되네.", "2025-03-09",null));
            feedList.add(new FeedItem("Charlie", "이번 책도 완독했다!", "2025-03-08",null));

            feedAdapter = new FeedAdapter(this, feedList);
            feedRecyclerView.setAdapter(feedAdapter);

            //하단 메뉴 애니메이션--------------------------------------------------------------------------- 나중에 따로 코드 분리 해야할듯
            gradientCircle = findViewById(R.id.gradientCircle);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);

            // 기본 애니메이션 제거 (Shift Mode 제거)
            bottomNavigationView.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_UNLABELED);

            // 아이템 클릭 시 애니메이션 방지
            bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // 클릭된 아이템의 강조 효과 제거 (예: Ripple Effect 무시)
                    View view = bottomNavigationView.findViewById(item.getItemId());
                    view.setPressed(false);
                    view.setSelected(false);

                    // 원하는 작업 수행
                    return true;
                }
            });

            // 아이콘의 위치를 구하기 위해 뷰가 그려진 후 위치 계산
            bottomNavigationView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                int menuItemCount = bottomNavigationView.getMenu().size();
                iconPositions = new int[menuItemCount];

                for (int i = 0; i < menuItemCount; i++) {
                    View itemView = bottomNavigationView.findViewById(bottomNavigationView.getMenu().getItem(i).getItemId());
                    iconPositions[i] = (int) itemView.getX() + (itemView.getWidth() / 2) - (gradientCircle.getWidth() / 2);
                }

                // 처음 앱이 실행될 때 원을 첫 번째 아이콘 아래로 이동시키기
                /*if (iconPositions.length > 0) {
                    gradientCircle.setX(iconPositions[0]);  // 첫 번째 아이콘 (Feed) 위치로 설정
                }*/
            });

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int position = getSelectedPosition(item.getItemId());
                if (position >= 0 && iconPositions != null) {
                    gradientCircle.animate().x(iconPositions[position]).setDuration(300).start();
                }
                return true;
            });
        }

    }

    //하단 메뉴 위치 설정
    private int getSelectedPosition(int itemId) {
        if (itemId == R.id.Feed) {
            return 0;
        } else if (itemId == R.id.Browser) {
            return 1;
        } else if (itemId == R.id.Calendar) {
            return 2;
        } else if (itemId == R.id.Group) {
            return 3;
        } else if (itemId == R.id.Profile) {
            return 4;
        } else {
            return -1;
        }
    }
}