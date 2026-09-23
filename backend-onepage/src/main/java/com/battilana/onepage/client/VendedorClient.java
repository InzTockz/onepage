package com.battilana.onepage.client;

import com.battilana.onepage.dto.vendedor.VendedorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "sap-service", contextId = "vendedor")
public interface VendedorClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v2/vendedores")
    List<VendedorResponse> listaVendedores();
}
