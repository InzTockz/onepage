package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.pago.PagoVigenteResponse;
import com.battilana.onepage.entity.PagoVigenteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PagoVigenteMapper {

    List<PagoVigenteResponse> toPagoVigenteResponseList(List<PagoVigenteEntity> pagoVigenteEntities);

    @Mapping(target = "idBanco", source = "bancoEntity.idBanco")
    PagoVigenteResponse toPagoVigenteResponse(PagoVigenteEntity pagoVigenteEntity);
}
