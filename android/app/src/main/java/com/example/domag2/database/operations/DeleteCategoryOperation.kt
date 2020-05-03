package com.example.domag2.database.operations

import com.example.domag2.database.database.AppDatabase
import com.example.domag2.database.relations.CategoryWithContents

suspend fun AppDatabase.deleteCategory(category: CategoryWithContents) {
    val categoryDao = categoryDao()
    val itemDao = itemDao()
    category.categories.forEach {
        it.uid?.let { childId ->
            deleteCategory(categoryDao.findWithContentsByIdImmediately(childId))
        }
    }
    category.items.forEach {
        itemDao.delete(it)
    }
    categoryDao.delete(category.category)
}

