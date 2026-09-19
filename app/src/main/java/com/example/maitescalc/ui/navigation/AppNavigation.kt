package com.example.maitescalc.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                FloatingPillNavigationBar(
                    items = bottomNavItems,
                    currentRoute = currentDestination?.route,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
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
private fun FloatingPillNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(percent = 50),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(percent = 50),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "iconScale"
                    )

                    val pillBgColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                        label = "pillBgColor"
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "contentColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(pillBgColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onItemClick(item.route) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
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
