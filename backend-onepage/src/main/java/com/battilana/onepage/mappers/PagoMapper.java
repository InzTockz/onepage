package com.battilana.onepage.mappers;

import com.battilana.onepage.dto.pago.PagoResponse;
import com.battilana.onepage.entity.PagoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PagoMapper {



    @Mapping(target = "idBanco", source = "bancoEntity.idBanco")
    PagoResponse toPagoResponse(PagoEntity pagoEntity);

    List<PagoResponse> toListPagoResponse(List<PagoEntity> pagoEntityList);
}
