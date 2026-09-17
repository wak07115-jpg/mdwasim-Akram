package com.example.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.LocalKartTopBar
import com.example.ui.screens.AddProductScreen
import com.example.ui.screens.AdminCategoriesScreen
import com.example.ui.screens.AdminCommissionsScreen
import com.example.ui.screens.AdminCouponsScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminSellersScreen
import com.example.ui.screens.AdminShopsScreen
import com.example.ui.screens.CustomerCartScreen
import com.example.ui.screens.CustomerCheckoutScreen
import com.example.ui.screens.CustomerExploreScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.CustomerOrderDetailScreen
import com.example.ui.screens.CustomerOrdersScreen
import com.example.ui.screens.CustomerProductDetailScreen
import com.example.ui.screens.CustomerProfileScreen
import com.example.ui.screens.CustomerShopDetailScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.screens.SellerDashboardScreen
import com.example.ui.screens.SellerOrdersScreen
import com.example.ui.screens.SellerProductsScreen
import com.example.ui.screens.SellerShopEditScreen
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardWhite
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MutedGray
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAccent
import com.example.ui.viewmodel.LocalKartViewModel
import kotlinx.coroutines.flow.collectLatest

sealed class CustomerNav {
  object Home : CustomerNav()
  object Explore : CustomerNav()
  object Cart : CustomerNav()
  object Orders : CustomerNav()
  object Profile : CustomerNav()
  data class ShopDetail(val shopId: String) : CustomerNav()
  data class ProductDetail(val productId: String) : CustomerNav()
  object Checkout : CustomerNav()
  data class OrderDetail(val orderId: String) : CustomerNav()
}

sealed class SellerNav {
  object Dashboard : SellerNav()
  object Products : SellerNav()
  object AddProduct : SellerNav()
  object Orders : SellerNav()
  object Shop : SellerNav()
}

sealed class AdminNav {
  object Dashboard : AdminNav()
  object Sellers : AdminNav()
  object Shops : AdminNav()
  object Categories : AdminNav()
  object Coupons : AdminNav()
  object Commissions : AdminNav()
}

data class BottomNavItem(
  val title: String,
  val icon: ImageVector,
  val testTag: String
)

@Composable
fun LocalKartApp(
  viewModel: LocalKartViewModel
) {
  val context = LocalContext.current
  val activeRole by viewModel.activeRole.collectAsState()
  val cartItems by viewModel.cartItemsWithProducts.collectAsState()
  val totalCartQty = cartItems.sumOf { it.cartItem.qty }

  // Customer Navigation State Stack
  var customerScreen by remember { mutableStateOf<CustomerNav>(CustomerNav.Home) }
  var customerNavStack by remember { mutableStateOf(listOf<CustomerNav>()) }

  // Seller Navigation State
  var sellerScreen by remember { mutableStateOf<SellerNav>(SellerNav.Dashboard) }

  // Admin Navigation State
  var adminScreen by remember { mutableStateOf<AdminNav>(AdminNav.Dashboard) }

  val snackbarHostState = remember { SnackbarHostState() }

  // Listen for user messages
  LaunchedEffect(Unit) {
    viewModel.userMessage.collectLatest { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
  }

  fun navigateCustomer(newScreen: CustomerNav) {
    customerNavStack = customerNavStack + customerScreen
    customerScreen = newScreen
  }

  fun popCustomerBack(): Boolean {
    if (customerNavStack.isNotEmpty()) {
      customerScreen = customerNavStack.last()
      customerNavStack = customerNavStack.dropLast(1)
      return true
    }
    return false
  }

  BackHandler(enabled = customerNavStack.isNotEmpty() && activeRole == UserRole.CUSTOMER) {
    popCustomerBack()
  }

  val isCustomerSubscreen = customerScreen is CustomerNav.ShopDetail ||
    customerScreen is CustomerNav.ProductDetail ||
    customerScreen is CustomerNav.Checkout ||
    customerScreen is CustomerNav.OrderDetail

  val isSellerSubscreen = sellerScreen == SellerNav.AddProduct

  BackHandler(enabled = isSellerSubscreen && activeRole == UserRole.SELLER) {
    sellerScreen = SellerNav.Products
  }

  val currentTitle = when (activeRole) {
    UserRole.CUSTOMER -> when (val s = customerScreen) {
      is CustomerNav.Home -> "LocalKart"
      is CustomerNav.Explore -> "Explore Stores"
      is CustomerNav.Cart -> "My Cart"
      is CustomerNav.Orders -> "My Orders"
      is CustomerNav.Profile -> "Customer Profile"
      is CustomerNav.ShopDetail -> "Storefront"
      is CustomerNav.ProductDetail -> "Product Details"
      is CustomerNav.Checkout -> "Checkout"
      is CustomerNav.OrderDetail -> "Order Tracking"
    }
    UserRole.SELLER -> when (sellerScreen) {
      SellerNav.Dashboard -> "Seller Dashboard"
      SellerNav.Products -> "Product Catalog"
      SellerNav.AddProduct -> "Add Product"
      SellerNav.Orders -> "Store Orders"
      SellerNav.Shop -> "Store Settings"
    }
    UserRole.ADMIN -> when (adminScreen) {
      AdminNav.Dashboard -> "Admin Console"
      AdminNav.Sellers -> "Merchant Management"
      AdminNav.Shops -> "Storefront Directory"
      AdminNav.Categories -> "Categories Taxonomy"
      AdminNav.Coupons -> "Promos & Coupons"
      AdminNav.Commissions -> "Commission Policy"
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      LocalKartTopBar(
        title = currentTitle,
        currentRole = activeRole,
        onRoleSwitch = { newRole ->
          viewModel.setRole(newRole)
          // Reset navigation when switching
          customerScreen = CustomerNav.Home
          customerNavStack = emptyList()
          sellerScreen = SellerNav.Dashboard
          adminScreen = AdminNav.Dashboard
        },
        cartCount = totalCartQty,
        onCartClick = {
          if (activeRole == UserRole.CUSTOMER) {
            navigateCustomer(CustomerNav.Cart)
          }
        },
        showBack = (isCustomerSubscreen && activeRole == UserRole.CUSTOMER) ||
          (isSellerSubscreen && activeRole == UserRole.SELLER),
        onBackClick = {
          if (activeRole == UserRole.SELLER && isSellerSubscreen) {
            sellerScreen = SellerNav.Products
          } else {
            popCustomerBack()
          }
        }
      )
    },
    bottomBar = {
      when (activeRole) {
        UserRole.CUSTOMER -> {
          if (!isCustomerSubscreen) {
            NavigationBar(
              containerColor = CardWhite,
              tonalElevation = 8.dp
            ) {
              val items = listOf(
                Triple(CustomerNav.Home, "Home", Icons.Default.Home),
                Triple(CustomerNav.Explore, "Explore", Icons.Default.Explore),
                Triple(CustomerNav.Cart, "Cart", Icons.Default.ShoppingCart),
                Triple(CustomerNav.Orders, "Orders", Icons.Default.ReceiptLong),
                Triple(CustomerNav.Profile, "Profile", Icons.Default.Person)
              )

              items.forEach { (route, label, icon) ->
                val isSelected = customerScreen == route
                NavigationBarItem(
                  selected = isSelected,
                  onClick = {
                    customerNavStack = emptyList()
                    customerScreen = route
                  },
                  icon = {
                    if (route == CustomerNav.Cart && totalCartQty > 0) {
                      BadgedBox(badge = {
                        Badge(containerColor = OrangeAccent, contentColor = CardWhite) {
                          Text(totalCartQty.toString(), fontWeight = FontWeight.Bold)
                        }
                      }) {
                        Icon(icon, contentDescription = label)
                      }
                    } else {
                      Icon(icon, contentDescription = label)
                    }
                  },
                  label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavyPrimary,
                    selectedTextColor = NavyPrimary,
                    indicatorColor = BlueAccent.copy(alpha = 0.15f),
                    unselectedIconColor = MutedGray,
                    unselectedTextColor = MutedGray
                  ),
                  modifier = Modifier.testTag("nav_item_${label.lowercase()}")
                )
              }
            }
          }
        }
        UserRole.SELLER -> {
          if (!isSellerSubscreen) {
            NavigationBar(
              containerColor = CardWhite,
              tonalElevation = 8.dp
            ) {
              val items = listOf(
                Triple(SellerNav.Dashboard, "Dashboard", Icons.Default.Dashboard),
                Triple(SellerNav.Products, "Products", Icons.Default.Inventory),
                Triple(SellerNav.Orders, "Orders", Icons.Default.ReceiptLong),
                Triple(SellerNav.Shop, "Store Profile", Icons.Default.Store)
              )
              items.forEach { (route, label, icon) ->
                val isSelected = sellerScreen == route
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { sellerScreen = route },
                  icon = { Icon(icon, contentDescription = label) },
                  label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavyPrimary,
                    selectedTextColor = NavyPrimary,
                    indicatorColor = BlueAccent.copy(alpha = 0.15f),
                    unselectedIconColor = MutedGray,
                    unselectedTextColor = MutedGray
                  ),
                  modifier = Modifier.testTag("seller_nav_${label.lowercase()}")
                )
              }
            }
          }
        }
        UserRole.ADMIN -> {
          NavigationBar(
            containerColor = CardWhite,
            tonalElevation = 8.dp
          ) {
            val items = listOf(
              Triple(AdminNav.Dashboard, "Overview", Icons.Default.Dashboard),
              Triple(AdminNav.Sellers, "Sellers", Icons.Default.People),
              Triple(AdminNav.Shops, "Shops", Icons.Default.Store),
              Triple(AdminNav.Categories, "Categories", Icons.Default.Category),
              Triple(AdminNav.Coupons, "Coupons", Icons.Default.LocalOffer),
              Triple(AdminNav.Commissions, "Rates", Icons.Default.Paid)
            )
            items.forEach { (route, label, icon) ->
              val isSelected = adminScreen == route
              NavigationBarItem(
                selected = isSelected,
                onClick = { adminScreen = route },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = NavyPrimary,
                  selectedTextColor = NavyPrimary,
                  indicatorColor = BlueAccent.copy(alpha = 0.15f),
                  unselectedIconColor = MutedGray,
                  unselectedTextColor = MutedGray
                ),
                modifier = Modifier.testTag("admin_nav_${label.lowercase()}")
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (activeRole) {
        UserRole.CUSTOMER -> {
          when (val s = customerScreen) {
            is CustomerNav.Home -> CustomerHomeScreen(
              viewModel = viewModel,
              onProductClick = { productId -> navigateCustomer(CustomerNav.ProductDetail(productId)) },
              onShopClick = { shopId -> navigateCustomer(CustomerNav.ShopDetail(shopId)) },
              onSeeAllShopsClick = {
                customerNavStack = emptyList()
                customerScreen = CustomerNav.Explore
              }
            )
            is CustomerNav.Explore -> CustomerExploreScreen(
              viewModel = viewModel,
              onShopClick = { shopId -> navigateCustomer(CustomerNav.ShopDetail(shopId)) }
            )
            is CustomerNav.Cart -> CustomerCartScreen(
              viewModel = viewModel,
              onProceedToCheckout = { navigateCustomer(CustomerNav.Checkout) },
              onStartShopping = {
                customerNavStack = emptyList()
                customerScreen = CustomerNav.Home
              }
            )
            is CustomerNav.Orders -> CustomerOrdersScreen(
              viewModel = viewModel,
              onOrderClick = { orderId -> navigateCustomer(CustomerNav.OrderDetail(orderId)) }
            )
            is CustomerNav.Profile -> CustomerProfileScreen(
              viewModel = viewModel
            )
            is CustomerNav.ShopDetail -> CustomerShopDetailScreen(
              shopId = s.shopId,
              viewModel = viewModel,
              onProductClick = { productId -> navigateCustomer(CustomerNav.ProductDetail(productId)) },
              onBack = { popCustomerBack() }
            )
            is CustomerNav.ProductDetail -> CustomerProductDetailScreen(
              productId = s.productId,
              viewModel = viewModel,
              onShopClick = { shopId -> navigateCustomer(CustomerNav.ShopDetail(shopId)) },
              onBuyNowClick = { navigateCustomer(CustomerNav.Checkout) },
              onBack = { popCustomerBack() }
            )
            is CustomerNav.Checkout -> CustomerCheckoutScreen(
              viewModel = viewModel,
              onOrderSuccess = { orderId ->
                customerNavStack = emptyList()
                customerScreen = CustomerNav.OrderDetail(orderId)
              },
              onBack = { popCustomerBack() }
            )
            is CustomerNav.OrderDetail -> CustomerOrderDetailScreen(
              orderId = s.orderId,
              viewModel = viewModel,
              onBack = { popCustomerBack() }
            )
          }
        }
        UserRole.SELLER -> {
          when (sellerScreen) {
            SellerNav.Dashboard -> SellerDashboardScreen(
              viewModel = viewModel,
              onNavigateToProducts = { sellerScreen = SellerNav.Products },
              onNavigateToOrders = { sellerScreen = SellerNav.Orders },
              onNavigateToShop = { sellerScreen = SellerNav.Shop }
            )
            SellerNav.Products -> ProductListScreen(
              viewModel = viewModel,
              onNavigateToAddProduct = { sellerScreen = SellerNav.AddProduct }
            )
            SellerNav.AddProduct -> AddProductScreen(
              viewModel = viewModel,
              onNavigateBack = { sellerScreen = SellerNav.Products }
            )
            SellerNav.Orders -> SellerOrdersScreen(
              viewModel = viewModel
            )
            SellerNav.Shop -> SellerShopEditScreen(
              viewModel = viewModel
            )
          }
        }
        UserRole.ADMIN -> {
          when (adminScreen) {
            AdminNav.Dashboard -> AdminDashboardScreen(
              viewModel = viewModel,
              onNavigateToSellers = { adminScreen = AdminNav.Sellers },
              onNavigateToShops = { adminScreen = AdminNav.Shops },
              onNavigateToCategories = { adminScreen = AdminNav.Categories },
              onNavigateToCoupons = { adminScreen = AdminNav.Coupons },
              onNavigateToCommissions = { adminScreen = AdminNav.Commissions }
            )
            AdminNav.Sellers -> AdminSellersScreen(viewModel = viewModel)
            AdminNav.Shops -> AdminShopsScreen(viewModel = viewModel)
            AdminNav.Categories -> AdminCategoriesScreen(viewModel = viewModel)
            AdminNav.Coupons -> AdminCouponsScreen(viewModel = viewModel)
            AdminNav.Commissions -> AdminCommissionsScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}
