package com.devsia.blog.preference

class Const {
     companion object Preference{
       fun savedTags() = "SAVED_TAGS"
       fun savedToken() = "SAVED_TOKEN"
     }

    class Setting {
        companion object Setting {
            fun countCharsForPost() = 450
        }
    }

    class Extra {
        companion object Extra {
            fun extraPost() = "POST"
            fun extraTag() = "TAG"
        }
    }
}