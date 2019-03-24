package ru.lifelaboratory.it4life;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeInfoActivity extends AppCompatActivity {

    private List<Recipe.RecipeDescription> listForRecipe;
    private RecipeListAdapter adapter;
    private String title;
    private ArrayList<String> nextStr = new ArrayList<>(Arrays.asList("Затем", "Сперва", "Теперь необходимо", "Далее", "Погнали"));

    EditText score, comment;
    Button btnCancel, btnSend;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((ImageView) findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainActivity = new Intent(RecipeInfoActivity.this, MainActivity.class);
                startActivity(toMainActivity);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listForRecipe.remove(0);
                adapter.notifyDataSetChanged();

                if (listForRecipe.size() == 0) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(RecipeInfoActivity.this);
                    adb.setTitle("Оценка рецепта");
                    view = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.dialog_score, null);
                    adb.setView(view);
                    score = (EditText) view.findViewById(R.id.recipe_score);
                    comment = (EditText) view.findViewById(R.id.recipe_comment);
                    btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                    btnSend = (Button) view.findViewById(R.id.btn_send);

                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Retrofit server = new Retrofit.Builder()
                                    .baseUrl("http://10.100.110.141:12452")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            Recipe toServerRace = server.create(Recipe.class);
                            toServerRace.setRecipeScore(new Recipe.SetRecipeScore(comment.getText().toString(), id, Integer.valueOf(score.getText().toString()))).enqueue(new Callback<Object>() {
                                @Override
                                public void onResponse(Call<Object> call, Response<Object> response) {
                                    Toast.makeText(RecipeInfoActivity.this, "Оценка выставлена", Toast.LENGTH_SHORT).show();
                                    Intent toMainActivity = new Intent(RecipeInfoActivity.this, MainActivity.class);
                                    startActivity(toMainActivity);
                                }
                                @Override
                                public void onFailure(Call<Object> call, Throwable t) { }
                            });
                        }
                    });

                    AlertDialog dialog = adb.create();
                    dialog.show();
                } else {
                    Random random = new Random();
                    new MainActivity.RetrieveFeedTask(nextStr.get(Math.abs(random.nextInt() % nextStr.size())).concat(listForRecipe.get(0).step_description)).execute();
                }

                Log.d("YANDEX_MTS", String.valueOf(listForRecipe.size()));
            }
        });

        title = getIntent().getStringExtra("TITLE");
        ((TextView) findViewById(R.id.title_text)).setText(title);
        ((TextView) findViewById(R.id.title_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainActivity = new Intent(RecipeInfoActivity.this, MainActivity.class);
                startActivity(toMainActivity);
            }
        });
        id = getIntent().getIntExtra("ID", -1);

        ImageView recipeImg = (ImageView) findViewById(R.id.recipe_img);
        switch (id){
            case 1: recipeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.one)); break;
            case 2: recipeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.two)); break;
            case 3: recipeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.three)); break;
            default: recipeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.mts_logo)); break;
        }

        ((TextView) findViewById(R.id.recipe_time)).setText(String.valueOf(getIntent().getIntExtra("TOTAL_TIME", -1) / 60).concat(" минут"));
        ((TextView) findViewById(R.id.recipe_description)).setText(String.valueOf(getIntent().getStringExtra("DESCRIPTION")).replace("\\n", "\n"));

        ListView lst = (ListView) findViewById(R.id.list_recipe);
        listForRecipe = new ArrayList<>();
        adapter = new RecipeListAdapter(this, listForRecipe);
        lst.setAdapter(adapter);

        Retrofit server = new Retrofit.Builder()
                .baseUrl("http://10.100.110.141:12452") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        Recipe toServerRace = server.create(Recipe.class);
        toServerRace.getRecipeDescription(new Recipe.RecipeGetDescription(getIntent().getIntExtra("ID", -1))).enqueue(new Callback<List<Recipe.RecipeDescription>>() {
            @Override
            public void onResponse(Call<List<Recipe.RecipeDescription>> call, Response<List<Recipe.RecipeDescription>> response) {
                listForRecipe.clear();
                listForRecipe.addAll(response.body());
                adapter.notifyDataSetChanged();
                String firstCommand = ". Сперва необходимо ".concat(listForRecipe.get(0).step_description);
                new MainActivity.RetrieveFeedTask("Мне нужна твоя одежда и ".concat(title).concat(firstCommand)).execute();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Log.e("YANDEX_MTS", e.getMessage());
                }
                if (listForRecipe.get(0).time_step == 0) {
                    timer(5);
                } else {
                    timer(listForRecipe.get(0).time_step);
                }
            }
            @Override
            public void onFailure(Call<List<Recipe.RecipeDescription>> call, Throwable t) { }
        });
    }

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    private void timer(Integer time) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, time * 1000);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
            final String strDate = simpleDateFormat.format(calendar.getTime());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new MainActivity.RetrieveFeedTask("Давайте перейдем к следующему шагу?").execute();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e("YANDEX_MTS", e.getMessage());
                    }
                    Log.d("YANDEX_MTS", "ВЕЩАНИЕ");
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Дальше?");
                    try {
                        startActivityForResult(intent, 100);
                    } catch (ActivityNotFoundException a) {
                        Log.e("YANDEX_MTS", a.getMessage());
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("YANDEX_MTS", result.get(0));

                    if (result.get(0).contains("да") || result.get(0).contains("конечно") || result.get(0).contains("продолж")){
                        listForRecipe.remove(0);
                        adapter.notifyDataSetChanged();

                        if (listForRecipe.size() == 0) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(RecipeInfoActivity.this);
                            adb.setTitle("Оценка рецепта");
                            View view = (LinearLayout) getLayoutInflater()
                                    .inflate(R.layout.dialog_score, null);
                            adb.setView(view);
                            score = (EditText) view.findViewById(R.id.recipe_score);
                            comment = (EditText) view.findViewById(R.id.recipe_comment);
                            btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                            btnSend = (Button) view.findViewById(R.id.btn_send);

                            btnSend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Retrofit server = new Retrofit.Builder()
                                            .baseUrl("http://10.100.110.141:12452")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    Recipe toServerRace = server.create(Recipe.class);
                                    toServerRace.setRecipeScore(new Recipe.SetRecipeScore(comment.getText().toString(), id, Integer.valueOf(score.getText().toString()))).enqueue(new Callback<Object>() {
                                        @Override
                                        public void onResponse(Call<Object> call, Response<Object> response) {
                                            Toast.makeText(RecipeInfoActivity.this, "Оценка выставлена", Toast.LENGTH_SHORT).show();
                                            Intent toMainActivity = new Intent(RecipeInfoActivity.this, MainActivity.class);
                                            startActivity(toMainActivity);
                                        }
                                        @Override
                                        public void onFailure(Call<Object> call, Throwable t) { }
                                    });
                                }
                            });

                            AlertDialog dialog = adb.create();
                            dialog.show();
                        } else {
                            Random random = new Random();
                            new MainActivity.RetrieveFeedTask(nextStr.get(Math.abs(random.nextInt() % nextStr.size())).concat(listForRecipe.get(0).step_description)).execute();
                            if (listForRecipe.get(0).time_step == 0)
                                timer(5);
                            else
                                timer(listForRecipe.get(0).time_step);
                        }
                    } else if(result.get(0).contains("повтор")) {
                        Random random = new Random();
                        new MainActivity.RetrieveFeedTask(nextStr.get(Math.abs(random.nextInt() % nextStr.size())).concat(listForRecipe.get(0).step_description)).execute();
                        if (listForRecipe.get(0).time_step == 0)
                            timer(5);
                        else
                            timer(listForRecipe.get(0).time_step);
                    } else if(result.get(0).contains("нет")) {
                        if (listForRecipe.get(0).time_step == 0)
                            timer(5);
                        else
                            timer(listForRecipe.get(0).time_step);
                    } else {
                        new MainActivity.RetrieveFeedTask("Я НЕ ПОНЯЛЬ").execute();
                        timer(1);
                    }
                }
                break;
            }
        }
    }

}
