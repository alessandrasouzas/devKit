import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GoogleBooksClient (

    @Value("\${integration.google.books.url}")
    private val url: String
){

    private val restTemplate = RestTemplate()

    /*
    TODO:
        Refinar busca usando isbn
        Incluir comic view api ou outro para refinar buscas
     */
    fun buscarPorTitulo(titulo: String): String? {

        val url = "$url{$titulo}"

        val response = restTemplate.getForObject(url, Map::class.java)

        val items = response?.get("items") as? List<*>
        val firstItem = items?.firstOrNull() as? Map<*, *>
        val volumeInfo = firstItem?.get("volumeInfo") as? Map<*, *>

        val industryIdentifiers = volumeInfo?.get("industryIdentifiers") as? List<*>

        val isbn = industryIdentifiers
            ?.mapNotNull { it as? Map<*, *> }
            ?.firstOrNull { it["type"] == "ISBN_13" }
            ?.get("identifier") as? String

        return isbn
    }
}