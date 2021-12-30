import com.google.gson.Gson;
import me.wawwior.core.item.JsonRecipe;


import java.io.FileNotFoundException;
import java.io.FileReader;

public class Test {

    public static void main(String[] args) {

        try {

            JsonRecipe jsonRecipe = new Gson().fromJson(new FileReader(Test.class.getClassLoader().getResource("recipe.json").getFile()), JsonRecipe.class);

            System.out.println(jsonRecipe);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
