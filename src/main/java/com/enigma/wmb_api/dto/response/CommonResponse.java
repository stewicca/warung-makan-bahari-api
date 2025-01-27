package com.enigma.wmb_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// T -> Type non primitive
// U, V -> pembuatan generic type untuk 2nd, 3rd ...
// E -> Element dari suatu collection
// R -> Return Type
public class CommonResponse<T> {
    private Integer status;
    private String message;
    private T data;
    private PagingResponse paging;
}
