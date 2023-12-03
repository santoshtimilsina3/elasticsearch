package com.elk.requestresponse;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {
    private int statusCode;
    private String message;
    private Object data;

}
