package ru.lifelaboratory.it4life;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Recipe {

    // список рецептов
    @POST("/get_recipe_list")
    Call<List<RecipeList>> getRecipeList();

    public class RecipeList {
        public Integer id_recipe;
        public String name;
        public Integer total_time;
        public String description;
    }

    // информация о рецепте
    @POST("/get_recipe_desc")
    Call<List<RecipeDescription>> getRecipeDescription(@Body RecipeGetDescription id);

    public class RecipeGetDescription {
        public Integer id_recipe;
        public RecipeGetDescription(Integer id){
            this.id_recipe = id;
        }
    }

    public class RecipeDescription {
        public Integer id_recipe;
        public String recipe_description;
        public Integer ord;
        public Integer time_step;
        public String step_description;
        public Integer total_time;
    }


    // отправка оценки рецепта
    @POST("/set_recipe_score")
    Call<Object> setRecipeScore(@Body SetRecipeScore id);

    public class SetRecipeScore {
        public String comment;
        public Integer id_recire;
        public Integer score;
        public SetRecipeScore(String comment, Integer id_recire, Integer score) {
            this.comment = comment;
            this.id_recire = id_recire;
            this.score = score;
        }
    }

}
