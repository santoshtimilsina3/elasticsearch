package com.elk.requestresponse;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericResponse implements Serializable {
    @JsonProperty("StatusCode")
    private int statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private Object data;

    // Private constructor to be used by the builder
    private GenericResponse(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static class Builder {
        @JsonProperty("StatusCode")
        private int statusCode;
        @JsonProperty("message")
        private String message;
        @JsonProperty("data")
        private Object data;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public GenericResponse build() {
            return new GenericResponse(this.statusCode, this.message, this.data);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
