package com.example.am_clicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class MainMenuEvent {
    object NavigateToGame : MainMenuEvent()
    object NavigateToProfile : MainMenuEvent()
    object NavigateToAchievements : MainMenuEvent()
    object NavigateToCredits : MainMenuEvent()
    object NavigateToAtlas : MainMenuEvent() // <-- DODANE ZDARZENIE ATLASU
}

class MainMenuViewModel : ViewModel() {
    private val _menuEvents = MutableSharedFlow<MainMenuEvent>()
    val menuEvents = _menuEvents.asSharedFlow()

    fun onPlayClicked() { viewModelScope.launch { _menuEvents.emit(MainMenuEvent.NavigateToGame) } }
    fun onProfileClicked() { viewModelScope.launch { _menuEvents.emit(MainMenuEvent.NavigateToProfile) } }
    fun onAchievementsClicked() { viewModelScope.launch { _menuEvents.emit(MainMenuEvent.NavigateToAchievements) } }
    fun onCreditsClicked() { viewModelScope.launch { _menuEvents.emit(MainMenuEvent.NavigateToCredits) } }
    fun onAtlasClicked() { viewModelScope.launch { _menuEvents.emit(MainMenuEvent.NavigateToAtlas) } } // <-- DODANA FUNKCJA ATLASU
}