package io.kn65op.domag2.database.database

class DatabaseFactoryImpl(val factoryInit: DatabaseFactory? = null) {
    init {
        if (factoryInit != null)
            factoryInstance = factoryInit
    }

    var factory: DatabaseFactory
        get() = factoryInstance
        set(value) {
            factoryInstance = value
        }

    companion object {
        private var factoryInstance: DatabaseFactory = SqlDatabaseFactory()
    }
}
