package com.example.androidtbc.domain.mapper

import com.example.androidtbc.data.local.entity.UserEntity
import com.example.androidtbc.data.remote.dto.User
import com.example.androidtbc.domain.model.User as DomainUser

object UserMapper {
    fun mapEntityToDomain(entity: UserEntity): DomainUser {
        return DomainUser(
            id = entity.id,
            email = entity.email,
            firstName = entity.firstName,
            lastName = entity.lastName,
            avatar = entity.avatar
        )
    }

    fun mapDtoToEntity(dto: User, lastUpdated: Long): UserEntity {
        return UserEntity(
            id = dto.id,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            avatar = dto.avatar,
            lastUpdated = lastUpdated
        )
    }


}