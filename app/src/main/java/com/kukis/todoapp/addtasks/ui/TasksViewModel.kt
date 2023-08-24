package com.kukis.todoapp.addtasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kukis.todoapp.addtasks.domain.AddTaskUseCase
import com.kukis.todoapp.addtasks.domain.GetTasksUseCase
import com.kukis.todoapp.addtasks.domain.UpdateTaskUseCase
import com.kukis.todoapp.addtasks.domain.DeleteTaskUseCase
import com.kukis.todoapp.addtasks.ui.TasksUiState.Error
import com.kukis.todoapp.addtasks.ui.TasksUiState.Success
import com.kukis.todoapp.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    //Antes de hacer persistencia
    //private val _tasks = mutableStateListOf<TaskModel>()
    //val tasks: List<TaskModel> = _tasks

    //Necesario para hacer persistencia con Room
    val uiState: StateFlow<TasksUiState> = getTasksUseCase().map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState.Loading)

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onDialogOpen() {
        _showDialog.value = true
    }

    fun onTasksCreated(task: String) {
        //Antes de hacer persistencia
        //_tasks.add(TaskModel(task = task))
        onDialogClose()

        //Necesario para hacer persistencia con Room
        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = task))
        }
    }

    fun onCheckBoxSelected(taskModel: TaskModel) {
        //Antes de hacer persistencia Actualizar check
//        val index = _tasks.indexOf(taskModel)
//        if (index != -1) {
//            _tasks[index] = _tasks[index].let {
//                it.copy(selected = !it.selected)
//            }
//        }
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }

    }

    fun onItemRemove(taskModel: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(taskModel)
        }
        //Antes de hacer persistencia Eliminar item
//        val task = _tasks.find { it.id == taskModel.id }
//        _tasks.remove(task)
    }
}
