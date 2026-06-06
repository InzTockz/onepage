package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.borradores.PedidodiarioResponse;
import com.battilana.onepage.entity.BorradoresEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BorradoresMapper {

    List<PedidodiarioResponse> toListPedidodiarioResponse(List<BorradoresEntity> borradoresEntities);

}
