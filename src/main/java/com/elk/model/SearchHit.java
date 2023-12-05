package com.elk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHit implements Serializable {
    @JsonProperty("index")
    private String index;
    @JsonProperty("id")
    private String id;
    @JsonProperty("score")
    private Double score;
    @JsonProperty("source")
    private Map<String,Object> source;

}
