package com.app.merrbioapi.model.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // Important for Lombok with inheritance
public class CustomerRegisterRequest extends BaseRegisterRequest {
}