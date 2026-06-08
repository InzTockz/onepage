package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.borradores.PedidoDiarioResponse;
import com.battilana.onepage.entity.BorradoresEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BorradoresMapper {

    List<PedidoDiarioResponse> toListPedidodiarioResponse(List<BorradoresEntity> borradoresEntities);

}
