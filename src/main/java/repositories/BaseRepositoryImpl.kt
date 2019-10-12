package repositories

open class BaseRepositoryImpl <T> : BaseRepository<T> {

    override fun save(entity: T): T {
        return entity
    }


}