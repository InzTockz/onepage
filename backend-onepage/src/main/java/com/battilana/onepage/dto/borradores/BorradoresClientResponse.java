package com.battilana.onepage.dto.borradores;

import java.time.LocalDate;

public record BorradoresClientResponse(
        Integer docEntry,
        String objType,
        LocalDate docDate,
        LocalDate createDate,
        String cardCode,
        String cardName,
        Integer slpCode,
        String fullNamesSlp,
        Integer ownerCode,
        String fullNamesOwner,
        String docStatus,
        String wddStatus,
        String comments,
        String docCur,
        Double vatSum,
        Double vatSumFc,
        Double docTotalFc,
        Double docTotal
) {
}
