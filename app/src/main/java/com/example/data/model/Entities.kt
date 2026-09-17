package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val emoji: String,
  val colorHex: String
)

@Entity(tableName = "shops")
data class ShopEntity(
  @PrimaryKey val id: String,
  val sellerId: String,
  val name: String,
  val category: String,
  val emoji: String,
  val banner: String,
  val description: String,
  val address: String,
  val city: String,
  val state: String,
  val pincode: String,
  val rating: Float,
  val isOpen: Boolean,
  val isFeatured: Boolean
)

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey val id: String,
  val sellerId: String,
  val shopId: String,
  val categoryId: String,
  val name: String,
  val emoji: String,
  val price: Double,
  val mrp: Double,
  val stock: Int,
  val description: String,
  val sku: String = "",
  val status: String = "active", // active, draft, outofstock, hidden
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val password: String = "demo123",
  val phone: String,
  val street: String = "",
  val city: String = "Bengaluru",
  val state: String = "Karnataka",
  val pincode: String = "560001",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sellers")
data class SellerEntity(
  @PrimaryKey val id: String,
  val ownerName: String,
  val email: String,
  val password: String = "demo123",
  val phone: String,
  val status: String = "approved", // approved, pending, suspended, rejected
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coupons")
data class CouponEntity(
  @PrimaryKey val id: String,
  val code: String,
  val type: String, // percent, fixed
  val value: Double,
  val minOrder: Double,
  val maxDiscount: Double,
  val usageLimit: Int,
  val used: Int,
  val expiry: Long,
  val scope: String = "platform", // platform, seller
  val sellerId: String? = null,
  val isActive: Boolean = true
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val customerId: String,
  val productId: String,
  val shopId: String,
  val sellerId: String,
  val qty: Int
)

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey val id: String,
  val customerId: String,
  val shopId: String,
  val sellerId: String,
  val itemsJson: String, // formatted json string of List<OrderItem>
  val subtotal: Double,
  val delivery: Double,
  val discount: Double,
  val total: Double,
  val status: String, // pending, confirmed, packed, shipped, out_for_delivery, delivered, cancelled
  val paymentMethod: String, // cod, online
  val couponId: String? = null,
  val street: String,
  val city: String,
  val state: String,
  val pincode: String,
  val customerName: String,
  val customerPhone: String,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_configs")
data class PlatformConfigEntity(
  @PrimaryKey val key: String,
  val value: String
)

data class OrderItem(
  val productId: String,
  val name: String,
  val emoji: String,
  val price: Double,
  val qty: Int,
  val shopId: String,
  val sellerId: String
)

enum class UserRole {
  CUSTOMER,
  SELLER,
  ADMIN
}
