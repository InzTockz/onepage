package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.borradores.BorradoresRequest;
import com.battilana.onepage.dto.borradores.BorradoresResponse;
import com.battilana.onepage.entity.BorradoresEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BorradoresMapper {

    List<BorradoresResponse> toListPedidodiarioResponse(List<BorradoresEntity> borradoresEntities);
    BorradoresResponse toBorradoresResponse(BorradoresEntity borradoresEntity);
    @InheritInverseConfiguration
    BorradoresEntity toBorradoresEntity(BorradoresRequest borradoresRequest);
}
