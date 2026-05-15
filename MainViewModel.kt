package com.example.matrusneh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matrusneh.data.local.UserEntity
import com.example.matrusneh.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val user: StateFlow<UserEntity?> = repository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val totalKicks: StateFlow<Int> = repository.totalKicks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    
    // Derived kicks for today
    fun getKicksForToday(): StateFlow<Int> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis - 1

        return repository.getKicksForToday(startOfDay, endOfDay).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    }

    fun saveUser(name: String, email: String) {
        viewModelScope.launch {
            repository.insertUser(UserEntity(name = name, email = email))
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            repository.updateLanguage(languageCode)
        }
    }

    fun setReminderPreference(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReminderPreference(enabled)
            // Note: In a full app, you would also interact with WorkManager here
            // to actually schedule/cancel the ReminderWorker.
        }
    }

    fun addKick() {
        viewModelScope.launch {
            repository.insertKick(System.currentTimeMillis())
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
