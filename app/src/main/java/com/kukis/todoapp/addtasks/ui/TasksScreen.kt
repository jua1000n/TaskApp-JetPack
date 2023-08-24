package com.kukis.todoapp.addtasks.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kukis.todoapp.addtasks.ui.model.TaskModel

//@Preview
@Composable
//fun TasksScreen() {
fun TasksScreen(tasksViewModel: TasksViewModel) {
//    val showDialog: Boolean by tasksViewModel.showDialog.observeAsState(false)
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AddTasksDialog(show = showDialog, onDismiss = {
//            tasksViewModel.onDialogClose()
//        }, onTaskAdded = {
//            tasksViewModel.onTasksCreated(it)
//        })
//        FabDialog(Modifier.align(Alignment.BottomEnd), tasksViewModel)
//        TasksList(tasksViewModel)
//    }

    //Para crear persistencia
    val showDialog: Boolean by tasksViewModel.showDialog.observeAsState(false)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<TasksUiState>(
        initialValue = TasksUiState.Loading,
        key1 = lifecycle,
        key2 = tasksViewModel
    ){
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            tasksViewModel.uiState.collect{ value = it }
        }
    }

    when(uiState) {
        is TasksUiState.Error -> {}
        TasksUiState.Loading -> { CircularProgressIndicator() }
        is TasksUiState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AddTasksDialog(show = showDialog, onDismiss = {
                    tasksViewModel.onDialogClose()
                }, onTaskAdded = {
                    tasksViewModel.onTasksCreated(it)
                })
                FabDialog(Modifier.align(Alignment.BottomEnd), tasksViewModel)
                TasksList( (uiState as TasksUiState.Success ).tasks, tasksViewModel)
            }
        }
    }
}



@Composable
//st(tasksViewModel: TasksViewModel) {  Sin persistencia
fun TasksList(tasks: List<TaskModel>, tasksViewModel: TasksViewModel) {
//    Antes de hacer persistencia
//    val myTasks: List<TaskModel> = tasksViewModel.tasks
//    LazyColumn {
//        items(myTasks, key = { it.id }) {
//            ItemTask(taskModel = it, tasksViewModel = tasksViewModel)
//        }
//    }
    LazyColumn {
        items(tasks, key = { it.id }) {
            ItemTask(taskModel = it, tasksViewModel = tasksViewModel)
        }
    }
}

@Composable
fun ItemTask(taskModel: TaskModel, tasksViewModel: TasksViewModel) {
    Card(
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    tasksViewModel.onItemRemove(taskModel)
                })
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF9E9E9E))
                .padding(8.dp)
        ) {
            Text(
                text = taskModel.task, modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = taskModel.selected,
                onCheckedChange = { tasksViewModel.onCheckBoxSelected(taskModel) })
        }
    }
}

@Composable
fun FabDialog(modifier: Modifier, tasksViewModel: TasksViewModel) {
    FloatingActionButton(modifier = modifier.padding(16.dp), onClick = {
        tasksViewModel.onDialogOpen()
    }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTasksDialog(show: Boolean, onTaskAdded: (String) -> Unit, onDismiss: () -> Unit) {
    var myTask by rememberSaveable { mutableStateOf("") }
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Añade tu tarea", fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(8.dp))
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = myTask,
                    maxLines = 1,
                    singleLine = true,
                    onValueChange = { myTask = it })
                Spacer(modifier = Modifier.size(16.dp))
                Button(shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onTaskAdded(myTask)
                        myTask = ""
                    }) {
                    Text(text = "Añadir tarea")
                }
            }
        }
    }
}
