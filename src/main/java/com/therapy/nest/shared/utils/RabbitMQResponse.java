package com.therapy.nest.shared.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RabbitMQResponse {
    public Boolean status;
    public String error;
    public Object message;
}
