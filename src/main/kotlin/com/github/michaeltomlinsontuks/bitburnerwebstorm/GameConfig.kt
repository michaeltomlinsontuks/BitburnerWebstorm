object GameConfig {
    const val PORT = 9990
    const val SCHEMA = "http"
    const val URL = "localhost"
    const val FILE_POST_URI = "/"
    val VALID_FILE_EXTENSIONS = listOf(".js", ".script", ".ns", ".txt")

    fun getServerUrl(): String {
        return "$SCHEMA://$URL:$PORT$FILE_POST_URI"
    }

    fun isValidFileExtension(filename: String): Boolean {
        return VALID_FILE_EXTENSIONS.any { filename.endsWith(it) }
    }
}