package com.example.maitescalc.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.maitescalc.ui.screens.auth.LoginScreen
import com.example.maitescalc.ui.screens.home.HomeScreen
import com.example.maitescalc.ui.screens.ingredients.AddEditIngredientSheet
import com.example.maitescalc.ui.screens.ingredients.IngredientSearchScreen
import com.example.maitescalc.ui.screens.ingredients.IngredientsScreen
import com.example.maitescalc.ui.screens.recipes.AddEditRecipeScreen
import com.example.maitescalc.ui.screens.recipes.RecipeDetailScreen
import com.example.maitescalc.ui.screens.recipes.RecipesScreen
import com.example.maitescalc.ui.screens.sales.AddEditSaleScreen
import com.example.maitescalc.ui.screens.sales.SaleDetailScreen
import com.example.maitescalc.ui.screens.sales.SalesScreen
import com.example.maitescalc.ui.screens.settings.SettingsScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val INGREDIENTS = "ingredients"
    const val ADD_INGREDIENT = "add_ingredient"
    const val EDIT_INGREDIENT = "edit_ingredient/{ingredientId}"
    const val INGREDIENT_SEARCH = "ingredient_search"
    const val RECIPES = "recipes"
    const val ADD_RECIPE = "add_recipe"
    const val EDIT_RECIPE = "edit_recipe/{recipeId}"
    const val RECIPE_DETAIL = "recipe_detail/{recipeId}"
    const val SALES = "sales"
    const val ADD_SALE = "add_sale"
    const val EDIT_SALE = "edit_sale/{saleId}"
    const val SALE_DETAIL = "sale_detail/{saleId}"
    const val SETTINGS = "settings"

    fun editIngredient(id: String) = "edit_ingredient/$id"
    fun editRecipe(id: String) = "edit_recipe/$id"
    fun recipeDetail(id: String) = "recipe_detail/$id"
    fun editSale(id: String) = "edit_sale/$id"
    fun saleDetail(id: String) = "sale_detail/$id"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        label = "ראשי",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Routes.INGREDIENTS,
        label = "מצרכים",
        selectedIcon = Icons.Filled.Inventory2,
        unselectedIcon = Icons.Outlined.Inventory2
    ),
    BottomNavItem(
        route = Routes.RECIPES,
        label = "מתכונים",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    ),
    BottomNavItem(
        route = Routes.SALES,
        label = "מכירות",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    )
)

@Composable
fun MainNavigation(
    isSignedIn: Boolean,
    onSignedIn: () -> Unit,
    onSignOut: () -> Unit = {}
) {
    val navController = rememberNavController()

    if (!isSignedIn) {
        LoginScreen(onLoginSuccess = onSignedIn)
        return
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            onSignOut = onSignOut,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        // Home
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToIngredients = { navController.navigate(Routes.INGREDIENTS) },
                onNavigateToRecipes = { navController.navigate(Routes.RECIPES) },
                onNavigateToSales = { navController.navigate(Routes.SALES) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        // Ingredients
        composable(Routes.INGREDIENTS) {
            IngredientsScreen(
                onNavigateToAdd = { navController.navigate(Routes.ADD_INGREDIENT) },
                onNavigateToEdit = { id: String -> navController.navigate(Routes.editIngredient(id)) },
                onNavigateToSearch = { navController.navigate(Routes.INGREDIENT_SEARCH) }
            )
        }
        composable(Routes.ADD_INGREDIENT) {
            AddEditIngredientSheet(
                ingredientId = null,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = { navController.navigate(Routes.INGREDIENT_SEARCH) }
            )
        }
        composable(
            Routes.EDIT_INGREDIENT,
            arguments = listOf(navArgument("ingredientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ingredientId = backStackEntry.arguments?.getString("ingredientId")
            AddEditIngredientSheet(
                ingredientId = ingredientId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = { navController.navigate(Routes.INGREDIENT_SEARCH) }
            )
        }
        composable(Routes.INGREDIENT_SEARCH) {
            IngredientSearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Recipes
        composable(Routes.RECIPES) {
            RecipesScreen(
                onNavigateToAdd = { navController.navigate(Routes.ADD_RECIPE) },
                onNavigateToDetail = { id: String -> navController.navigate(Routes.recipeDetail(id)) },
                onNavigateToEdit = { id: String -> navController.navigate(Routes.editRecipe(id)) }
            )
        }
        composable(Routes.ADD_RECIPE) {
            AddEditRecipeScreen(
                recipeId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Routes.EDIT_RECIPE,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            AddEditRecipeScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Routes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.editRecipe(recipeId)) }
            )
        }

        // Sales
        composable(Routes.SALES) {
            SalesScreen(
                onNavigateToAdd = { navController.navigate(Routes.ADD_SALE) },
                onNavigateToDetail = { id: String -> navController.navigate(Routes.saleDetail(id)) },
                onNavigateToEdit = { id: String -> navController.navigate(Routes.editSale(id)) }
            )
        }
        composable(Routes.ADD_SALE) {
            AddEditSaleScreen(
                saleId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Routes.EDIT_SALE,
            arguments = listOf(navArgument("saleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getString("saleId")
            AddEditSaleScreen(
                saleId = saleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Routes.SALE_DETAIL,
            arguments = listOf(navArgument("saleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getString("saleId") ?: return@composable
            SaleDetailScreen(
                saleId = saleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.editSale(saleId)) }
            )
        }

        // Settings
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignOut = onSignOut
            )
        }
    }
}
