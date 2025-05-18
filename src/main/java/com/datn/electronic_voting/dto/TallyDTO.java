package com.datn.electronic_voting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TallyDTO {
    private Long candidateId;
    private int votes;
    private int agree;
    private int disagree;

}
