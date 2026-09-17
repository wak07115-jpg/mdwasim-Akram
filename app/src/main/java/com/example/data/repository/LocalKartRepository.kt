package com.example.data.repository

import com.example.data.db.LocalKartDao
import com.example.data.db.LocalKartDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.OrderJsonHelper
import com.example.data.model.PlatformConfigEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SeedData
import com.example.data.model.SellerEntity
import com.example.data.model.ShopEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class LocalKartRepository(private val dao: LocalKartDao) {

  // Categories
  val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
  suspend fun insertCategory(category: CategoryEntity) = dao.insertCategory(category)
  suspend fun deleteCategory(id: String) = dao.deleteCategory(id)

  // Shops
  val allShops: Flow<List<ShopEntity>> = dao.getAllShops()
  fun getShopById(id: String): Flow<ShopEntity?> = dao.getShopById(id)
  fun getShopBySeller(sellerId: String): Flow<ShopEntity?> = dao.getShopBySellerId(sellerId)
  suspend fun updateShop(shop: ShopEntity) = dao.updateShop(shop)
  suspend fun insertShop(shop: ShopEntity) = dao.insertShop(shop)

  // Products
  val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
  fun getProductsByShop(shopId: String): Flow<List<ProductEntity>> = dao.getProductsByShop(shopId)
  fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> = dao.getProductsBySeller(sellerId)
  fun getProductById(id: String): Flow<ProductEntity?> = dao.getProductById(id)
  suspend fun insertProduct(product: ProductEntity) = dao.insertProduct(product)
  suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
  suspend fun deleteProduct(id: String) = dao.deleteProduct(id)

  // Customers
  val allCustomers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
  fun getCustomerById(id: String): Flow<CustomerEntity?> = dao.getCustomerById(id)
  suspend fun insertCustomer(customer: CustomerEntity) = dao.insertCustomer(customer)
  suspend fun updateCustomer(customer: CustomerEntity) = dao.updateCustomer(customer)

  // Sellers
  val allSellers: Flow<List<SellerEntity>> = dao.getAllSellers()
  fun getSellerById(id: String): Flow<SellerEntity?> = dao.getSellerById(id)
  suspend fun insertSeller(seller: SellerEntity) = dao.insertSeller(seller)
  suspend fun updateSeller(seller: SellerEntity) = dao.updateSeller(seller)

  // Coupons
  val allCoupons: Flow<List<CouponEntity>> = dao.getAllCoupons()
  suspend fun insertCoupon(coupon: CouponEntity) = dao.insertCoupon(coupon)
  suspend fun deleteCoupon(id: String) = dao.deleteCoupon(id)

  // Cart
  fun getCartForCustomer(customerId: String): Flow<List<CartItemEntity>> = dao.getCartForCustomer(customerId)

  suspend fun addToCart(customerId: String, product: ProductEntity, qtyToAdd: Int = 1) {
    val existing = dao.findCartItem(customerId, product.id)
    if (existing != null) {
      dao.updateCartItem(existing.copy(qty = existing.qty + qtyToAdd))
    } else {
      dao.insertCartItem(
        CartItemEntity(
          customerId = customerId,
          productId = product.id,
          shopId = product.shopId,
          sellerId = product.sellerId,
          qty = qtyToAdd
        )
      )
    }
  }

  suspend fun updateCartQty(cartItemId: Int, newQty: Int) {
    if (newQty <= 0) {
      dao.deleteCartItem(cartItemId)
    } else {
      // Find item
      val allCarts = dao.getCartForCustomer("c1").firstOrNull() ?: emptyList()
      val item = allCarts.find { it.id == cartItemId }
      if (item != null) {
        dao.updateCartItem(item.copy(qty = newQty))
      }
    }
  }

  suspend fun removeCartItem(cartItemId: Int) = dao.deleteCartItem(cartItemId)
  suspend fun clearCart(customerId: String) = dao.clearCart(customerId)

  // Orders
  val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
  fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>> = dao.getOrdersForCustomer(customerId)
  fun getOrdersForSeller(sellerId: String): Flow<List<OrderEntity>> = dao.getOrdersForSeller(sellerId)
  fun getOrderById(id: String): Flow<OrderEntity?> = dao.getOrderById(id)
  suspend fun updateOrderStatus(orderId: String, newStatus: String) {
    val order = dao.getOrderById(orderId).firstOrNull() ?: return
    dao.updateOrder(order.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
  }

  suspend fun placeOrder(
    customerId: String,
    shopId: String,
    sellerId: String,
    items: List<OrderItem>,
    subtotal: Double,
    delivery: Double,
    discount: Double,
    paymentMethod: String,
    couponId: String?,
    street: String,
    city: String,
    state: String,
    pincode: String,
    customerName: String,
    customerPhone: String
  ): String {
    val orderId = "ORD-${(1000 + (Math.random() * 9000).toInt())}"
    val total = (subtotal + delivery - discount).coerceAtLeast(0.0)
    val order = OrderEntity(
      id = orderId,
      customerId = customerId,
      shopId = shopId,
      sellerId = sellerId,
      itemsJson = OrderJsonHelper.serialize(items),
      subtotal = subtotal,
      delivery = delivery,
      discount = discount,
      total = total,
      status = "pending",
      paymentMethod = paymentMethod,
      couponId = couponId,
      street = street,
      city = city,
      state = state,
      pincode = pincode,
      customerName = customerName,
      customerPhone = customerPhone,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )
    dao.insertOrder(order)

    // Decrement stock
    for (item in items) {
      val product = dao.getProductById(item.productId).firstOrNull()
      if (product != null) {
        val newStock = (product.stock - item.qty).coerceAtLeast(0)
        val newStatus = if (newStock == 0) "outofstock" else product.status
        dao.updateProduct(product.copy(stock = newStock, status = newStatus))
      }
    }

    return orderId
  }

  // Configs
  fun getConfig(key: String): Flow<PlatformConfigEntity?> = dao.getConfig(key)
  suspend fun saveConfig(key: String, value: String) = dao.saveConfig(PlatformConfigEntity(key, value))

  // Reset Demo Data
  suspend fun resetDemoData() {
    dao.clearCategories()
    dao.clearShops()
    dao.clearProducts()
    dao.clearSellers()
    dao.clearCustomers()
    dao.clearCoupons()
    dao.clearOrders()
    dao.clearAllCartItems()
    LocalKartDatabase.populateDatabase(dao)
  }

  // Check if database is empty and seed if needed
  suspend fun ensureSeeded() {
    val shops = dao.getAllShops().firstOrNull()
    if (shops.isNullOrEmpty()) {
      LocalKartDatabase.populateDatabase(dao)
    }
  }
}
