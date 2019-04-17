package com.assign.beans
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Delivery(@PrimaryKey val id : Int,
               val description : String , val imageUrl : String,
               @Embedded val location : Location)