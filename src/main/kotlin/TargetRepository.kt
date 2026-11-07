package com.blastclouddi

object TargetRepository {
    // Храним цели в изменяемом списке
    private val targets = mutableListOf<Target>()

    // Получить все цели
    fun getAll(): List<Target> = targets.toList()

    // Добавить новую цель
    fun add(target: Target) {
        targets.add(target)
    }

    // Удалить цель по ID (возвращает true, если удаление прошло успешно)
    fun remove(id: String): Boolean {
        return targets.removeIf { it.id == id }
    }

    // Найти цель по ID
    fun findById(id: String): Target? {
        return targets.find { it.id == id }
    }

    fun clear() {
        targets.clear()
    }
}