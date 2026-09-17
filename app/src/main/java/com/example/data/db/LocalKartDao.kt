package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PlatformConfigEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SellerEntity
import com.example.data.model.ShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalKartDao {
  // Categories
  @Query("SELECT * FROM categories")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteCategory(id: String)

  // Shops
  @Query("SELECT * FROM shops")
  fun getAllShops(): Flow<List<ShopEntity>>

  @Query("SELECT * FROM shops WHERE id = :id")
  fun getShopById(id: String): Flow<ShopEntity?>

  @Query("SELECT * FROM shops WHERE sellerId = :sellerId LIMIT 1")
  fun getShopBySellerId(sellerId: String): Flow<ShopEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertShop(shop: ShopEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertShops(shops: List<ShopEntity>)

  @Update
  suspend fun updateShop(shop: ShopEntity)

  // Products
  @Query("SELECT * FROM products ORDER BY createdAt DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE shopId = :shopId")
  fun getProductsByShop(shopId: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
  fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  fun getProductById(id: String): Flow<ProductEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProducts(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun deleteProduct(id: String)

  // Customers
  @Query("SELECT * FROM customers")
  fun getAllCustomers(): Flow<List<CustomerEntity>>

  @Query("SELECT * FROM customers WHERE id = :id")
  fun getCustomerById(id: String): Flow<CustomerEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerEntity)

  @Update
  suspend fun updateCustomer(customer: CustomerEntity)

  // Sellers
  @Query("SELECT * FROM sellers")
  fun getAllSellers(): Flow<List<SellerEntity>>

  @Query("SELECT * FROM sellers WHERE id = :id")
  fun getSellerById(id: String): Flow<SellerEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSeller(seller: SellerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSellers(sellers: List<SellerEntity>)

  @Update
  suspend fun updateSeller(seller: SellerEntity)

  // Coupons
  @Query("SELECT * FROM coupons")
  fun getAllCoupons(): Flow<List<CouponEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCoupon(coupon: CouponEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCoupons(coupons: List<CouponEntity>)

  @Query("DELETE FROM coupons WHERE id = :id")
  suspend fun deleteCoupon(id: String)

  // Cart
  @Query("SELECT * FROM cart_items WHERE customerId = :customerId")
  fun getCartForCustomer(customerId: String): Flow<List<CartItemEntity>>

  @Query("SELECT * FROM cart_items WHERE customerId = :customerId AND productId = :productId LIMIT 1")
  suspend fun findCartItem(customerId: String, productId: String): CartItemEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCartItem(cartItem: CartItemEntity)

  @Update
  suspend fun updateCartItem(cartItem: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE id = :id")
  suspend fun deleteCartItem(id: Int)

  @Query("DELETE FROM cart_items WHERE customerId = :customerId")
  suspend fun clearCart(customerId: String)

  // Orders
  @Query("SELECT * FROM orders ORDER BY createdAt DESC")
  fun getAllOrders(): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
  fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY createdAt DESC")
  fun getOrdersForSeller(sellerId: String): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE id = :id")
  fun getOrderById(id: String): Flow<OrderEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrders(orders: List<OrderEntity>)

  @Update
  suspend fun updateOrder(order: OrderEntity)

  // Configs
  @Query("SELECT * FROM platform_configs WHERE key = :key LIMIT 1")
  fun getConfig(key: String): Flow<PlatformConfigEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveConfig(config: PlatformConfigEntity)

  // Clear all for demo reset
  @Query("DELETE FROM categories")
  suspend fun clearCategories()

  @Query("DELETE FROM shops")
  suspend fun clearShops()

  @Query("DELETE FROM products")
  suspend fun clearProducts()

  @Query("DELETE FROM sellers")
  suspend fun clearSellers()

  @Query("DELETE FROM customers")
  suspend fun clearCustomers()

  @Query("DELETE FROM coupons")
  suspend fun clearCoupons()

  @Query("DELETE FROM orders")
  suspend fun clearOrders()

  @Query("DELETE FROM cart_items")
  suspend fun clearAllCartItems()
}
