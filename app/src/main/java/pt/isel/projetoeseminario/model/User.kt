package pt.isel.projetoeseminario.model

data class UserLoginInputModel(
    val email: String,
    val password: String
)

data class UserLoginOutputModel(
    val token: String
)

data class ImageOutputModel(
    val foto: String
)

data class UserSignupInputModel(
    val name: String,
    val email: String,
    val password: String
)

data class UserSignupOutputModel(
    val message: String
)

data class UserOutputModel(
    val nome: String,
    val email: String,
)

data class UserAuthInputModel(
    val token: String
)
