package com.ale.devkit.lab.projects.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * POC: MDC (correlationId) + coroutines.
 *
 * Como testar:
 * 1. Suba a aplicação
 * 2. curl localhost:8080/mdc-sem-tratamento
 *    -> observe no log: a linha "ANTES do delay" mostra correlationId=xxxx normalmente.
 *       a linha "DEPOIS do delay" pode aparecer com correlationId= (vazio) — a coroutine
 *       pode ter retomado em outra thread, onde o MDC (que é ThreadLocal) nunca foi setado.
 *       Nota: em cargas baixas o Kotlin às vezes reaproveita a mesma thread, então rode
 *       algumas vezes em paralelo pra aumentar a chance de ver a troca de thread de fato:
 *       for i in {1..10}; do curl -s localhost:8080/mdc-sem-tratamento & done
 *
 * 3. curl localhost:8080/mdc-com-tratamento
 *    -> com withContext(MDCContext()), o correlationId persiste em ambas as linhas,
 *       mesmo que a coroutine troque de thread por baixo dos panos.
 */
@RestController
class MdcCoroutineController {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/mdc-sem-tratamento")
    suspend fun semTratamento(): String {
        val correlationId = UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)

        log.info("ANTES do delay (thread da requisicao)")
        delay(300) // ponto onde a coroutine pode trocar de thread
        log.info("DEPOIS do delay - repare se o correlationId sumiu do log acima")

        val valorAposDelay = MDC.get("correlationId")
        return "correlationId gerado: $correlationId | apos o delay: ${valorAposDelay ?: "SUMIU (esperado sem MDCContext)"}"
    }

    @GetMapping("/mdc-com-tratamento")
    suspend fun comTratamento(): String {
        val correlationId = UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)

        log.info("ANTES do delay (thread da requisicao)")

        return withContext(MDCContext()) {
            delay(300)
            log.info("DEPOIS do delay - com MDCContext o correlationId deveria persistir")
            "correlationId gerado: $correlationId | apos o delay: ${MDC.get("correlationId")}"
        }
    }
}
