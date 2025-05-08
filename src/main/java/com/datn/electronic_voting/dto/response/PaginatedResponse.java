package com.datn.electronic_voting.dto.response;

import com.datn.electronic_voting.dto.ElectionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> listElements;
    private int totalPages;
}
