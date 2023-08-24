package com.kukis.todoapp.addtasks.domain

import com.kukis.todoapp.addtasks.data.TaskRepository
import com.kukis.todoapp.addtasks.ui.model.TaskModel
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskModel: TaskModel) {
        taskRepository.delete(taskModel)
    }
}
