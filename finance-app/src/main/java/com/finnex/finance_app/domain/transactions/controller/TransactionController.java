package com.finnex.finance_app.domain.transactions.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.common.response.PagedResponse;
import com.finnex.finance_app.domain.transactions.dto.request.CreateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.request.UpdateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.service.TransactionService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private  final TransactionService transactionService;

    @GetMapping
    public ApiResponse<PagedResponse<TransactionResponse>> getTransactions(

            @AuthenticationPrincipal User currentUser,

            @PageableDefault(
                    size = 10,
                    sort = "transactionDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        return ApiResponse.ok(
                transactionService.getTransactions(
                        currentUser,
                        pageable
                ),
                "Transactions fetched successfully"
        );
    }

    @PostMapping
    public ApiResponse<TransactionResponse> createTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody CreateTransactionRequest request
            ){
        return ApiResponse.ok(transactionService.createTransaction(user,request),"Transaction created");
    }

    @GetMapping("/{id}")
    public  ApiResponse<TransactionResponse> getTransactionById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
            ){
        return ApiResponse.ok(
                transactionService.getTransactionById(user,id),
                "Transaction fetched successfully"
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<TransactionResponse> updateTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody UpdateTransactionRequest request
    ){
        return ApiResponse.ok(
                transactionService.updateTransaction(user,id,request),
                "Transaction updated Succcessfully"
        );
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ){
        transactionService.deleteTransaction(user,id);
        return  ApiResponse.ok(
                null,
                "Transaction deleted successfully"
        );
    }
}
