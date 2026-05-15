package com.example.matrusneh.data.repository

import com.example.matrusneh.data.local.KickDao
import com.example.matrusneh.data.local.KickEntity
import com.example.matrusneh.data.local.UserDao
import com.example.matrusneh.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val kickDao: KickDao
) {
    val user: Flow<UserEntity?> = userDao.getUser()
    val totalKicks: Flow<Int> = kickDao.getTotalKicks()

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateLanguage(lang: String) {
        userDao.updateLanguage(lang)
    }

    suspend fun updateReminderPreference(enabled: Boolean) {
        userDao.updateReminderPreference(enabled)
    }

    suspend fun insertKick(timestamp: Long) {
        kickDao.insertKick(KickEntity(timestamp = timestamp))
    }

    fun getKicksForToday(startOfDay: Long, endOfDay: Long): Flow<Int> {
        return kickDao.getKicksForToday(startOfDay, endOfDay)
    }
}
