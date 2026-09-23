package com.battilana.onepage.client;

import com.battilana.onepage.dto.cliente.ClienteClientResponse;
import com.battilana.onepage.dto.cliente.ClienteDeudorClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "sap-service", contextId = "cliente")
public interface ClienteClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v2/clientes")
    List<ClienteClientResponse> listarClientes();

    @RequestMapping(method = RequestMethod.GET, value = "/api/v2/clientes/vendedor/{idVendedor}")
    List<ClienteClientResponse> listarClientePorIdVendedor(@PathVariable Integer idVendedor);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v2/clientes/deudor")
    List<ClienteDeudorClientResponse> buscarClientesDeudores();

    @RequestMapping(method = RequestMethod.GET, value = "/api/v2/clientes/deudor/vendedor")
    List<ClienteDeudorClientResponse> buscarClientesDeudoresPorVendedor(@RequestParam("idVendedor") Integer idVendedor);
}
