package com.ferelin.stockprice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.features.about.about.AboutRoute
import com.ferelin.features.home.home.HomeScreenRoute
import com.ferelin.features.login.LoginRoute
import com.ferelin.features.search.SearchRoute
import com.ferelin.features.settings.SettingsRoute
import com.ferelin.features.splash.LoadingScreen
import com.ferelin.stockprice.navigation.Destination.*

@Composable
internal fun AppNavigationGraph(
  navHostController: NavHostController
) {
  NavHost(
    navController = navHostController,
    startDestination = SplashDestination.key
  ) {
    composable(route = SplashDestination.key) {
      LoadingScreen {
        navHostController.navigate(HomeDestination.key) {
          popUpTo(SplashDestination.key) { inclusive = true }
        }
      }
    }
    composable(route = HomeDestination.key) {
      HomeScreenRoute(
        onSettingsRoute = { navHostController.navigate(route = SettingsDestination.key) },
        onCryptosRoute = { },
        onNewsRoute =  { },
        onStocksRoute = { },
        onSupportRoute =  { }
      )
    }
    composable(route = SettingsDestination.key) {
      SettingsRoute(
        onLogInRoute = { navHostController.navigate(route = AuthenticationDestination.key) },
        onBackRoute = { navHostController.popBackStack() }
      )
    }
    composable(route = AuthenticationDestination.key) {
      LoginRoute(
        onBackRoute = { navHostController.popBackStack() }
      )
    }
    composable(route = SearchDestination.key) {
      SearchRoute(
        onBackRoute = { navHostController.popBackStack() },
        onStockRoute = {
          navHostController.navigate(
            route = AboutDestination.buildNavigationPath(it)
          )
        }
      )
    }
    composable(
      route = AboutDestination.key +
              "/{${AboutDestination.ARG_ID}}" +
              "/{${AboutDestination.ARG_NAME}}" +
              "/{${AboutDestination.ARG_TICKER}}",
      arguments = listOf(
        navArgument(AboutDestination.ARG_ID) { type = NavType.IntType },
        navArgument(AboutDestination.ARG_NAME) { type = NavType.StringType },
        navArgument(AboutDestination.ARG_TICKER) { type = NavType.StringType },
      )
    ) { entry ->
      val args = requireNotNull(entry.arguments)
      val id = args.getInt(AboutDestination.ARG_ID)
      val name = requireNotNull(args.getString(AboutDestination.ARG_NAME))
      val ticker = requireNotNull(args.getString(AboutDestination.ARG_TICKER))

      AboutRoute(
        params = AboutParams(CompanyId(id), ticker, name),
        onBackRoute = { navHostController.popBackStack() }
      )
    }
  }
}

private fun AboutDestination.buildNavigationPath(stockViewData: StockViewData): String {
  return this.key +
          "/${stockViewData.id.value}" +
          "/${stockViewData.name}" +
          "/${stockViewData.ticker}"
}