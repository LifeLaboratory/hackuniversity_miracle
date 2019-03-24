package ru.lifelaboratory.it4life;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter {

    private List<Recipe.RecipeDescription> recipeList;
    private Context context;

    public RecipeListAdapter(Context context, List<Recipe.RecipeDescription> raceList) {
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

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = convertView;
        if (rootView == null)
            rootView = inflater.inflate(R.layout.adapter_recipe_list, null);

        TextView title = (TextView) rootView.findViewById(R.id.main_list_text);
        title.setText(this.recipeList.get(position).step_description);
        TextView time = (TextView) rootView.findViewById(R.id.recipe_list_time);
        Log.d("YANDEX_MTS", String.valueOf(this.recipeList.get(position).time_step / 60).concat(" мин"));
        if (this.recipeList.get(position).time_step / 60 > 0)
            time.setText(String.valueOf(this.recipeList.get(position).time_step / 60).concat(" мин."));
        else
            time.setText("");

        return rootView;
    }
}
