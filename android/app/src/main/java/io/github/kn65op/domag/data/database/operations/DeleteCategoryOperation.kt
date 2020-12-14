package io.github.kn65op.domag.data.database.operations

import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.relations.CategoryWithContents

suspend fun AppDatabase.deleteCategory(category: CategoryWithContents) {
    val categoryDao = categoryDao()
    val itemDao = itemDao()
    val consumeDao = consumeDao()
    val categoryLimitDao = categoryLimitDao()

    category.categories.forEach {
        it.uid?.let { childId ->
            deleteCategory(categoryDao.findWithContentsByIdImmediately(childId))
        }
    }
    category.items.forEach {
        itemDao.delete(it)
    }
    categoryDao.delete(category.category)

    category.category.uid?.let { uid ->
        consumeDao.findByCategoryImmediately(categoryId = uid).forEach { consume ->
            consumeDao.delete(consume = consume)
        }
    }

    category.limits?.let {
        categoryLimitDao.delete(it)
    }
}

