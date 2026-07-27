package com.ale.devkit.lab.projects.coroutines

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

/**
 * Case: circuit breaker com Resilience4j + coroutines.
 */
@RestController
class PismoCircuitBreakerController(
    // O Spring injeta aqui o registry configurado a partir do application.yml.
    // O registry é criado UMA VEZ pra aplicação inteira (é um bean singleton).
    circuitBreakerRegistry: CircuitBreakerRegistry
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // "pismo" é o NOME da instância. O registry procura configuração específica
    // para "pismo" em resilience4j.circuitbreaker.instances.pismo no yml;
    // se não achar, cai no "default".
    // Por que usar o registry em vez de CircuitBreaker.ofDefaults("pismo")
    // ou CircuitBreaker.of("pismo", config)?
    //   -> ofDefaults/of criam uma instância NOVA e ISOLADA, sem ligação com o
    //      Spring/actuator, sem métricas expostas automaticamente, e você teria
    //      que gerenciar o ciclo de vida (e configuração) na mão.
    //   -> o registry é o "catálogo" central: qualquer outra classe que pedir
    //      circuitBreakerRegistry.circuitBreaker("pismo") pega a MESMA instância
    //      (mesmo estado, mesmas métricas). Isso é essencial se você tiver
    //      várias classes chamando o mesmo serviço externo (Pismo) — todas
    //      compartilham o mesmo "disjuntor".
    private val circuitBreaker: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("pismo")

    // Só um contador pra simular "toda 3ª chamada falha" — não tem nada a ver
    // com o Resilience4j, é decoração da simulação.
    private val contadorChamadas = AtomicInteger(0)

    @GetMapping("/bloquear-conta")
    suspend fun bloquearConta(): String {
        return try {
            // executeSuspendFunction é a versão do Resilience4j-Kotlin pensada para funções `suspend`. Existem "irmãs" dela na lib:
            //   - executeCallable { }      -> para código bloqueante (Callable<T>)
            //   - executeFunction { }      -> para uma função síncrona comum
            //   - decorateSuspendFunction  -> NÃO executa na hora, retorna uma
            //                                 função "decorada" pra você chamar depois
            // Usamos executeSuspendFunction porque chamarPismoFake() é suspend
            // (usa delay, que é coroutine-friendly) e queremos executar JÁ,
            // não guardar pra depois.
            //
            // O que ela faz por baixo dos panos:
            //   1. Verifica o estado do circuito ANTES de chamar o bloco.
            //   2. Se CLOSED ou HALF_OPEN (testando): executa o bloco normalmente.
            //   3. Se OPEN: nem executa o bloco — lança CallNotPermittedException
            //      na hora (é por isso que o log "chamarPismoFake" some quando
            //      o circuito está aberto: a função de fato não roda).
            //   4. Registra o resultado (sucesso/falha/tempo) nas métricas internas,
            //      que alimentam a decisão de abrir/fechar o circuito.
            circuitBreaker.executeSuspendFunction {
                chamarPismoFake()
            }
        } catch (e: Exception) {
            // Cai aqui tanto quando a Pismo fake falha de verdade (RuntimeException)
            // quanto quando o circuito está OPEN (CallNotPermittedException).
            // circuitBreaker.state te diz qual dos dois casos foi.
            log.error("Bloqueio NAO confirmado. Estado do circuito: {} | motivo: {}",
                circuitBreaker.state, e.message)
            "503 - bloqueio nao confirmado (circuito: ${circuitBreaker.state})"
        }
    }

    @GetMapping("/circuito-status")
    fun circuitoStatus(): Map<String, Any> {
        // circuitBreaker.metrics é um snapshot IMUTÁVEL do estado atual da
        // janela deslizante — não é histórico total desde o início da app,
        // é uma "foto" da janela configurada (ex.: últimas 10 chamadas).
        val metrics = circuitBreaker.metrics
        return mapOf(
            // state: enum -> CLOSED, OPEN, HALF_OPEN, DISABLED, FORCED_OPEN, METRICS_ONLY
            "estado" to circuitBreaker.state.toString(),
            "chamadasComFalha" to metrics.numberOfFailedCalls,
            "chamadasComSucesso" to metrics.numberOfSuccessfulCalls,
            // failureRate: percentual de falha na janela atual;
            // vem -1 se ainda não há chamadas suficientes pra calcular
            // (menos que minimum-number-of-calls)
            "taxaDeFalha" to metrics.failureRate
        )
    }

    // Simula a chamada real à Pismo: falha a cada 3 chamadas, pra forçar o circuito a reagir
    private suspend fun chamarPismoFake(): String {
        delay(200) // simula latência de rede (200ms) — não é parte do resilience4j,
        // só faz a chamada "parecer" uma chamada de rede real
        val chamadaAtual = contadorChamadas.incrementAndGet()
        log.info("Chamando Pismo fake - tentativa numero {}", chamadaAtual)
        if (chamadaAtual % 3 == 0) {
            log.warn("Pismo fake falhando de proposito na chamada {}", chamadaAtual)
            throw RuntimeException("Pismo indisponivel (chamada $chamadaAtual)")
        }
        return "200 - bloqueio efetivado com sucesso (chamada $chamadaAtual)"
    }
}