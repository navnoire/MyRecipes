package com.navnoire.myrecipes

import android.graphics.drawable.Drawable
import java.util.*

/**
 * Created by navnoire on 01/05/18 in MyRecipes project
 */
class RecipeKt(val url:String,
               val title:String,
               val mainImageUrl:String,
               var summary:String?,
               var ingredientsString: String?,
               var mainImage:Drawable?,
               var instructions: ArrayList<ArrayList<String>>?)