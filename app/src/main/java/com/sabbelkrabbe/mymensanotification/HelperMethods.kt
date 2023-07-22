package com.sabbelkrabbe.mymensanotification

class HelperMethods {
    companion object {
        fun getIndex(array: Array<String>, s: String): Int{
            for (i in array.indices){
                if(s == array[i]){
                    return i
                }
            }
            return -1
        }
    }
}
