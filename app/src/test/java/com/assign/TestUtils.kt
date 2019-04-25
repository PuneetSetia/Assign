package com.assign

import com.assign.beans.Delivery
import com.assign.beans.Location

class TestUtils{
    companion object {
        fun getData(startIndex : Int, count : Int): List<Delivery> {
            val list = ArrayList<Delivery>()
            for (i in startIndex until startIndex+count) {
                val delivery = Delivery(
                    i, "desc", "image_url",
                    Location(1.1, 1.2, "address")
                )
                list.add(delivery)
            }
            return list
        }

    }

}