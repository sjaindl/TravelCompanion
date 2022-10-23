import io.ktor.client.features.json.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

class JsonKotlinxSerializer : JsonSerializer {

    override fun read(type: io.ktor.util.reflect.TypeInfo, body: Input): Any {

        return super.read(type, body)
    }

    /*
    override fun read(type: TypeInfo, body: Input): Any {
        val mapper = mappers[type.type]!!
        val text = body.readText()

        return Json.nonstrict.parse(mapper, text)
    }

     */

    val mappers = mutableMapOf<KClass<Any>, KSerializer<Any>>()

    /**
     * Set mapping from [type] to generated [KSerializer].
     */
    inline fun <reified T> setMapper(serializer: KSerializer<T>) {
        mappers[T::class as KClass<Any>] = serializer as KSerializer<Any>
    }

    override fun write(data: Any, contentType: ContentType): OutgoingContent {
        TODO("Not yet implemented")
    }

    /*
    override fun write(data: Any, contentType: ContentType): OutgoingContent {
        @Suppress("UNCHECKED_CAST")
        val content = Json.nonstrict.stringify(mappers[data::class]!!, data)
        return TextContent(content, contentType) //ContentType.Application.Json
    }

     */
}