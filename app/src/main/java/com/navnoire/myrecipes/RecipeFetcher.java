package com.navnoire.myrecipes;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by navnoire on 19/10/17 in MyRecipes project
 */

public class RecipeFetcher {
    private static final String TAG = "RecipeFetcher";
    private static final String MAIN_URL = "https://gotovim-doma.ru";

    private RecipeKt mRecipe;
    private List<RecipeKt> mRecipes;
    private List<MenuItem> mMenuItems;
    private Document mDocument;
    private String mUrl;

    public RecipeFetcher() {
//        mRecipe = new RecipeKt();
        mRecipes = new ArrayList<>();
        mMenuItems = new ArrayList<>();
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + " with: " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }

    public RecipeKt fetchSingleRecipe(RecipeKt recipe) {
        mRecipe = recipe;
        String url = recipe.getUrl();

        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla").get();

            Element recipeElement = document.select("div.recept-shadow").first();
            if (recipeElement != null) {
                setTextFields(recipeElement);
            } else {
                Log.d(TAG, "fetchSingleRecipe: failed to find recipe element");
            }

            Element recipeInstructions = document.select("div.recept-shadow").get(1);
            ArrayList<ArrayList<String>> stepsList = new ArrayList<>();

            if (recipeInstructions != null) {
                Elements steps = recipeInstructions.select("div.step");
                Iterator i = steps.iterator();
                while (i.hasNext()) {
                    Element step = (Element) i.next();
                    String imageUrl = null;
                    if (step.select("img.stepimg").size() != 0) {
                        imageUrl = step.select("img.stepimg").get(0).absUrl("src");
                    }
                    String stepText = step.select("p").text();
                    ArrayList<String> oneStep = new ArrayList<>();
                    oneStep.add(stepText);
                    oneStep.add(imageUrl);

                    stepsList.add(oneStep);
                }

                mRecipe.setInstructions(stepsList);
            }


        } catch (IOException ioe) {
            Log.d(TAG, "Connection failed jsoup");
        }

        return mRecipe;
    }

    public List<RecipeKt> fetchRecipeList(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).userAgent("Mozilla").get();
        } catch (IOException ioe) {
            Log.e(TAG, "fetchRecipeList: recipe list fetching failed", ioe);
            return null;
        }
        if (document.select("div.recept-item").size() != 0) {
            Elements recipes = document.select("div.recept-item-img");
            Iterator iterator = recipes.iterator();
            while (iterator.hasNext()) {
                Element item = (Element) iterator.next();
                RecipeKt recipe = new RecipeKt(item.select("a[href]").first().absUrl("href"),
                        item.select("img[alt]").attr("alt"),
                        null,
                        null,
                        item.select("img").first().absUrl("src"),
                        null,
                        null);

//                recipe.setMainImageUrl(item.select("img").first().absUrl("src"));
//                recipe.setTitle(item.select("img[alt]").attr("alt"));
//                recipe.setUrl(item.select("a[href]").first().absUrl("href"));
                mRecipes.add(recipe);
            }
        }
        return mRecipes;
    }

    public List<MenuItem> fetchMenuList(String url) {
        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla").get();
            if (url.equals(MAIN_URL)) {
                mMenuItems = getMenu(document, "table.menu-main", "th");
            } else {

                if (document.select("ul.razdel-menu").size() != 0) {
                    mMenuItems = getMenu(document, "ul.razdel-menu", "li");
                } else {
                    Log.d(TAG, "fetchMenuList: no way forward");
                    return null;
                }
            }

        } catch (IOException ioe) {
            Log.e(TAG, "fetchMenuList: connection failed JSOUP", ioe);
        }
        return mMenuItems;
    }

    private List<MenuItem> getMenu(Document root, String element, String child) {
        List<MenuItem> menuItems = new ArrayList<>();

        Elements items = root.select(element).first().select(child);
        Iterator iterator = items.listIterator();
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            String name = item.text();
            String itemUrl = item.select("a[href]").first().absUrl("href");
            MenuItem newItem = new MenuItem(name, itemUrl);
            menuItems.add(newItem);
            Log.d(TAG, "fetchMenuList: fetched MenuItem " + name + " " + itemUrl);
        }
        return menuItems;
    }

    private void setTextFields(Element root) {
        String summary = root.getElementsByClass("summary").get(0).text();
        mRecipe.setSummary(summary);

        ArrayList<Element> ingredients = root.getElementsByClass("recept-table").get(0)
                .getElementsByClass("ingredient");
        Log.d(TAG, "setTextFields: got " + ingredients.size() + " ingredients");

        StringBuilder sb = new StringBuilder();

        for (Element e : ingredients) {
            String ingredientName = e.child(0).text();
            String ingredientAmount = e.child(1).text();

            String s = ("\u25cf  " + ingredientName + " - " + ingredientAmount + "\n");
            sb.append(s);
        }

        String finalIngredientsString = sb.toString();
        mRecipe.setIngredientsString(finalIngredientsString);
    }
}
