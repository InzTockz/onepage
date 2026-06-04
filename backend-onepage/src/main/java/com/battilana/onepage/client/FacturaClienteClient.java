package com.battilana.onepage.client;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezClientResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "Factura-cliente-service", url = "${feign.client.base-url}/api/v2/facturas-cliente")
public interface FacturaClienteClient {

    @RequestMapping(method = RequestMethod.GET, value = "/facturas-por-cobrar")
    List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrar();

    @RequestMapping(method = RequestMethod.GET, value = "/facturas-por-cobrar/cliente/{ruc}")
    List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorCliente(@PathVariable String ruc);

    @RequestMapping(method = RequestMethod.GET, value = "/facturas-por-cobrar/vendedor/{slpCode}")
    List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorVendedor(@PathVariable Integer slpCode);

    @RequestMapping(method = RequestMethod.GET, value = "/facturas-por-cobrar/top-diez")
    List<FacturasPorCobrarTopDiezClientResponse> buscarFacturasPorCobrarTopDiez();

    @RequestMapping(method = RequestMethod.GET, value = "/resumen-cartera")
    List<ResumenCarteraClientResponse> resumentCartera();
}
