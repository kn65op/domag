package io.github.kn65op.domag.data.database.database

class DatabaseFactoryImpl(factoryInit: DatabaseFactory? = null) {
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
