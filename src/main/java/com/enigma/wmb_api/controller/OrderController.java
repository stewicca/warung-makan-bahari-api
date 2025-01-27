package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.*;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.OrderDetailResponse;
import com.enigma.wmb_api.dto.response.OrderResponse;
import com.enigma.wmb_api.service.OrderService;
import com.enigma.wmb_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = Constant.ORDER_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Order", description = "APIs for managing orders, including creating, updating, retrieving, and deleting order details")
public class OrderController {
    private static class CommonResponseOrderResponse extends CommonResponse<OrderResponse> {}
    private static class CommonResponseOrderDetailResponse extends CommonResponse<OrderDetailResponse> {}

    private final OrderService orderService;

    @Operation(summary = "Create draft order",
            description = "This endpoint allows creating a draft order. The order is created in the DRAFT status, awaiting further updates.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Draft order created successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PostMapping("/draft")
    public ResponseEntity<?> createDraftOrder(@RequestBody DraftOrderRequest request) {
        OrderResponse orderResponse = orderService.createDraft(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, Constant.SUCCESS_CREATE_ORDER_DRAFT, orderResponse);
    }

    @Operation(summary = "Get order details",
            description = "Retrieve the details of a specific order by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order details retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderDetailResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @GetMapping("/{orderId}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
        List<OrderDetailResponse> orderDetails = orderService.getOrderDetails(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_GET_ORDER_DETAILS, orderDetails);
    }

    @Operation(summary = "Add order detail",
            description = "Add a new detail to a specific order by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order detail added successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PostMapping("/{orderId}/details")
    public ResponseEntity<?> addOrderDetail(@PathVariable String orderId, @RequestBody OrderDetailRequest request) {
        OrderResponse orderResponse = orderService.addOrderDetail(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_ADD_ORDER_DETAIL, orderResponse);
    }

    @Operation(summary = "Update order detail",
            description = "Update an existing order detail by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order detail updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order or detail not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PutMapping("/{orderId}/details/{detailId}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable String orderId, @PathVariable String detailId, @RequestBody OrderDetailRequest request) {
        OrderResponse orderResponse = orderService.updateOrderDetail(orderId, detailId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_UPDATE_ORDER_DETAIL, orderResponse);
    }

    @Operation(summary = "Remove order detail",
            description = "Remove an order detail by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order detail removed successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order or detail not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @DeleteMapping("/{orderId}/details/{detailId}")
    public ResponseEntity<?> removeOrderDetail(@PathVariable String orderId, @PathVariable String detailId) {
        OrderResponse orderResponse = orderService.removeOrderDetail(orderId, detailId);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_REMOVE_ORDER_DETAIL, orderResponse);
    }

    @Operation(summary = "Update order status",
            description = "Update the status of a specific order by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid status data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderId, @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_UPDATE_ORDER_STATUS, orderResponse);
    }

    @Operation(summary = "Get all orders",
            description = "Retrieve a paginated list of all orders with optional filtering by date and query.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "startDate") String startDate,
            @RequestParam(name = "endDate") String endDate
    ) {
        SearchOrderRequest request = SearchOrderRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .query(query)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Page<OrderResponse> orders = orderService.getAllOrders(request);
        return ResponseUtil.buildResponsePage(HttpStatus.OK, Constant.SUCCESS_GET_ALL_ORDERS, orders);
    }

    @Operation(summary = "Get order by ID",
            description = "Retrieve a specific order by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseOrderResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - invalid signature", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_GET_ORDER_BY_ID, orderService.getOrderById(id));
    }
}