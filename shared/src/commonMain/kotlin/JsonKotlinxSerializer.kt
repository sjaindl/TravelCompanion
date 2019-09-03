import io.ktor.client.call.TypeInfo
import io.ktor.client.features.json.JsonSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import kotlinx.io.core.Input
import kotlinx.io.core.readText
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

class JsonKotlinxSerializer : JsonSerializer {

    override fun read(type: TypeInfo, body: Input): Any {
        val mapper = mappers[type.type]!!
        val text = body.readText()

        return Json.nonstrict.parse(mapper, text)
    }

    val mappers = mutableMapOf<KClass<Any>, KSerializer<Any>>()

    /**
     * Set mapping from [type] to generated [KSerializer].
     */
    inline fun <reified T> setMapper(serializer: KSerializer<T>) {
        mappers[T::class as KClass<Any>] = serializer as KSerializer<Any>
    }

    override fun write(data: Any, contentType: ContentType): OutgoingContent {
        @Suppress("UNCHECKED_CAST")
        val content = Json.nonstrict.stringify(mappers[data::class]!!, data)
        return TextContent(content, contentType) //ContentType.Application.Json
    }
}