package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.ProductEntity
import com.example.data.model.SellerEntity
import com.example.data.model.ShopEntity
import com.example.data.model.UserRole
import com.example.data.repository.LocalKartRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartItemWithProduct(
  val cartItem: CartItemEntity,
  val product: ProductEntity,
  val shop: ShopEntity?
)

class LocalKartViewModel(
  private val repository: LocalKartRepository
) : ViewModel() {

  init {
    viewModelScope.launch {
      repository.ensureSeeded()
    }
  }

  // Active Role and Persona
  private val _activeRole = MutableStateFlow(UserRole.CUSTOMER)
  val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

  fun setRole(role: UserRole) {
    _activeRole.value = role
  }

  // Active Customer & Seller IDs
  val currentCustomerId = MutableStateFlow("c1")
  val currentSellerId = MutableStateFlow("s1")

  // Data streams from repository
  val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val shops: StateFlow<List<ShopEntity>> = repository.allShops
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val products: StateFlow<List<ProductEntity>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sellers: StateFlow<List<SellerEntity>> = repository.allSellers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val coupons: StateFlow<List<CouponEntity>> = repository.allCoupons
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Customer Filter State
  val selectedCategoryId = MutableStateFlow("all")
  val searchQuery = MutableStateFlow("")

  // Toast / Snackbars
  private val _userMessage = MutableSharedFlow<String>()
  val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

  fun showToast(message: String) {
    viewModelScope.launch {
      _userMessage.emit(message)
    }
  }

  // Cart Flow
  val cartItemsWithProducts: StateFlow<List<CartItemWithProduct>> = combine(
    repository.getCartForCustomer("c1"),
    repository.allProducts,
    repository.allShops
  ) { cartItems, allProducts, allShops ->
    cartItems.mapNotNull { cartItem ->
      val product = allProducts.find { it.id == cartItem.productId }
      val shop = allShops.find { it.id == cartItem.shopId }
      if (product != null) {
        CartItemWithProduct(cartItem, product, shop)
      } else null
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Applied Coupon State
  val appliedCoupon = MutableStateFlow<CouponEntity?>(null)
  val couponError = MutableStateFlow<String?>(null)

  fun applyCouponCode(code: String, subtotal: Double) {
    viewModelScope.launch {
      val trimmed = code.trim().uppercase()
      if (trimmed.isEmpty()) {
        couponError.value = "Enter coupon code"
        return@launch
      }
      val found = coupons.value.find { it.code.uppercase() == trimmed && it.isActive }
      if (found == null) {
        couponError.value = "Invalid coupon code"
        appliedCoupon.value = null
      } else if (found.expiry < System.currentTimeMillis()) {
        couponError.value = "Coupon has expired"
        appliedCoupon.value = null
      } else if (subtotal < found.minOrder) {
        couponError.value = "Min order ₹${found.minOrder.toInt()} required"
        appliedCoupon.value = null
      } else {
        appliedCoupon.value = found
        couponError.value = null
        showToast("Coupon ${found.code} applied!")
      }
    }
  }

  fun removeAppliedCoupon() {
    appliedCoupon.value = null
    couponError.value = null
  }

  // Customer actions
  fun addToCart(product: ProductEntity, qty: Int = 1) {
    viewModelScope.launch {
      repository.addToCart(currentCustomerId.value, product, qty)
      showToast("Added ${product.name} to cart")
    }
  }

  fun updateCartQty(cartItemId: Int, newQty: Int) {
    viewModelScope.launch {
      repository.updateCartQty(cartItemId, newQty)
    }
  }

  fun removeCartItem(cartItemId: Int) {
    viewModelScope.launch {
      repository.removeCartItem(cartItemId)
      showToast("Removed from cart")
    }
  }

  fun updateCustomerAddress(name: String, phone: String, street: String, city: String, state: String, pincode: String) {
    viewModelScope.launch {
      val cust = customers.value.find { it.id == currentCustomerId.value }
      if (cust != null) {
        repository.updateCustomer(
          cust.copy(
            name = name,
            phone = phone,
            street = street,
            city = city,
            state = state,
            pincode = pincode
          )
        )
        showToast("Address updated successfully")
      }
    }
  }

  fun placeOrder(
    street: String,
    city: String,
    state: String,
    pincode: String,
    customerName: String,
    customerPhone: String,
    paymentMethod: String,
    onSuccess: (String) -> Unit
  ) {
    viewModelScope.launch {
      val items = cartItemsWithProducts.value
      if (items.isEmpty()) return@launch

      // Group items by shop to create distinct shop orders
      val grouped = items.groupBy { it.cartItem.shopId }
      val totalSubtotal = items.sumOf { it.product.price * it.cartItem.qty }
      val coupon = appliedCoupon.value

      var lastOrderId = ""

      for ((shopId, shopItems) in grouped) {
        val sellerId = shopItems.first().product.sellerId
        val shopSubtotal = shopItems.sumOf { it.product.price * it.cartItem.qty }
        val shopDelivery = 30.0

        // Proportional discount if coupon is applied
        val discount = if (coupon != null && totalSubtotal > 0) {
          val share = shopSubtotal / totalSubtotal
          val totalDiscount = if (coupon.type == "percent") {
            (totalSubtotal * coupon.value / 100).coerceAtMost(coupon.maxDiscount)
          } else {
            coupon.value.coerceAtMost(totalSubtotal)
          }
          totalDiscount * share
        } else 0.0

        val orderItems = shopItems.map {
          OrderItem(
            productId = it.product.id,
            name = it.product.name,
            emoji = it.product.emoji,
            price = it.product.price,
            qty = it.cartItem.qty,
            shopId = shopId,
            sellerId = sellerId
          )
        }

        lastOrderId = repository.placeOrder(
          customerId = currentCustomerId.value,
          shopId = shopId,
          sellerId = sellerId,
          items = orderItems,
          subtotal = shopSubtotal,
          delivery = shopDelivery,
          discount = discount,
          paymentMethod = paymentMethod,
          couponId = coupon?.id,
          street = street,
          city = city,
          state = state,
          pincode = pincode,
          customerName = customerName,
          customerPhone = customerPhone
        )
      }

      // Clear cart
      repository.clearCart(currentCustomerId.value)
      appliedCoupon.value = null
      showToast("🎉 Order placed successfully!")
      onSuccess(lastOrderId)
    }
  }

  // Seller Actions
  fun updateSellerShop(
    shopId: String,
    name: String,
    description: String,
    address: String,
    city: String,
    state: String,
    pincode: String,
    isOpen: Boolean
  ) {
    viewModelScope.launch {
      val shop = shops.value.find { it.id == shopId }
      if (shop != null) {
        repository.updateShop(
          shop.copy(
            name = name,
            description = description,
            address = address,
            city = city,
            state = state,
            pincode = pincode,
            isOpen = isOpen
          )
        )
        showToast("Shop details updated")
      }
    }
  }

  fun saveProduct(
    id: String?,
    name: String,
    categoryId: String,
    emoji: String,
    price: Double,
    mrp: Double,
    stock: Int,
    description: String,
    sku: String,
    status: String
  ) {
    viewModelScope.launch {
      val sellerId = currentSellerId.value
      val shop = shops.value.find { it.sellerId == sellerId } ?: return@launch

      if (id != null) {
        val existing = products.value.find { it.id == id }
        if (existing != null) {
          repository.updateProduct(
            existing.copy(
              name = name,
              categoryId = categoryId,
              emoji = emoji,
              price = price,
              mrp = mrp,
              stock = stock,
              description = description,
              sku = sku,
              status = if (stock == 0) "outofstock" else status
            )
          )
          showToast("Product updated")
        }
      } else {
        val newId = "p-${System.currentTimeMillis()}"
        repository.insertProduct(
          ProductEntity(
            id = newId,
            sellerId = sellerId,
            shopId = shop.id,
            categoryId = categoryId,
            name = name,
            emoji = emoji,
            price = price,
            mrp = mrp,
            stock = stock,
            description = description,
            sku = sku,
            status = if (stock == 0) "outofstock" else status
          )
        )
        showToast("Product added to shop")
      }
    }
  }

  fun getProductsForShop(shopId: String) = repository.getProductsByShop(shopId)

  fun addProduct(
    name: String,
    price: Double,
    description: String,
    shopId: String,
    categoryId: String = "cat_grocery",
    emoji: String = "📦",
    mrp: Double = price,
    stock: Int = 10,
    sellerId: String = currentSellerId.value
  ) {
    viewModelScope.launch {
      val newId = "p-${System.currentTimeMillis()}"
      repository.insertProduct(
        ProductEntity(
          id = newId,
          sellerId = sellerId,
          shopId = shopId,
          categoryId = categoryId,
          name = name,
          emoji = emoji,
          price = price,
          mrp = mrp,
          stock = stock,
          description = description,
          status = if (stock == 0) "outofstock" else "active"
        )
      )
      showToast("Product $name added to catalog")
    }
  }

  fun deleteProduct(productId: String) {
    viewModelScope.launch {
      repository.deleteProduct(productId)
      showToast("Product deleted")
    }
  }

  fun updateOrderStatus(orderId: String, newStatus: String) {
    viewModelScope.launch {
      repository.updateOrderStatus(orderId, newStatus)
      showToast("Order status updated to $newStatus")
    }
  }

  // Admin Actions
  fun setSellerStatus(sellerId: String, status: String) {
    viewModelScope.launch {
      val seller = sellers.value.find { it.id == sellerId }
      if (seller != null) {
        repository.updateSeller(seller.copy(status = status))
        val shop = shops.value.find { it.sellerId == sellerId }
        if (shop != null && status == "approved") {
          repository.updateShop(shop.copy(isOpen = true))
        } else if (shop != null && status == "suspended") {
          repository.updateShop(shop.copy(isOpen = false))
        }
        showToast("Seller $status")
      }
    }
  }

  fun toggleShopOpen(shopId: String) {
    viewModelScope.launch {
      val shop = shops.value.find { it.id == shopId }
      if (shop != null) {
        repository.updateShop(shop.copy(isOpen = !shop.isOpen))
        showToast("Shop ${if (!shop.isOpen) "opened" else "closed"}")
      }
    }
  }

  fun toggleShopFeatured(shopId: String) {
    viewModelScope.launch {
      val shop = shops.value.find { it.id == shopId }
      if (shop != null) {
        repository.updateShop(shop.copy(isFeatured = !shop.isFeatured))
        showToast("Shop ${if (!shop.isFeatured) "featured" else "unfeatured"}")
      }
    }
  }

  fun addCategory(name: String, emoji: String, colorHex: String) {
    viewModelScope.launch {
      val id = "cat_${System.currentTimeMillis()}"
      repository.insertCategory(CategoryEntity(id, name, emoji, colorHex))
      showToast("Category added")
    }
  }

  fun deleteCategory(id: String) {
    viewModelScope.launch {
      repository.deleteCategory(id)
      showToast("Category removed")
    }
  }

  fun createCoupon(
    code: String,
    type: String,
    value: Double,
    minOrder: Double,
    maxDiscount: Double,
    limit: Int
  ) {
    viewModelScope.launch {
      val id = "cp_${System.currentTimeMillis()}"
      repository.insertCoupon(
        CouponEntity(
          id = id,
          code = code.uppercase().trim(),
          type = type,
          value = value,
          minOrder = minOrder,
          maxDiscount = maxDiscount,
          usageLimit = limit,
          used = 0,
          expiry = System.currentTimeMillis() + 86400000L * 30,
          isActive = true
        )
      )
      showToast("Coupon created successfully")
    }
  }

  fun deleteCoupon(id: String) {
    viewModelScope.launch {
      repository.deleteCoupon(id)
      showToast("Coupon deleted")
    }
  }

  fun updateCommission(rate: String) {
    viewModelScope.launch {
      repository.saveConfig("global_commission", rate)
      showToast("Global commission set to $rate%")
    }
  }

  fun resetDemoData() {
    viewModelScope.launch {
      repository.resetDemoData()
      appliedCoupon.value = null
      showToast("Demo data restored to initial state")
    }
  }
}
