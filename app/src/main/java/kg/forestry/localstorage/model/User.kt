package kg.forestry.localstorage.model

data class User(
    var username: String = "",
    var lang: String = "",
    var userPhoto: String = "",
    var email : String = "",
    var post : String = "",
    var organization: String = "",
    var date: String = "",
    var phone: String = ""){

    fun isUserExists() =
        this.username.isNotEmpty()
                && this.email.isNotEmpty()
                && this.post.isNotEmpty()
                && this.organization.isNotEmpty()
}


