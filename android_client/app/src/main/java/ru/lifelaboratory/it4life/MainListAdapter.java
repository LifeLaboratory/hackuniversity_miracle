package ru.lifelaboratory.it4life;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainListAdapter extends BaseAdapter {

    private List<Recipe.RecipeList> recipeList;
    private Context context;

    public MainListAdapter(Context context, List<Recipe.RecipeList> raceList) {
        this.recipeList = raceList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.recipeList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.recipeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private LayoutInflater inflater;

    private AlertDialog dialog;

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = convertView;
        if (rootView == null)
            rootView = inflater.inflate(R.layout.adapter_main_list, null);

        TextView title = (TextView) rootView.findViewById(R.id.main_list_text);
        title.setText(this.recipeList.get(position).name);
        Log.d("YANDEX_MTS", this.recipeList.get(position).name);

        ImageView img = (ImageView) rootView.findViewById(R.id.main_list_img);
        switch (MainListAdapter.this.recipeList.get(position).id_recipe){
            case 1: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.one)); break;
            case 2: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.two)); break;
            case 3: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.three)); break;
            default: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.mts_logo)); break;
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainListAdapter.this.context);
                adb.setTitle(MainListAdapter.this.recipeList.get(position).name);
                view = (LinearLayout) inflater.inflate(R.layout.dialog_recipe, null);
                TextView time = (TextView) view.findViewById(R.id.recipe_time);
                time.setText("Время приготовления: ".concat(String.valueOf(MainListAdapter.this.recipeList.get(position).total_time / 60).concat(" минут")));
                TextView description = (TextView) view.findViewById(R.id.recipe_description);
                description.setText(MainListAdapter.this.recipeList.get(position).description.replace("\\n", "\n"));
                adb.setView(view);
                ((Button) view.findViewById(R.id.cancel_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();
                    }
                });
                ImageView img = (ImageView) view.findViewById(R.id.recipe_img);
                switch (MainListAdapter.this.recipeList.get(position).id_recipe){
                    case 1: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.one)); break;
                    case 2: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.two)); break;
                    case 3: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.three)); break;
                    default: img.setImageDrawable(MainListAdapter.this.context.getResources().getDrawable(R.drawable.mts_logo)); break;
                }

                ((Button) view.findViewById(R.id.next_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toRecipeInfo = new Intent(MainListAdapter.this.context, RecipeInfoActivity.class);
                        toRecipeInfo.putExtra("ID", MainListAdapter.this.recipeList.get(position).id_recipe);
                        toRecipeInfo.putExtra("TITLE", MainListAdapter.this.recipeList.get(position).name);
                        toRecipeInfo.putExtra("DESCRIPTION", MainListAdapter.this.recipeList.get(position).description);
                        toRecipeInfo.putExtra("TOTAL_TIME", MainListAdapter.this.recipeList.get(position).total_time);
                        MainListAdapter.this.context.startActivity(toRecipeInfo);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        return rootView;
    }
}
