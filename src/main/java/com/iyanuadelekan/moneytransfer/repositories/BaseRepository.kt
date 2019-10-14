package com.iyanuadelekan.moneytransfer.repositories

interface BaseRepository<T> {

    fun save(entity: T): T

    fun countRecords(): Int
}