package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.borradores.LotePedidoRequest;
import com.battilana.onepage.dto.borradores.LotePedidosResponse;
import com.battilana.onepage.entity.LotePedidosEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LotePedidoMapper {

    List<LotePedidosResponse> toListResponse(List<LotePedidosEntity> lotePedidoEntities);
    LotePedidosResponse toRegisterResponse(LotePedidosEntity lotePedidosEntity);
    @InheritInverseConfiguration
    LotePedidosEntity toLoteEntity(LotePedidoRequest lotePedidoRequest);
}
