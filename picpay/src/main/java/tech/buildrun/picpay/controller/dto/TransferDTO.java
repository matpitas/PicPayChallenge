package tech.buildrun.picpay.controller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransferDTO(@NotNull @DecimalMin("0.01") BigDecimal value,@NotNull Long payer,@NotNull Long payee) {

}
