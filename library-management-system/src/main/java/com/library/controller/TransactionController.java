package com.library.controller;

import com.library.dto.IssueRequestDTO;
import com.library.dto.ReturnRequestDTO;
import com.library.dto.TransactionDTO;
import com.library.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "Endpoints for managing book issues and returns")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/issue")
    @Operation(summary = "Issue a book to a member")
    public ResponseEntity<TransactionDTO> issueBook(@Valid @RequestBody IssueRequestDTO request) {
        return new ResponseEntity<>(transactionService.issueBook(request), HttpStatus.CREATED);
    }
    
    @PostMapping("/return")
    @Operation(summary = "Return a book")
    public ResponseEntity<TransactionDTO> returnBook(@Valid @RequestBody ReturnRequestDTO request) {
        return ResponseEntity.ok(transactionService.returnBook(request));
    }
    
    @GetMapping
    @Operation(summary = "Get all transactions with pagination and sorting")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @PageableDefault(size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }
    
    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get transactions by member")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByMember(
            @PathVariable Long memberId,
            @PageableDefault(size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactionsByMember(memberId, pageable));
    }
    
    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get transactions by book")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByBook(
            @PathVariable Long bookId,
            @PageableDefault(size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactionsByBook(bookId, pageable));
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter transactions by status, member, or book")
    public ResponseEntity<Page<TransactionDTO>> filterTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long bookId,
            @PageableDefault(size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.filterTransactions(status, memberId, bookId, pageable));
    }
}