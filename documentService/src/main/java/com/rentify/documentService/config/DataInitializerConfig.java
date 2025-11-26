package com.rentify.documentService.config;

import com.rentify.documentService.model.Estado;
import com.rentify.documentService.model.TipoDocumento;
import com.rentify.documentService.repository.EstadoRepository;
import com.rentify.documentService.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Configuración de inicialización de datos.
 * Puebla automáticamente las tablas con datos iniciales cuando el servicio arranca.
 *
 * IMPORTANTE: Solo se ejecuta si las tablas están vacías (no duplica datos)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializerConfig {

    private final EstadoRepository estadoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    /**
     * Inicializa estados de documentos.
     * Estados: PENDIENTE, ACEPTADO, RECHAZADO, EN_REVISION
     */
    @Bean
    @Order(1)
    public CommandLineRunner initEstados() {
        return args -> {
            log.info("🔄 Verificando estados de documentos...");

            if (estadoRepository.count() == 0) {
                log.info("📝 Creando estados de documentos...");

                Estado pendiente = Estado.builder()
                        .nombre("PENDIENTE")
                        .build();

                Estado aceptado = Estado.builder()
                        .nombre("ACEPTADO")
                        .build();

                Estado rechazado = Estado.builder()
                        .nombre("RECHAZADO")
                        .build();

                Estado enRevision = Estado.builder()
                        .nombre("EN_REVISION")
                        .build();

                estadoRepository.save(pendiente);
                estadoRepository.save(aceptado);
                estadoRepository.save(rechazado);
                estadoRepository.save(enRevision);

                log.info("✅ Estados creados exitosamente:");
                log.info("   - ID 1: PENDIENTE");
                log.info("   - ID 2: ACEPTADO");
                log.info("   - ID 3: RECHAZADO");
                log.info("   - ID 4: EN_REVISION");
            } else {
                log.info("✅ Estados ya existen ({} registros)", estadoRepository.count());
            }
        };
    }

    /**
     * Inicializa tipos de documentos.
     * Tipos: DNI, PASAPORTE, LIQUIDACION_SUELDO, etc.
     */
    @Bean
    @Order(2)
    public CommandLineRunner initTiposDocumento() {
        return args -> {
            log.info("🔄 Verificando tipos de documentos...");

            if (tipoDocumentoRepository.count() == 0) {
                log.info("📝 Creando tipos de documentos...");

                TipoDocumento dni = TipoDocumento.builder()
                        .nombre("DNI")
                        .build();

                TipoDocumento pasaporte = TipoDocumento.builder()
                        .nombre("PASAPORTE")
                        .build();

                TipoDocumento liquidacion = TipoDocumento.builder()
                        .nombre("LIQUIDACION_SUELDO")
                        .build();

                TipoDocumento antecedentes = TipoDocumento.builder()
                        .nombre("CERTIFICADO_ANTECEDENTES")
                        .build();

                TipoDocumento afp = TipoDocumento.builder()
                        .nombre("CERTIFICADO_AFP")
                        .build();

                TipoDocumento contrato = TipoDocumento.builder()
                        .nombre("CONTRATO_TRABAJO")
                        .build();

                tipoDocumentoRepository.save(dni);
                tipoDocumentoRepository.save(pasaporte);
                tipoDocumentoRepository.save(liquidacion);
                tipoDocumentoRepository.save(antecedentes);
                tipoDocumentoRepository.save(afp);
                tipoDocumentoRepository.save(contrato);

                log.info("✅ Tipos de documento creados exitosamente:");
                log.info("   - ID 1: DNI");
                log.info("   - ID 2: PASAPORTE");
                log.info("   - ID 3: LIQUIDACION_SUELDO");
                log.info("   - ID 4: CERTIFICADO_ANTECEDENTES");
                log.info("   - ID 5: CERTIFICADO_AFP");
                log.info("   - ID 6: CONTRATO_TRABAJO");
            } else {
                log.info("✅ Tipos de documento ya existen ({} registros)", tipoDocumentoRepository.count());
            }
        };
    }

    /**
     * Resumen de inicialización.
     */
    @Bean
    @Order(3)
    public CommandLineRunner printInitializationSummary() {
        return args -> {
            log.info("════════════════════════════════════════════════════════");
            log.info("🎉 INICIALIZACIÓN COMPLETADA - DOCUMENT SERVICE");
            log.info("════════════════════════════════════════════════════════");
            log.info("📊 Resumen:");
            log.info("   - Estados: {} registros", estadoRepository.count());
            log.info("   - Tipos de Documento: {} registros", tipoDocumentoRepository.count());
            log.info("════════════════════════════════════════════════════════");
            log.info("🚀 Document Service listo para recibir peticiones");
            log.info("🔗 Puerto: 8083");
            log.info("📖 Swagger UI: http://localhost:8083/swagger-ui/index.html");
            log.info("════════════════════════════════════════════════════════");
        };
    }
}