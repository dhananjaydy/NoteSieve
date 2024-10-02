package com.example.notesieve.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [NoteSieveEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 2,
            to = 3,
            spec = NoteSieveDatabase.RemoveShowOptions::class
        )
    ]
)
abstract class NoteSieveDatabase : RoomDatabase() {

    abstract fun noteSieveDao(): NoteSieveDao


    @DeleteColumn(
        tableName = "notifications_table",
        columnName = "showOptions"
    )
    class RemoveShowOptions : AutoMigrationSpec
}
