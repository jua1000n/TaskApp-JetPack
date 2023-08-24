package com.kukis.todoapp.addtasks.ui

import com.kukis.todoapp.addtasks.ui.model.TaskModel

//Necesario para hacer persistencia con Room
sealed interface TasksUiState {
    object Loading:TasksUiState
    data class Error(val throwable: Throwable):TasksUiState
    data class Success(val tasks:List<TaskModel>):TasksUiState
}