package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PlatformConfigEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SeedData
import com.example.data.model.SellerEntity
import com.example.data.model.ShopEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    CategoryEntity::class,
    ShopEntity::class,
    ProductEntity::class,
    CustomerEntity::class,
    SellerEntity::class,
    CouponEntity::class,
    CartItemEntity::class,
    OrderEntity::class,
    PlatformConfigEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class LocalKartDatabase : RoomDatabase() {
  abstract fun localKartDao(): LocalKartDao

  companion object {
    @Volatile
    private var INSTANCE: LocalKartDatabase? = null

    fun getDatabase(context: Context): LocalKartDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          LocalKartDatabase::class.java,
          "localkart_database"
        )
        .addCallback(DatabaseCallback(CoroutineScope(Dispatchers.IO)))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    fun getDatabase(context: Context, scope: CoroutineScope): LocalKartDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          LocalKartDatabase::class.java,
          "localkart_database"
        )
        .addCallback(DatabaseCallback(scope))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateDatabase(database.localKartDao())
          }
        }
      }
    }

    suspend fun populateDatabase(dao: LocalKartDao) {
      dao.insertCategories(SeedData.categories)
      dao.insertSellers(SeedData.sellers)
      dao.insertShops(SeedData.shops)
      dao.insertProducts(SeedData.products)
      dao.insertCustomer(SeedData.defaultCustomer)
      dao.insertCoupons(SeedData.coupons)
      dao.insertOrders(SeedData.initialOrders)
      dao.saveConfig(PlatformConfigEntity("global_commission", "5.0"))
    }
  }
}
