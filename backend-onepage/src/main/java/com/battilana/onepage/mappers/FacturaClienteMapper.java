package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarRequest;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.entity.FacturaClienteEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FacturaClienteMapper {

    List<FacturasPorCobrarResponse> toListDto(List<FacturaClienteEntity> facturasPorCobrarEntities);
    FacturasPorCobrarResponse toDto(FacturaClienteEntity facturaClienteEntity);
    @InheritInverseConfiguration
    FacturaClienteEntity toEntity(FacturasPorCobrarRequest facturasPorCobrarRequest);
}
