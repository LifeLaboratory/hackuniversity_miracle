package ru.lifelaboratory.it4life;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AudioRecord audioRecord;

    private int mChannels = 2;
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 8000;

    private List<Recipe.RecipeList> listForRecipe;
    private MainListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int channelConfig = mChannels == 2?
                AudioFormat.CHANNEL_IN_STEREO:
                AudioFormat.CHANNEL_IN_MONO;
        int bufferSize = AudioRecord.getMinBufferSize(
                sampleRate, channelConfig, audioEncoding);
        audioRecord = new AudioRecord(
                audioSource, sampleRate, channelConfig, audioEncoding, bufferSize);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("YADNEX_MTS","AudioRecord init failed");
            return;
        }

        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.btn_record);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Что будем готовить?");
                try {
                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException a) {
                    Log.e("YANDEX_MTS", a.getMessage());
                }
            }
        });

        ListView lst = (ListView) findViewById(R.id.list_recipe);
        listForRecipe = new ArrayList<>();
        adapter = new MainListAdapter(this, listForRecipe);
        lst.setAdapter(adapter);
        Retrofit server = new Retrofit.Builder()
                .baseUrl("http://10.100.110.141:12452") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        Recipe toServerRace = server.create(Recipe.class);
        toServerRace.getRecipeList().enqueue(new Callback<List<Recipe.RecipeList>>() {
            @Override
            public void onResponse(Call<List<Recipe.RecipeList>> call, Response<List<Recipe.RecipeList>> response) {
                Log.d("YANDEX_MTS", response.body().get(0).name);
                listForRecipe.clear();
                listForRecipe.addAll(response.body());
                adapter.notifyDataSetChanged();
                new RetrieveFeedTask("Получен список рецептов! Что будем готовить?", MainActivity.this, listForRecipe, getApplicationContext()).execute();
            }

            @Override
            public void onFailure(Call<List<Recipe.RecipeList>> call, Throwable t) {

            }
        });
    }

    private AlertDialog dialog;
    private HashMap<Integer, Recipe.RecipeList> distance;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("YANDEX_MTS", result.get(0));

                    distance = new HashMap<>();
                    for (Recipe.RecipeList tmp : listForRecipe) {
                        distance.put(levenstain(result.get(0), tmp.name), tmp);
                    }
                    Log.d("YANDEX_MTS", distance.get(Collections.min(distance.keySet())).name);

                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle(distance.get(Collections.min(distance.keySet())).name);
                    View view = (LinearLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_recipe, null);
                    TextView time = (TextView) view.findViewById(R.id.recipe_time);
                    time.setText("Время приготовления: ".concat(String.valueOf(distance.get(Collections.min(distance.keySet())).total_time / 60).concat(" минут")));
                    TextView description = (TextView) view.findViewById(R.id.recipe_description);
                    description.setText(distance.get(Collections.min(distance.keySet())).description.replace("\\n", "\n"));
                    adb.setView(view);
                    ((Button) view.findViewById(R.id.cancel_btn)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.hide();
                        }
                    });
                    ((Button) view.findViewById(R.id.next_btn)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent toRecipeInfo = new Intent(MainActivity.this, RecipeInfoActivity.class);
                            toRecipeInfo.putExtra("ID", distance.get(Collections.min(distance.keySet())).id_recipe);
                            toRecipeInfo.putExtra("TITLE",  distance.get(Collections.min(distance.keySet())).name);
                            toRecipeInfo.putExtra("DESCRIPTION", distance.get(Collections.min(distance.keySet())).description);
                            toRecipeInfo.putExtra("TOTAL_TIME", distance.get(Collections.min(distance.keySet())).total_time);
                            startActivity(toRecipeInfo);
                        }
                    });
                    dialog = adb.create();
                    new RetrieveFeedTask("Время приготовления: ".concat(String.valueOf(distance.get(Collections.min(distance.keySet())).total_time / 60)).concat(" минут. ").concat(distance.get(Collections.min(distance.keySet())).description.replace("n", ""))).execute();
                    dialog.show();

//                    Intent toRecipeInfo = new Intent(MainActivity.this, RecipeInfoActivity.class);
//                    toRecipeInfo.putExtra("ID", distance.get(Collections.min(distance.keySet())).id_recipe);
//                    toRecipeInfo.putExtra("TITLE", distance.get(Collections.min(distance.keySet())).name);
//                    startActivity(toRecipeInfo);

//                    new RetrieveFeedTask(result.get(0)).execute();
                }
                break;
            }

        }
    }

    private static int min(int n1, int n2, int n3) {
        return Math.min(Math.min(n1, n2), n3);
    }

    public static int levenstain(String str1, String str2) {
        int[] Di_1 = new int[str2.length() + 1];
        int[] Di = new int[str2.length() + 1];

        for (int j = 0; j <= str2.length(); j++) {
            Di[j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            System.arraycopy(Di, 0, Di_1, 0, Di_1.length);

            Di[0] = i;
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) != str2.charAt(j - 1)) ? 1 : 0;
                Di[j] = min(
                        Di_1[j] + 1,
                        Di[j - 1] + 1,
                        Di_1[j - 1] + cost
                );
            }
        }

        return Di[Di.length - 1];
    }

    public static class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {

        private String str;
        private Activity activity;
        private List<Recipe.RecipeList> listForRecipe;
        private Context context;

        public RetrieveFeedTask(String str) {
            this.str = str;
        }

        public RetrieveFeedTask(String str, Activity activity, List<Recipe.RecipeList> listForRecipe, Context context){
            this.str = str;
            this.activity = activity;
            this.listForRecipe = listForRecipe;
            this.context = context;
        }

        private MediaPlayer player;

        protected Void doInBackground(Void... arg) {
            try {
                URL website = new URL("https://tts.voicetech.yandex.net/generate?format=mp3&lang=ru_RU&key=069b6659-984b-4c5f-880e-aaedcfd84102&text=" + this.str);
                ReadableByteChannel rbc = null;
                rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp.wav");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                player = new MediaPlayer();
                try {
                    player.setDataSource(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp.wav");
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    Log.e("YANDEX_MTS", "prepare() failed");
                }
            } catch (IOException e) {
                Log.e("YANDEX_MTS", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            if (this.activity != null) {
                while (player.isPlaying()) {}
//                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Что будем готовить?");
//                try {
//                    this.activity.startActivityForResult(intent, 100);
//                } catch (ActivityNotFoundException a) {
//                    Log.e("YANDEX_MTS", a.getMessage());
//                }
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
                SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(this.activity);
                sr.setRecognitionListener(new listener(this.listForRecipe, this.context, this.activity));
                sr.startListening(intent);
            }
        }
    }

    static class listener implements RecognitionListener {
        private String TAG = "YANDEX_MST";
        private List<Recipe.RecipeList> listForRecipe;
        Dialog dialog;
        private Context context;
        private HashMap<Integer, Recipe.RecipeList> distance;
        private Activity activity;

        public listener (List<Recipe.RecipeList> listForRecipe, Context context, Activity activity) {
            this.listForRecipe = listForRecipe;
            this.context = context;
            this.activity = activity;
        }

        public void onReadyForSpeech(Bundle params) { }
        public void onBeginningOfSpeech() { }
        public void onRmsChanged(float rmsdB) { }
        public void onBufferReceived(byte[] buffer) { }
        public void onEndOfSpeech() { }
        public void onError(int error) { }

        public void onResults(Bundle results)
        {
            String str = new String();
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                str += data.get(i);
            }
            String result = str;
            Log.d("YANDEX_MTS", result);
            HashMap<String, Integer> number = new HashMap<String, Integer>() {{
                put("один", 0);
                put("два", 1);
                put("три", 2);
                put("четыре", 3);
                put("пять", 4);

                put("первый", 0);
                put("второй", 1);
                put("третий", 2);
                put("четвертый", 3);
                put("пятый", 4);

                put("1", 0);
                put("2", 1);
                put("3", 2);
                put("4", 3);
                put("5", 4);
            }};
            for (Map.Entry me : number.entrySet()) {
                Log.d("YANDEX_MTS", me.getKey().toString().concat("  <<<<<<"));
                Log.d("YANDEX_MTS", String.valueOf(result.contains(me.getKey().toString())).concat("  <<<<<<"));

                if (result.contains(me.getKey().toString())) {
                    Intent toRecipeInfo = new Intent(listener.this.context, RecipeInfoActivity.class);
                    toRecipeInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    toRecipeInfo.putExtra("ID", listForRecipe.get((Integer)me.getValue()).id_recipe);
                    toRecipeInfo.putExtra("TITLE", listForRecipe.get((Integer)me.getValue()).name);
                    toRecipeInfo.putExtra("DESCRIPTION", listForRecipe.get((Integer)me.getValue()).description);
                    toRecipeInfo.putExtra("TOTAL_TIME", listForRecipe.get((Integer)me.getValue()).total_time);
                    listener.this.context.startActivity(toRecipeInfo);
                    return;
                }
            }
            if (result.contains("список")) {
                String listRecipe = "";
                for (Recipe.RecipeList tmp : listForRecipe) {
                    listRecipe = listRecipe.concat(tmp.name.concat(". "));
                }
                new RetrieveFeedTask("Список рецептов: ".concat(listRecipe)).execute();
                timer(10, this.activity, this.context, this.listForRecipe);
            } else if (!result.contains("сброс") && !result.contains("отмена")) {
                distance = new HashMap<>();
                for (Recipe.RecipeList tmp : listForRecipe) {
                    distance.put(levenstain(result, tmp.name), tmp);
                }
                Log.d("YANDEX_MTS", distance.get(Collections.min(distance.keySet())).name);

                Intent toRecipeInfo = new Intent(listener.this.context, RecipeInfoActivity.class);
                toRecipeInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toRecipeInfo.putExtra("ID", distance.get(Collections.min(distance.keySet())).id_recipe);
                toRecipeInfo.putExtra("TITLE", distance.get(Collections.min(distance.keySet())).name);
                toRecipeInfo.putExtra("DESCRIPTION", distance.get(Collections.min(distance.keySet())).description);
                toRecipeInfo.putExtra("TOTAL_TIME", distance.get(Collections.min(distance.keySet())).total_time);
                listener.this.context.startActivity(toRecipeInfo);
            }
        }
        public void onPartialResults(Bundle partialResults) { }
        public void onEvent(int eventType, Bundle params) { }
    }

    private static void timer(Integer time, Activity activity, Context context, List<Recipe.RecipeList> listForRecipe) {
        Timer mTimer;
        MyTimerTask mMyTimerTask;
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask(activity, context, listForRecipe);
        mTimer.schedule(mMyTimerTask, time * 1000);
    }

    static class MyTimerTask extends TimerTask {

        private Activity activity;
        private Context context;
        private List<Recipe.RecipeList> listForRecipe;

        public MyTimerTask(Activity activity, Context context, List<Recipe.RecipeList> listForRecipe) {
            this.activity = activity;
            this.context = context;
            this.listForRecipe = listForRecipe;
        }

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
            final String strDate = simpleDateFormat.format(calendar.getTime());
            Log.d("YANDEX_MTS", "ВЕЩАНИЕ");
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new RetrieveFeedTask("Что-нибудь еще?", activity, listForRecipe, context).execute();
                }
            });
        }
    }

}
