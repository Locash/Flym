package net.frju.flym.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import net.frju.flym.data.entities.Item
import net.frju.flym.data.entities.ItemWithFeed


@Dao
interface ItemDao {

    @Query("SELECT * FROM items INNER JOIN feeds ON items.feedId = feeds.feedId WHERE fetchDate <= :arg0 ORDER BY publicationDate DESC, id")
    fun observeAll(maxDate: Long): LiveData<List<ItemWithFeed>>

    @Query("SELECT COUNT(*) FROM items WHERE read = 0 AND fetchDate > :arg0")
    fun observeNewItemsCount(minDate: Long): LiveData<Long>

    @get:Query("SELECT * FROM items INNER JOIN feeds ON items.feedId = feeds.feedId WHERE favorite = 1")
    val favorites: List<ItemWithFeed>

    @Query("SELECT * FROM items INNER JOIN feeds ON items.feedId = feeds.feedId WHERE items.feedId IS :arg0 AND fetchDate <= :arg1")
    fun observeByFeed(feedId: String, maxDate: Long): LiveData<List<ItemWithFeed>>

    @Query("SELECT * FROM items INNER JOIN feeds ON items.feedId = feeds.feedId WHERE groupId IS :arg0 AND fetchDate <= :arg1")
    fun observeByGroup(groupId: String, maxDate: Long): LiveData<List<ItemWithFeed>>

    @get:Query("SELECT COUNT(*) FROM items WHERE read = 0")
    val countUnread: Int

    @Query("SELECT * FROM items WHERE id IS :arg0 LIMIT 1")
    fun findById(id: String): Item?

    @Query("SELECT * FROM items WHERE id IN (:arg0)")
    fun findByIds(ids: List<String>): List<Item>

    @Query("UPDATE items SET read = 1 WHERE id IN (:arg0)")
    fun markAsRead(ids: List<String>)

    @Query("DELETE FROM items WHERE fetchDate < :arg0 AND favorite = 0")
    fun deleteOlderThan(keepDateBorderTime: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Item)

    @Delete
    fun deleteAll(items: Item)
}