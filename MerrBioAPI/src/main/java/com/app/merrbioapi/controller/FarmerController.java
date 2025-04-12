package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.response.FarmerResponse;
import com.app.merrbioapi.service.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/farmers")
@RequiredArgsConstructor
@Tag(name = "Farmers", description = "Farmer information APIs")
public class FarmerController {

    private final FarmerService farmerService;

    @Operation(summary = "Get farmers", description = "Retrieve a list of farmers, optionally filtered by farm name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of farmers retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FarmerResponse.class)))),
    })
    @GetMapping
    public ResponseEntity<List<FarmerResponse>> getAllFarmers(
            @Parameter(description = "Optional farm name filter (case-insensitive, partial match)", required = false)
            @RequestParam(required = false) String farmName
    ) {
        List<FarmerResponse> farmers = farmerService.getFarmers(farmName);
        return ResponseEntity.ok(farmers);
    }

}