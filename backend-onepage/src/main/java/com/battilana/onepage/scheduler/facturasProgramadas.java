package com.battilana.onepage.scheduler;

import com.battilana.onepage.service.FacturaClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class facturasProgramadas {

    private final FacturaClienteService facturaClienteService;

    @Scheduled(cron = "0 59 23 L * ?")
    public void registroMensualFacturas(){
        log.info("=== TAREA PROGRAMADA: Registro de facturas ===");
        try {
            facturaClienteService.registrarFacturasDelMes();
            log.info("=== TAREA COMPELTADA EXITOSAMENTE ===");
        } catch (Exception e){
            log.error("=== ERROR EN TAREA PROGRMADA: {}", e.getMessage(), e);
        }
    }
}
